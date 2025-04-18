package com.davit.chess.model;

public class Move {
    Square from;
    Square to;
    Piece piece;
    private final PieceType promotionType; // nullable

    public Move(Square from, Square to, Piece piece) {
        this(from, to, piece, null);
    }

    public Move(Square from, Square to, Piece piece, PieceType promotionType) {
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.promotionType = promotionType;
    }

    public PieceType getPromotionType() {
        return promotionType;
    }

    public boolean isPromotion() {
        return promotionType != null;
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
