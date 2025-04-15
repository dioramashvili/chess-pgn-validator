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
        if (cleaned.length() < 2) return null;
        String coord = cleaned.substring(cleaned.length() - 2);
        int col = coord.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(coord.charAt(1));
        if (col < 0 || col > 7 || row < 0 || row > 7) return null;
        Square to = new Square(row, col);

        // Determine piece type
        PieceType type = extractPieceType(cleaned);

        // Special handling for pawn captures (e.g., exd5)
        if (type == PieceType.PAWN && isCapture) {
            char fromFile = cleaned.charAt(0); // e.g., 'e' in "exd5"
            int fromCol = fromFile - 'a';

            for (Square from : board.getAllSquaresWithPiecesOfColor(player)) {
                if (from.col() != fromCol) continue;
                Piece piece = board.getPiece(from);
                if (piece.getType() != PieceType.PAWN) continue;

                for (Move move : piece.getLegalMoves(board, from)) {
                    if (move.to().equals(to)) {
                        if (!board.isOccupied(to)) continue;
                        Piece target = board.getPiece(to);
                        if (target.getColor() != player) {
                            return move;
                        }
                    }
                }
            }
            return null; // no matching pawn found
        }

        // All other pieces
        for (Square from : board.getAllSquaresWithPiecesOfColor(player)) {
            Piece piece = board.getPiece(from);
            if (piece.getType() != type) continue;

            List<Move> legalMoves = piece.getLegalMoves(board, from);
            for (Move move : legalMoves) {
                if (move.to().equals(to)) {
                    if (isCapture) {
                        if (!board.isOccupied(to)) continue;
                        Piece target = board.getPiece(to);
                        if (target.getColor() == player) continue; // can't capture own
                    }
                    return move;
                }
            }
        }

        return null; // no valid move matched
    }

    private static Move getCastlingMove(Game game, String san) {
        Board board = game.getBoard();
        Color color = game.getPlayerToMove();
        Square from, to;

        if (color == Color.WHITE) {
            from = new Square(7, 4); // e1
            to = san.equals("O-O") ? new Square(7, 6) : new Square(7, 2); // g1 or c1
        } else {
            from = new Square(0, 4); // e8
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
            default -> PieceType.PAWN; // if no letter, it's a pawn
        };
    }
}
