package com.davit.chess.model;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {


    public Pawn(Color color, PieceType type) {
        super(color, type);
    }

    @Override
    public List<Move> getLegalMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();
        int direction = (getColor() == Color.WHITE) ? -1 : 1;
        int finalRank = (getColor() == Color.WHITE) ? 0 : 7;

        int oneForwardRow = from.row() + direction;
        int twoForwardRow = from.row() + 2 * direction;
        int col = from.col();

        // One step forward
        if (board.isOnBoard(oneForwardRow, col)) {
            Square oneForward = new Square(oneForwardRow, col);
            if (!board.isOccupied(oneForward)) {
                if (oneForwardRow == finalRank) {
                    // Add promotion options
                    for (PieceType promo : List.of(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT)) {
                        moves.add(new Move(from, oneForward, this, promo));
                    }
                } else {
                    moves.add(new Move(from, oneForward, this));
                }

                // Two forward if at starting row
                boolean atStart = (getColor() == Color.WHITE && from.row() == 6)
                        || (getColor() == Color.BLACK && from.row() == 1);
                if (atStart) {
                    Square twoForward = new Square(twoForwardRow, col);
                    if (!board.isOccupied(twoForward)) {
                        moves.add(new Move(from, twoForward, this));
                    }
                }
            }
        }

        // Diagonal captures (including promotion)
        for (int dc : new int[]{-1, 1}) {
            int newCol = col + dc;
            if (board.isOnBoard(oneForwardRow, newCol)) {
                Square diag = new Square(oneForwardRow, newCol);
                if (board.isOccupied(diag)) {
                    Piece target = board.getPiece(diag);
                    if (target.getColor() != this.getColor()) {
                        if (oneForwardRow == finalRank) {
                            for (PieceType promo : List.of(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT)) {
                                moves.add(new Move(from, diag, this, promo));
                            }
                        } else {
                            moves.add(new Move(from, diag, this));
                        }
                    }
                }
            }
        }

        // En Passant
        Square enPassant = board.getEnPassantTarget();
        if (enPassant != null && enPassant.row() == oneForwardRow && Math.abs(enPassant.col() - col) == 1) {
            moves.add(new Move(from, enPassant, this));
        }

        return moves;
    }

    @Override
    public List<Square> getAttackedSquares(Board board, Square from) {
        List<Square> squares = new ArrayList<>();
        int direction = (this.getColor() == Color.WHITE) ? -1 : 1;

        int row = from.row() + direction;
        int colLeft = from.col() - 1;
        int colRight = from.col() + 1;

        if (board.isOnBoard(row, colLeft)) {
            squares.add(new Square(row, colLeft));
        }
        if (board.isOnBoard(row, colRight)) {
            squares.add(new Square(row, colRight));
        }

        return squares;
    }
}
