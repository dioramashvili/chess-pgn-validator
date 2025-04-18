package com.davit.chess.model;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {


    public Knight(Color color, PieceType type) {
        super(color, type);
    }

    @Override
    public List<Move> getLegalMoves(Board board, Square from) {

        List<Move> moves = new ArrayList<>();

        int row = from.row();
        int col = from.col();
        int[][] offsets = {
                {2, 1}, {1, 2}, {-1, 2}, {-2, 1},
                {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
        };

        for (int[] offset : offsets) {
            int newRow = row + offset[0];
            int newCol = col + offset[1];

                if (board.isOnBoard(newRow, newCol)){
                    Square to = new Square(newRow, newCol);
                    if(!board.isOccupied(to) || board.getPiece(to).getColor() != this.getColor()){
                        moves.add(new Move(from, to, this));
                    }
                }
        }

        return moves;
    }

    @Override
    public List<Square> getAttackedSquares(Board board, Square from) {
        List<Square> squares = new ArrayList<>();
        int[][] offsets = {
                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        for (int[] offset : offsets) {
            int row = from.row() + offset[0];
            int col = from.col() + offset[1];
            if (board.isOnBoard(row, col)) {
                squares.add(new Square(row, col));
            }
        }

        return squares;
    }
}