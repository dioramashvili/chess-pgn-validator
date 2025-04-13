package com.davit.chess;

import com.davit.chess.model.*;

public class Game {
    private final Board board;
    private Color playerToMove;

    public Game() {
        this.board = new Board();
        this.playerToMove = Color.WHITE;
    }

    public Board getBoard() {
        return board;
    }

    public Color getPlayerToMove() {
        return playerToMove;
    }

    public void switchTurn() {
        playerToMove = (playerToMove == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    public boolean tryMove(Move move) {
        if (MoveValidator.isMoveLegal(board, move, playerToMove)) {
            board.movePiece(move);
            switchTurn();
            return true;
        }
        return false;
    }
}
