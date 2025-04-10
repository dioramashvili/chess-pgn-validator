package com.davit.chess.model;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    public Rook(Color color, PieceType type) {
        super(color, type);
    }

    @Override
    public List<Move> getLegalMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();

        int[][] directions = {
                {-1, 0},  // Up
                {1, 0},   // Down
                {0, -1},  // Left
                {0, 1}    // Right
        };

        int row = from.row();
        int col = from.col();

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            while (board.isOnBoard(newRow, newCol)) {
                Square to = new Square(newRow, newCol);

                if (!board.isOccupied(to)) {
                    moves.add(new Move(from, to, this));
                } else {
                    Piece target = board.getPiece(to);
                    if (target.getColor() != this.getColor()) {
                        moves.add(new Move(from, to, this));
                    }
                    break; // Stop after hitting any piece
                }

                newRow += dir[0];
                newCol += dir[1];
            }
        }

        return moves;
    }
}
