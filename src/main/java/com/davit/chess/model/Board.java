package com.davit.chess.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private Piece[][] board = new Piece[8][8];
    private Square enPassantTarget;

    public Board() {
        initializeStandardSetup();
    }

    public Board(Board other) {
        this.board = new Piece[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece original = other.board[row][col];
                if (original != null) {
                    this.board[row][col] = clonePiece(original);
                }
            }
        }
    }


    public Square getEnPassantTarget() {
        return enPassantTarget;
    }

    public void setEnPassantTarget(Square square) {
        this.enPassantTarget = square;
    }

    public void clearEnPassantTarget() {
        this.enPassantTarget = null;
    }

    private void initializeStandardSetup() {
        // Place pawns
        for (int col = 0; col < 8; col++) {
            setPiece(new Square(1, col), new Pawn(Color.BLACK, PieceType.PAWN));
            setPiece(new Square(6, col), new Pawn(Color.WHITE, PieceType.PAWN));
        }

        // Define the order of major pieces
        PieceType[] pieceOrder = {
                PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };

        // Place major pieces
        for (int col = 0; col < 8; col++) {
            PieceType type = pieceOrder[col];
            setPiece(new Square(0, col), createPiece(Color.BLACK, type));
            setPiece(new Square(7, col), createPiece(Color.WHITE, type));
        }
    }

    private Piece createPiece(Color color, PieceType type) {
        return switch (type) {
            case PAWN -> new Pawn(color, type);
            case KNIGHT -> new Knight(color, type);
            case BISHOP -> new Bishop(color, type);
            case ROOK -> new Rook(color, type);
            case QUEEN -> new Queen(color, type);
            case KING -> new King(color, type);
        };
    }

    private Piece clonePiece(Piece original) {
        Piece copy = switch (original.getType()) {
            case PAWN -> new Pawn(original.getColor(), PieceType.PAWN);
            case ROOK -> new Rook(original.getColor(), PieceType.ROOK);
            case KNIGHT -> new Knight(original.getColor(), PieceType.KNIGHT);
            case BISHOP -> new Bishop(original.getColor(), PieceType.BISHOP);
            case QUEEN -> new Queen(original.getColor(), PieceType.QUEEN);
            case KING -> new King(original.getColor(), PieceType.KING);
        };
        copy.setHasMoved(original.hasMoved());  // This line is critical
        return copy;
    }


    public Piece getPiece(Square square) {
        return board[square.row()][square.col()];
    }

    public void setPiece(Square square, Piece piece) {
        board[square.row()][square.col()] = piece;
    }

    public boolean isOccupied(Square square) {
        return getPiece(square) != null;
    }

    public boolean isOnBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public boolean isOnBoard(Square square) {
        return isOnBoard(square.row(), square.col());
    }

    public List<Square> getAllSquaresWithPiecesOfColor(Color color) {
        List<Square> squares = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square sq = new Square(row, col);
                Piece p = getPiece(sq);
                if (p != null && p.getColor() == color) {
                    squares.add(sq);
                }
            }
        }
        return squares;
    }

    public boolean isSquareUnderAttack(Square square, Color byColor) {
        for (Square from : getAllSquaresWithPiecesOfColor(byColor)) {
            Piece p = getPiece(from);

            if (p.getType() == PieceType.KING) continue;

            if (p.getType() == PieceType.PAWN) {
                int direction = (p.getColor() == Color.WHITE) ? -1 : 1;
                int row = from.row() + direction;
                int colLeft = from.col() - 1;
                int colRight = from.col() + 1;

                if (isOnBoard(row, colLeft) && square.equals(new Square(row, colLeft))) {
                    return true;
                }
                if (isOnBoard(row, colRight) && square.equals(new Square(row, colRight))) {
                    return true;
                }
            } else {
                List<Square> attacks = p.getAttackedSquares(this, from);
                for (Square s : attacks) {
                    if (s.equals(square)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public void printBoard() {
        for (int row = 0; row < 8; row++) {
            System.out.print(8 - row + " "); // Print rank (8 to 1)
            for (int col = 0; col < 8; col++) {
                Piece p = board[row][col];
                if (p == null) {
                    System.out.print(". ");
                } else {
                    char symbol = switch (p.getType()) {
                        case KING -> 'K';
                        case QUEEN -> 'Q';
                        case ROOK -> 'R';
                        case BISHOP -> 'B';
                        case KNIGHT -> 'N';
                        case PAWN -> 'P';
                    };
                    System.out.print((p.getColor() == Color.WHITE ? Character.toUpperCase(symbol) : Character.toLowerCase(symbol)) + " ");
                }
            }
            System.out.println();
        }
        System.out.println("  a b c d e f g h");
    }

    public void movePiece(Move move) {
        Square from = move.from();
        Square to = move.to();
        Piece piece = getPiece(from);

        // Castling
        if (piece.getType() == PieceType.KING && Math.abs(from.col() - to.col()) == 2) {
            int row = from.row();
            if (to.col() == 6) {
                // Kingside
                Piece rook = getPiece(new Square(row, 7));
                setPiece(new Square(row, 5), rook);
                setPiece(new Square(row, 7), null);
                if (rook != null) rook.setHasMoved(true);
            } else if (to.col() == 2) {
                // Queenside
                Piece rook = getPiece(new Square(row, 0));
                setPiece(new Square(row, 3), rook);
                setPiece(new Square(row, 0), null);
                if (rook != null) rook.setHasMoved(true);
            }
        }

        // En Passant capture
        if (piece.getType() == PieceType.PAWN && to.equals(enPassantTarget) && getPiece(to) == null) {
            int direction = (piece.getColor() == Color.WHITE) ? 1 : -1;
            Square capturedPawnSquare = new Square(to.row() + direction, to.col());
            setPiece(capturedPawnSquare, null); // Remove captured pawn
        }

        // En Passant target setting or clearing
        if (piece.getType() == PieceType.PAWN && Math.abs(to.row() - from.row()) == 2) {
            int enPassantRow = (from.row() + to.row()) / 2;
            Square target = new Square(enPassantRow, from.col());
            this.setEnPassantTarget(target);
        } else {
            this.clearEnPassantTarget();
        }

        if (move.isPromotion()) {
            Piece promoted = switch (move.getPromotionType()) {
                case QUEEN -> new Queen(piece.getColor(), PieceType.QUEEN);
                case ROOK -> new Rook(piece.getColor(), PieceType.ROOK);
                case BISHOP -> new Bishop(piece.getColor(), PieceType.BISHOP);
                case KNIGHT -> new Knight(piece.getColor(), PieceType.KNIGHT);
                default -> piece; // fallback, should not happen
            };
            promoted.setHasMoved(true);
            setPiece(to, promoted);
        } else {
            setPiece(to, piece);
        }
        setPiece(from, null);

        piece.setHasMoved(true);
    }
}
