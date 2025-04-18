package com.davit.chess.util;

import com.davit.chess.Game;
import com.davit.chess.model.Board;
import com.davit.chess.model.Move;

public class ErrorReporter {

    public static void reportSyntaxError(int gameIndex, String token) {
        System.out.printf("""
            ❌ Game #%d contains a syntax error:
               - Invalid move notation: '%s'
               - Reason: Not a valid SAN (Standard Algebraic Notation) move.
               - Tip: Check for typos or use of unsupported notation.
            -------------------------------------
            """, gameIndex, token);
    }

    public static void reportIllegalMove(int gameIndex, int moveNumber, String san, Move move, Game game, Board boardBefore) {
        System.out.println("❌ Game #" + gameIndex + " → Illegal move at #" + moveNumber + ": " + san);

        if (move == null) {
            System.out.println("   - Reason: Move not recognized. Possibly malformed, ambiguous, or uses unsupported notation.");
        } else {
            boolean isCaptureOnEmpty = boardBefore.getPiece(move.to()) == null && san.contains("x");
            boolean isWrongTurn = boardBefore.getPiece(move.from()).getColor() != game.getPlayerToMove();

            if (isCaptureOnEmpty) {
                System.out.println("   - Reason: Capture attempted on empty square. Possibly incorrect x-notation or en passant error.");
            } else if (isWrongTurn) {
                System.out.println("   - Reason: Move made by wrong color.");
            } else {
                System.out.println("   - Reason: The move is illegal according to current rules (e.g. piece blocked or pinned).");
            }
        }

        game.getBoard().printBoard();
    }


}
