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

        // Handle promotion (e.g., e8=Q, exd8=N)
        PieceType promotionType = null;
        int promoIndex = san.indexOf('=');
        if (promoIndex != -1 && promoIndex < san.length() - 1) {
            char promoChar = san.charAt(promoIndex + 1);
            promotionType = switch (promoChar) {
                case 'Q' -> PieceType.QUEEN;
                case 'R' -> PieceType.ROOK;
                case 'B' -> PieceType.BISHOP;
                case 'N' -> PieceType.KNIGHT;
                default -> null;
            };
            san = san.substring(0, promoIndex); // strip =X
        }

        String cleaned = san.replaceAll("[+#]", "");
        boolean isCapture = cleaned.contains("x");

        // Extract target square (last two chars)
        String coord = cleaned.substring(cleaned.length() - 2);
        int col = coord.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(coord.charAt(1));
        Square to = new Square(row, col);

        // Determine piece type
        PieceType type = extractPieceType(cleaned);

        System.out.println(" → Cleaned: " + cleaned);
        System.out.println(" → Piece type: " + type);
        System.out.println(" → Target square: " + to);
        System.out.println(" → Is capture: " + isCapture);
        if (promotionType != null) {
            System.out.println(" → Promotion to: " + promotionType);
        }

        // Extract disambiguation
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
                            Square enPassant = board.getEnPassantTarget();
                            if (to.equals(enPassant)) {
                                return new Move(from, to, piece, promotionType); // en passant + promotion
                            }
                        } else {
                            if (target.getColor() != player) {
                                return new Move(from, to, piece, promotionType);
                            }
                        }
                    }
                }
            }
            return null;
        }

        // All other pieces, including non-capture pawn moves
        for (Square from : board.getAllSquaresWithPiecesOfColor(player)) {
            Piece piece = board.getPiece(from);
            if (piece.getType() != type) continue;

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
                    return (promotionType != null)
                            ? new Move(from, to, piece, promotionType)
                            : move;
                }
            }

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
            from = new Square(7, 4);
            to = san.equals("O-O") ? new Square(7, 6) : new Square(7, 2);
        } else {
            from = new Square(0, 4);
            to = san.equals("O-O") ? new Square(0, 6) : new Square(0, 2);
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
            default -> PieceType.PAWN;
        };
    }
}
