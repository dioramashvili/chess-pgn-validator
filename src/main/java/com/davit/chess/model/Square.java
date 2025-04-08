package com.davit.chess.model;

public record Square(int row, int col) {
    public Square {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            throw new IllegalArgumentException("Invalid board coordinates");
        }
    }

    public static Square fromAlgebraic(String notation) {
        if (notation == null || notation.length() != 2) {
            throw new IllegalArgumentException("invalid algebraic notation" + notation);
        }
        char fileChar = notation.charAt(0);
        char rankChar = notation.charAt(1);

        int col = fileChar - 'a';
        int row = 8 - Character.getNumericValue(rankChar);

        return new Square(row, col);
    }

    public String toAlgebraic() {
        char fileChar = (char) ('a' + col);
        int rank = 8 - row;
        return "" + fileChar + rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Square)) return false;
        Square square = (Square) o;
        return row == square.row && col == square.col;
    }

    @Override
    public String toString() {
        return toAlgebraic();
    }
}
