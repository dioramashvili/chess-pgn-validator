package com.davit.chess.model;

import java.util.List;

public abstract class Piece {
    protected Color color;
    protected PieceType type;

    public Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
    }

    public Color getColor() {
        return color;
    }

    public PieceType getType() {
        return type;
    }

    // Important: abstract method for move validation
    public abstract List<Move> getLegalMoves(Board board, Square from);
}
