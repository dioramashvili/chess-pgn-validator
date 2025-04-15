package com.davit.chess.parser;

import com.davit.chess.Game;
import com.davit.chess.model.*;

import java.util.List;

public class SANInterpreter {
    public static Move toMove(String san, Game game) {
        if (san.equals("O-O") || san.equals("O-O-O")) {
            return getCastlingMove(game, san);
        }

        Board board = game.getBoard();
        Color player = game.getPlayerToMove();

        // Strip check/mate markers
        String cleaned = san.replaceAll("[+#]", "");

        // Determine if it's a capture
        boolean isCapture = cleaned.contains("x");

        // Extract destination square (last two characters)
        String coord = cleaned.substring(cleaned.length() - 2);
        int col = coord.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(coord.charAt(1));
        Square to = new Square(row, col);

        // Determine piece type
        PieceType type = extractPieceType(cleaned);

        // Search all pieces of that type belonging to player
        for (Square from : board.getAllSquaresWithPiecesOfColor(player)) {
            Piece piece = board.getPiece(from);
            if (piece.getType() != type) continue;

            List<Move> legalMoves = piece.getLegalMoves(board, from);
            for (Move move : legalMoves) {
                if (move.to().equals(to)) {
                    return move; // Found valid move
                }
            }
        }

        // No legal move found
        return null;
    }

    private static Move getCastlingMove(Game game, String san) {
        Board board = game.getBoard();
        Color color = game.getPlayerToMove();
        Square from, to;

        if (color == Color.WHITE){
            from = new Square(7, 4); // White king starts at e1
            to = san.equals("O-O") ? new Square(7, 6) : new Square(7, 2); // g1 or c1
        } else {
            from = new Square(0, 4); // Black king starts at e8
            to = san.equals("O-O") ? new Square(0, 6) : new Square(0, 2); // g8 or c8
        }

        Piece king = board.getPiece(from);
        if (king == null || king.getType() != PieceType.KING) return null;

        List<Move> legalMoves = king.getLegalMoves(board, from);
        for (Move move : legalMoves) {
            if (move.to().equals(to)) {
                return move;
            }
        }

        return null;
    }

    private static PieceType extractPieceType(String san) {
        char c = san.charAt(0);
        return switch (c) {
            case 'N' -> PieceType.KNIGHT;
            case 'B' -> PieceType.BISHOP;
            case 'R' -> PieceType.ROOK;
            case 'Q' -> PieceType.QUEEN;
            case 'K' -> PieceType.KING;
            default -> PieceType.PAWN; // if not a piece letter, assume it's a pawn
        };
    }
}