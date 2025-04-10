package com.davit.chess.model;

public class Board {
    private final Piece[][] board = new Piece[8][8];

    public Board() {
//        initializeStandardSetup();
    }

//    private void initializeStandardSetup(){
//
//    }

    public Piece getPiece(Square square){
        return board[square.row()][square.col()];
    }

    public boolean isOccupied(Square square){
        return getPiece(square) != null;
    }

    public boolean isOnBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public boolean isOnBoard(Square square) {
        return isOnBoard(square.row(), square.col());
    }
}
