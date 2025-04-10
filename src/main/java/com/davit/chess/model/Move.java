package com.davit.chess.model;

public class Move {
    Square from;
    Square to;
    Piece piece;

    public Move(Square from, Square to, Piece piece) {
        this.from = from;
        this.to = to;
        this.piece = piece;
    }

    public Square from() {
        return from;
    }

    public Square to() {
        return to;
    }

    public Piece getPiece() {
        return piece;
    }
}
