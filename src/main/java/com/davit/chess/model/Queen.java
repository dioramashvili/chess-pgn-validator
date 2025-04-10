package com.davit.chess.model;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(Color color, PieceType type) {
        super(color, type);
    }

    @Override
    public List<Move> getLegalMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();

        int[][] directions = {
                {-1, 0},  // Up
                {1, 0},   // Down
                {0, -1},  // Left
                {0, 1},    // Right
                {-1, -1},  // Up-Left
                {-1,  1},  // Up-Right
                { 1, -1},  // Down-Left
                { 1,  1}   // Down-Right

        };

        int row = from.row();
        int col = from.col();

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            while (board.isOnBoard(newRow, newCol)) {
                Square to = new Square(newRow, newCol);

                if (!board.isOccupied(to)) {
                    // Add move and keep sliding
                    moves.add(new Move(from, to, this));
                } else {
                    // If it's an opponent piece, capture and stop
                    Piece target = board.getPiece(to);
                    if (target.getColor() != this.getColor()) {
                        moves.add(new Move(from, to, this));
                    }
                    break;
                }

                // Move one step further in this direction
                newRow += dir[0];
                newCol += dir[1];
            }
        }

        return moves;
    }
}
