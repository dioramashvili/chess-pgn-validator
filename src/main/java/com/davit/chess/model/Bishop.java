package com.davit.chess.model;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
    public Bishop(Color color, PieceType type) {
        super(color, type);
    }

    @Override
    public List<Move> getLegalMoves(Board board, Square from) {

        int[][] directions = {
                {-1, -1},  // Up-Left
                {-1,  1},  // Up-Right
                { 1, -1},  // Down-Left
                { 1,  1}   // Down-Right
        };

        return generateSlidingMoves(board, from, directions);
    }

    @Override
    public List<Square> getAttackedSquares(Board board, Square from) {
        int[][] directions = {
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };
        return extractAttackedSquaresFromSliding(board, from, directions);
    }
}
