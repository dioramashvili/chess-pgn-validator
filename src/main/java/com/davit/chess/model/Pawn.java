package com.davit.chess.model;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {


    public Pawn(Color color, PieceType type) {
        super(color, type);
    }

    @Override
    public List<Move> getLegalMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();
        int direction = (getColor() == Color.WHITE) ? -1 : 1; // Define the direction of the movement

        int oneForwardRow = from.row() + direction;
        int twoForwardRow = from.row() + 2 * direction;
        int col = from.col();

        // One Forward
        if (board.isOnBoard(oneForwardRow, col)) {
            Square oneForward = new Square(oneForwardRow, col);
            if (!board.isOccupied(oneForward)) {
                moves.add(new Move(from, oneForward, this));
            }

            // Two forward at starting row
            if (!hasMoved && board.isOnBoard(twoForwardRow, col)) {
                Square twoForward = new Square(twoForwardRow, col);
                if (!board.isOccupied(twoForward)) {
                    moves.add(new Move(from, twoForward, this));
                }
            }
        }

        // Left Diagonal Capture
        int leftCol = col - 1;
        if (board.isOnBoard(oneForwardRow, leftCol)) {
            Square leftCapture = new Square(oneForwardRow, leftCol);
            if (board.isOccupied(leftCapture)) {
                Piece target = board.getPiece(leftCapture);
                if (target.color != this.color) {
                    moves.add(new Move(from, leftCapture, this));
                }
            }
        }

        // Right Diagonal Capture
        int rightCol = col + 1;
        if (board.isOnBoard(oneForwardRow, rightCol)) {
            Square rightCapture = new Square(oneForwardRow, rightCol);
            if (board.isOccupied(rightCapture)) {
                Piece target = board.getPiece(rightCapture);
                if (target.color != this.color) {
                    moves.add(new Move(from, rightCapture, this));
                }
            }
        }

        return moves;
    }
}
