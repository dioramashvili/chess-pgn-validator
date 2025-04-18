package com.davit.chess.parser;

import com.davit.chess.Game;
import com.davit.chess.model.*;

import java.util.List;

public class SANInterpreter {

    public static Move toMove(String san, Game game) {
        System.out.println("🔍 Parsing SAN: " + san);

        if (san.equals("O-O") || san.equals("O-O-O")) {
            return getCastlingMove(game, san);
        }

        Board board = game.getBoard();
        Color player = game.getPlayerToMove();

        String cleaned = san.replaceAll("[+#]", "");
        boolean isCapture = cleaned.contains("x");

        // Extract target square (last two chars)
        String coord = cleaned.substring(cleaned.length() - 2);
        int col = coord.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(coord.charAt(1));
        Square to = new Square(row, col);

        // Determine piece type
        PieceType type = extractPieceType(cleaned);

        // Log parsed info
        System.out.println(" → Cleaned: " + cleaned);
        System.out.println(" → Piece type: " + type);
        System.out.println(" → Target square: " + to);
        System.out.println(" → Is capture: " + isCapture);

        // Extract disambiguation (but only for non-pawns)
        String disambiguation = null;
        int offset = isCapture ? 1 : 0;
        if (type != PieceType.PAWN && cleaned.length() > (3 + offset)) {
            char c = cleaned.charAt(1);
            if (c != 'x') {
                if (Character.isLetter(c)) {
                    disambiguation = String.valueOf(c);
                } else if (Character.isDigit(c)) {
                    disambiguation = String.valueOf(c);
                }
            }
        }

        System.out.println(" → Disambiguation: " + disambiguation);

        // Special case: pawn captures like exd5
        if (type == PieceType.PAWN && isCapture) {
            char sourceFile = cleaned.charAt(0);
            int fromCol = sourceFile - 'a';
            for (Square from : board.getAllSquaresWithPiecesOfColor(player)) {
                if (from.col() != fromCol) continue;

                Piece piece = board.getPiece(from);
                if (piece.getType() != PieceType.PAWN) continue;

                for (Move move : piece.getLegalMoves(board, from)) {
                    if (move.to().equals(to)) {
                        Piece target = board.getPiece(to);
                        if (target == null) {
                            // Check for en passant capture
                            Square enPassant = board.getEnPassantTarget();

                            if (to.equals(enPassant)) {
                                return move; // valid en passant capture
                            }
                        } else {
                            if (target.getColor() != player) {
                                return move; // valid normal capture
                            }
                        }
                    }
                }
            }
            return null;
        }

        // All other pieces, including pawn non-captures like a4
        for (Square from : board.getAllSquaresWithPiecesOfColor(player)) {
            Piece piece = board.getPiece(from);
            if (piece.getType() != type) continue;

            // Apply disambiguation filter if present
            if (disambiguation != null) {
                char dis = disambiguation.charAt(0);
                if (Character.isLetter(dis)) {
                    int disCol = dis - 'a';
                    if (from.col() != disCol) continue;
                } else if (Character.isDigit(dis)) {
                    int disRow = 8 - Character.getNumericValue(dis);
                    if (from.row() != disRow) continue;
                }
            }

            List<Move> legalMoves = piece.getLegalMoves(board, from);
            for (Move move : legalMoves) {
                if (move.to().equals(to)) {
                    if (isCapture) {
                        if (!board.isOccupied(to)) continue;
                        Piece target = board.getPiece(to);
                        if (target.getColor() == player) continue;
                    }
                    System.out.println("✅ Matched: " + from + " → " + to);
                    return move;
                }
            }

            // Print legal moves even if no match
            System.out.println("🔹 " + piece.getType() + " at " + from + " legal moves: ");
            for (Move m : legalMoves) {
                System.out.println("   → " + m.from() + " → " + m.to());
            }
        }

        System.out.println("❌ No matching move found for SAN: " + san);
        return null;
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
