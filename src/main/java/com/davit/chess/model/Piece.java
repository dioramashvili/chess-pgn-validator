package com.davit.chess.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
    protected Color color;
    protected PieceType type;
    protected boolean hasMoved;

    public Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
        this.hasMoved = false;
    }

    public Color getColor() {
        return color;
    }

    public PieceType getType() {
        return type;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    // Important: abstract method for move validation
    public abstract List<Move> getLegalMoves(Board board, Square from);

    // Inside Piece.java (make it protected so subclasses can use it)
    protected List<Move> generateSlidingMoves(Board board, Square from, int[][] directions) {
        List<Move> moves = new ArrayList<>();
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
                    break;
                }

                newRow += dir[0];
                newCol += dir[1];
            }
        }

        return moves;
    }

}
