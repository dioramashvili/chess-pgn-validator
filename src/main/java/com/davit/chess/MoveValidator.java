package com.davit.chess;

import com.davit.chess.model.*;

import java.util.List;

public class MoveValidator {
    public static boolean isMoveLegal(Board board, Move move, Color playerToMove) {
        Square from = move.from();
        Square to = move.to();

        // First, let's check that there is a piece at the 'from' square
        Piece piece = board.getPiece(from);
        if (piece == null) return false;

        // Second, let's validate that correct color is moving the piece by order
        if (piece.getColor() != playerToMove) return false;

        // Then, check if the move in legal moves for the piece
        List<Move> moves = piece.getLegalMoves(board, from);

        for (Move legal : moves) {
            if (legal.to().equals(to)) {
                return true; // Legal
            }
        }
        return false; // Not legal
    }
}
