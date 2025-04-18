package com.davit.chess.model;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    public Rook(Color color, PieceType type) {
        super(color, type);
    }

    @Override
    public List<Move> getLegalMoves(Board board, Square from) {

        int[][] directions = {
                {-1, 0},  // Up
                {1, 0},   // Down
                {0, -1},  // Left
                {0, 1}    // Right
        };

        return generateSlidingMoves(board, from, directions);
    }

    @Override
    public List<Square> getAttackedSquares(Board board, Square from) {
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}
        };
        return extractAttackedSquaresFromSliding(board, from, directions);
    }
}
