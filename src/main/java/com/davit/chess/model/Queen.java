package com.davit.chess.model;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(Color color, PieceType type) {
        super(color, type);
    }

    @Override
    public List<Move> getLegalMoves(Board board, Square from) {

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

        return generateSlidingMoves(board, from, directions);

    }
}
