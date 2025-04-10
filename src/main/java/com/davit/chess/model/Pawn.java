package com.davit.chess.model;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    boolean hasMoved;

    public Pawn(Color color, PieceType type) {
        super(color, type);
        hasMoved = false;
    }

    @Override
    public List<Move> getLegalMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();
        int direction = (getColor() == Color.WHITE) ? -1 : 1; // Define the direction of the movement

        //Initialize possible squares for pawn
        Square oneForward = new Square(from.row() + direction, from.col());
        Square twoForward = new Square(from.row() + 2 * direction, from.col());
        Square leftCapture = new Square(from.row() + direction, from.col() - 1);
        Square rightCapture = new Square(from.row() + direction, from.col() + 1);

        if (!board.isOccupied(oneForward)) {
            moves.add(new Move(from, oneForward, this));
            if (!hasMoved && !board.isOccupied(twoForward)){
                moves.add(new Move(from, twoForward, this));
            }
        }

        if (board.getPiece(leftCapture).color != this.color || !board.isOccupied(leftCapture)){
            moves.add(new Move(from, leftCapture, this));
        }
        if (board.getPiece(rightCapture).color != this.color || !board.isOccupied(rightCapture)){
            moves.add(new Move(from, rightCapture, this));
        }

        return moves;
    }
}
