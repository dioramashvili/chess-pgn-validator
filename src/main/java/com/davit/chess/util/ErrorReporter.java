package com.davit.chess.util;

public class ErrorReporter {

    public static void reportSyntaxError(int gameIndex, int moveIndex, String san, String message) {
        System.out.println("\u274C Syntax Error in Game #" + gameIndex + ", Move #" + moveIndex + ": " + san);
        System.out.println("   - Reason: " + message);
    }

    public static void reportIllegalMove(int gameIndex, int moveIndex, String san, boolean moveIsNull, String message) {
        System.out.println("\u274C Illegal Move in Game #" + gameIndex + ", Move #" + moveIndex + ": " + san);
        if (moveIsNull) {
            System.out.println("   - Reason: Move not recognized. Possibly malformed, ambiguous, or uses unsupported notation.");
        } else {
            System.out.println("   - Reason: " + message);
        }
    }
}
