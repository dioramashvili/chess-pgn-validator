package com.davit.chess.model;

import java.util.ArrayList;
import java.util.List;

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

    public List<Square> getAllSquaresWithPiecesOfColor(Color color){
        List <Square> squares = new ArrayList<>();
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                Square sq = new Square(row, col);
                Piece p = getPiece(sq);
                if(p != null && p.getColor() == color){
                    squares.add(sq);
                }
            }
        }
        return squares;
    }

    public boolean isSquareUnderAttack(Square square, Color byColor){
        for (Square from : getAllSquaresWithPiecesOfColor(byColor)){
            Piece p = getPiece(from);

            if (p != null){
                List<Move> attacks = p.getLegalMoves(this, from);

                for (Move m  : attacks){
                    if (m.to().equals(square)){
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
