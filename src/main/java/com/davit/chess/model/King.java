package com.davit.chess.model;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece{

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

        for (int[] dir : directions) {
            int newRow = from.row() + dir[0];
            int newCol = from.col() + dir[1];

            if(board.isOnBoard(newRow, newCol)){
                Square to = new Square(newRow, newCol);
                Piece target = board.getPiece(to);
                if(target == null || target.getColor() == enemyColor){
                    if(!board.isSquareUnderAttack(to, enemyColor)){
                        moves.add(new Move(from, to, this));
                    }
                }
            }
        }

        return moves;
    }
}
