package com.davit.chess.parser;

import com.davit.chess.model.Game;
import com.davit.chess.model.GameState;
import com.davit.chess.model.*;

import java.util.List;

public class SANInterpreter {

    private static final boolean DEBUG = false;
    private static int debugMoveCounter = 1;

    public static void resetDebugCounter() {
        debugMoveCounter = 1;
    }

    public static Move toMove(String san, Game game) {
        if (DEBUG) {
            System.out.println("🔍 Move #" + debugMoveCounter + " by " + game.getPlayerToMove() + ": " + san);
            if (game.getPlayerToMove() == Color.WHITE)
                debugMoveCounter++;
        }

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
            if (promotionType == null) {
                System.out.println("❌ Invalid promotion piece: " + promoChar);
                return null;
            }
            san = san.substring(0, promoIndex); // Remove promotion part for rest of parsing
        }

        boolean isCheck = san.contains("+");
        boolean isMate = san.contains("#");
        String cleaned = san.replaceAll("[+#]", "");
        boolean isCapture = cleaned.contains("x");

        String coord = cleaned.substring(cleaned.length() - 2);
        int col = coord.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(coord.charAt(1));
        Square to = new Square(row, col);

        PieceType type = extractPieceType(cleaned);

        if (DEBUG) {
            System.out.println(" → Cleaned: " + cleaned);
            System.out.println(" → Piece type: " + type);
            System.out.println(" → Target square: " + to);
            System.out.println(" → Is capture: " + isCapture);
            if (promotionType != null) {
                System.out.println(" → Promotion to: " + promotionType);
            }
        }

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

        if (DEBUG) System.out.println(" → Disambiguation: " + disambiguation);

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
                                return new Move(from, to, piece, promotionType);
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

                    if (isCheck || isMate) {
                        Board simulatedBoard = new Board(board);
                        Game simulatedGame = new Game(simulatedBoard, player);
                        simulatedGame.tryMove(move);
                        Color opponent = (player == Color.WHITE) ? Color.BLACK : Color.WHITE;

                        if (isCheck && !simulatedGame.isInCheck(opponent)) {
                            System.out.println("⚠️  Logical error: SAN token '" + san + "' declares check ('+'), but opponent king is not in check.");
                        }

                        if (isMate && simulatedGame.getGameState() != GameState.CHECKMATE) {
                            System.out.println("⚠️  Logical error: SAN token '" + san + "' declares mate ('#'), but the game is not in checkmate.");
                        }
                    }

                    if (DEBUG) System.out.println("✅ Matched: " + from + " → " + to);
                    return (promotionType != null)
                            ? new Move(from, to, piece, promotionType)
                            : move;
                }
            }

            if (DEBUG) {
                System.out.println("🔹 " + piece.getType() + " at " + from + " legal moves: ");
                for (Move m : legalMoves) {
                    System.out.println("   → " + m.from() + " → " + m.to());
                }
            }
        }

        if (DEBUG) System.out.println("❌ No matching move found for SAN: " + san);
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