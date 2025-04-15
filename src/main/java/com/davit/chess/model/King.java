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
                {-1, 0},  // Up
                {1, 0},   // Down
                {0, -1},  // Left
                {0, 1},   // Right
                {-1, -1}, // Up-Left
                {-1, 1},  // Up-Right
                {1, -1},  // Down-Left
                {1, 1}    // Down-Right
        };

        Color enemyColor = (this.getColor() == Color.WHITE) ? Color.BLACK : Color.WHITE;

        int row = from.row();
        int col = from.col();

        // Normal kind moves
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

        // Castling logic
        if (!this.hasMoved && !board.isSquareUnderAttack(from, enemyColor)) {
            // Kingside
            if (canCastle(board, from, true, enemyColor)) {
                Square to = new Square(row, 6);
                moves.add(new Move(from, to, this));
            }
            // Queenside
            if (canCastle(board, from, false, enemyColor)) {
                Square to = new Square(row, 2);
                moves.add(new Move(from, to, this));
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

        // Check that destination square is not under attack
        if (board.isSquareUnderAttack(new Square(row, castlingTargetCol), enemyColor)) {
            return false;
        }

        return true;
    }
}

