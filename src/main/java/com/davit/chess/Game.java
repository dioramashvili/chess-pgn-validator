package com.davit.chess;

import com.davit.chess.model.*;

import java.util.List;

public class Game {
    private final Board board;
    private Color playerToMove;
    private GameState gameState = GameState.ONGOING;


    public Game() {
        this.board = new Board();
        this.playerToMove = Color.WHITE;
    }

    public Game(Board board, Color color) {
        this.board = board;
        this.playerToMove = color;
    }
    public Board getBoard() {
        return board;
    }

    public Color getPlayerToMove() {
        return playerToMove;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void switchTurn() {
        playerToMove = (playerToMove == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    public boolean tryMove(Move move) {
        if (MoveValidator.isMoveLegal(board, move, playerToMove)) {
            board.movePiece(move);
            switchTurn();
            if(isInCheck(playerToMove)){
                if(!hasAnyLegalMoves(playerToMove)){
                    gameState = GameState.CHECKMATE;
                } else {
                    gameState = GameState.CHECK;
                }
            } else if (!hasAnyLegalMoves(playerToMove)) {
                gameState = GameState.STALEMATE;
            } else {
                gameState = GameState.ONGOING;
            }
            return true;
        }
        return false;
    }

    public boolean isInCheck(Color color){
        Square kingSquare = findKingSquare(color);
        if (kingSquare == null) return false;
        Color opponent = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
        return board.isSquareUnderAttack(kingSquare, opponent);
    }

    public Square findKingSquare(Color color) {
        for (Square square : board.getAllSquaresWithPiecesOfColor(color)) {
            Piece p = board.getPiece(square);
            if (p != null && p.getType() == PieceType.KING) {
                return square;
            }
        }
        return null;
    }

    public boolean hasAnyLegalMoves(Color color){
        for (Square from : board.getAllSquaresWithPiecesOfColor(color)){
            Piece piece = board.getPiece(from);
            List<Move> moves = piece.getLegalMoves(board, from);

            for (Move move : moves){
                // Simulate the move on copy of board
                Board simulated = new Board(board);
                simulated.movePiece(move);

                Game simgame = new Game(simulated, color);
                if (!simgame.isInCheck(color)){
                    return true; // Found legal move
                }
            }
        }
        return false;
    }
}
