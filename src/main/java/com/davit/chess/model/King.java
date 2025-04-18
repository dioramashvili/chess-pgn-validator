package com.davit.chess.model;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(Color color, PieceType type) {
        super(color, type);
    }

    @Override
    public List<Move> getLegalMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();

        int[][] directions = {
                {-1, 0},  {1, 0},  {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        Color myColor = this.getColor();
        Color enemyColor = (myColor == Color.WHITE) ? Color.BLACK : Color.WHITE;

        int row = from.row();
        int col = from.col();

        // Standard king moves
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (board.isOnBoard(newRow, newCol)) {
                Square to = new Square(newRow, newCol);
                Piece target = board.getPiece(to);
                if (target == null || target.getColor() == enemyColor) {
                    if (!board.isSquareUnderAttack(to, enemyColor)) {
                        moves.add(new Move(from, to, this));
                    }
                }
            }
        }

        // Castling logic (only if king is not in check and hasn't moved)
        if (!this.hasMoved && !board.isSquareUnderAttack(from, enemyColor)) {
            // Kingside castling
            if (canCastle(board, from, true, enemyColor)) {
                moves.add(new Move(from, new Square(row, 6), this));
            }
            // Queenside castling
            if (canCastle(board, from, false, enemyColor)) {
                moves.add(new Move(from, new Square(row, 2), this));
            }
        }

        return moves;
    }


    private boolean canCastle(Board board, Square kingSquare, boolean kingside, Color enemyColor) {
        int row = kingSquare.row();
        int kingCol = kingSquare.col();
        int rookCol = kingside ? 7 : 0;
        int step = kingside ? 1 : -1;
        int[] colsBetween = kingside ? new int[]{5, 6} : new int[]{1, 2, 3};
        int castlingTargetCol = kingside ? 6 : 2;

        Piece rook = board.getPiece(new Square(row, rookCol));
        if (rook == null || rook.getType() != PieceType.ROOK || rook.getColor() != this.getColor() || rook.hasMoved) {
            return false;
        }

        // Path must be clear
        for (int col : colsBetween) {
            if (board.isOccupied(new Square(row, col))) {
                return false;
            }
        }

        // Squares king passes through must not be under attack
        for (int i = 1; i <= 2; i++) {
            Square passing = new Square(row, kingCol + i * step);
            if (board.isSquareUnderAttack(passing, enemyColor)) {
                return false;
            }
        }

        // Final square also must not be under attack
        if (board.isSquareUnderAttack(new Square(row, castlingTargetCol), enemyColor)) {
            return false;
        }

        return true;
    }

    @Override
    public List<Square> getAttackedSquares(Board board, Square from) {
        List<Square> squares = new ArrayList<>();
        int[][] directions = {
                {-1, 0},  {1, 0},  {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] dir : directions) {
            int row = from.row() + dir[0];
            int col = from.col() + dir[1];
            if (board.isOnBoard(row, col)) {
                squares.add(new Square(row, col));
            }
        }

        return squares;
    }
}

