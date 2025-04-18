package com.davit.chess.model;

import com.davit.chess.Game;
import com.davit.chess.parser.PGNParseResult;
import com.davit.chess.parser.PGNParser;
import com.davit.chess.parser.SANInterpreter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InvalidMoveTests {

    @Test
    public void testInvalidSyntax_MoveFormat() {
        String pgn = "[Event \"SyntaxError\"]\n1. e4 e5 2. badMove Nc6 3. Bb5 a6";
        PGNParseResult result = PGNParser.extractMoves(pgn);

        // This should catch 'badMove'
        assertFalse(result.errors().isEmpty(), "Expected syntax error for malformed move format");
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("badMove")), "Error message should include 'badMove'");
    }


    @Test
    public void testInvalidSyntax_IllegalCharacter() {
        String badPGN = "1. Q@4";
        PGNParseResult result = PGNParser.extractMoves(badPGN);
        assertFalse(result.errors().isEmpty());
    }

    @Test
    public void testInvalidSyntax_CastlingTypo() {
        String badPGN = "1. O-0-0"; // wrong character for castling
        PGNParseResult result = PGNParser.extractMoves(badPGN);
        assertFalse(result.errors().isEmpty());
    }

    @Test
    public void testInvalidSyntax_InvalidSquare() {
        String badPGN = "1. e9";
        PGNParseResult result = PGNParser.extractMoves(badPGN);
        assertFalse(result.errors().isEmpty());
    }

    @Test
    public void testIllegalMove_TwoSquarePushNotFromStart() {
        Board board = new Board();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board.setPiece(new Square(r, c), null);

        Pawn whitePawn = new Pawn(Color.WHITE, PieceType.PAWN);
        board.setPiece(new Square(4, 4), whitePawn); // e4

        Game game = new Game(board, Color.WHITE);
        Move illegalDoublePush = new Move(new Square(4, 4), new Square(2, 4), whitePawn);

        assertFalse(game.tryMove(illegalDoublePush), "Pawn should not move two squares unless from starting position");
    }

    @Test
    public void testIllegalMove_KnightCannotReach() {
        Game game = new Game();
        Move illegalMove = SANInterpreter.toMove("Nc6", game); // no knight can jump to c6 for white
        assertFalse(game.tryMove(illegalMove));
    }

    @Test
    public void testIllegalMove_CaptureOnEmptySquare() {
        Game game = new Game();
        game.tryMove(SANInterpreter.toMove("e4", game));
        game.tryMove(SANInterpreter.toMove("e5", game));
        Move illegalCapture = SANInterpreter.toMove("Qxf7", game);
        assertFalse(game.tryMove(illegalCapture));
    }

    @Test
    public void testIllegalMove_BlockedCastling() {
        Game game = new Game();
        game.tryMove(SANInterpreter.toMove("e4", game)); // just move something
        Move illegalCastle = SANInterpreter.toMove("O-O-O", game);
        assertFalse(game.tryMove(illegalCastle));
    }

    @Test
    public void testIllegalMove_InvalidEnPassant() {
        Board board = new Board();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board.setPiece(new Square(r, c), null);

        Pawn white = new Pawn(Color.WHITE, PieceType.PAWN);
        Pawn black = new Pawn(Color.BLACK, PieceType.PAWN);

        board.setPiece(new Square(4, 4), white); // e5
        board.setPiece(new Square(4, 3), black); // d5 (NOT after 2-step move)
        // No en passant target set

        Game game = new Game(board, Color.WHITE);
        Move attempt = new Move(new Square(4, 4), new Square(3, 3), white);

        assertFalse(game.tryMove(attempt), "En passant capture should fail if not allowed");
    }

    @Test
    public void testCheckNotationWithoutCheck() {
        Game game = new Game();
        game.tryMove(SANInterpreter.toMove("e4", game));
        game.tryMove(SANInterpreter.toMove("e5", game));
        Move falseCheck = SANInterpreter.toMove("Qh5+", game); // Qh5+ but no check
        assertNotNull(falseCheck);
        assertTrue(game.tryMove(falseCheck)); // move itself may be legal
    }

    @Test
    public void testInvalidPromotionSymbol() {
        String pgn = "[Event \"BadPromotion\"]\n1. e3 e5 2. e8=X e6"; // Invalid promotion symbol
        PGNParseResult result = PGNParser.extractMoves(pgn);

        Game game = new Game();
        boolean failed = false;

        for (String san : result.moves()) {
            Move move = SANInterpreter.toMove(san, game);
            if (move == null || !game.tryMove(move)) {
                failed = true;
                break;
            }
        }

        assertTrue(failed, "Invalid promotion symbol should make the move fail");
    }

    @Test
    public void testInvalidPromotionToKing() {
        String pgn = "1. a8=K";
        PGNParseResult result = PGNParser.extractMoves(pgn);
        assertFalse(result.errors().isEmpty());
    }

    @Test
    public void testInvalidRookMove() {
        Board board = new Board();
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) board.setPiece(new Square(r, c), null);
        board.setPiece(new Square(4, 4), new Rook(Color.WHITE, PieceType.ROOK));
        Square from = new Square(4, 4);
        Piece rook = board.getPiece(from);
        List<Move> moves = rook.getLegalMoves(board, from);
        assertFalse(moves.stream().anyMatch(m -> m.to().equals(new Square(5, 5))));
    }

    @Test
    public void testInvalidKnightMove() {
        Board board = new Board();
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) board.setPiece(new Square(r, c), null);
        board.setPiece(new Square(4, 4), new Knight(Color.WHITE, PieceType.KNIGHT));
        Square from = new Square(4, 4);
        Piece knight = board.getPiece(from);
        List<Move> moves = knight.getLegalMoves(board, from);
        assertFalse(moves.stream().anyMatch(m -> m.to().equals(new Square(5, 5))));
    }

    @Test
    public void testPawnInvalidLeap() {
        Board board = new Board();
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) board.setPiece(new Square(r, c), null);
        board.setPiece(new Square(6, 0), new Pawn(Color.WHITE, PieceType.PAWN));
        Square from = new Square(6, 0);
        Piece pawn = board.getPiece(from);
        List<Move> moves = pawn.getLegalMoves(board, from);
        assertFalse(moves.stream().anyMatch(m -> m.to().equals(new Square(3, 0))));
    }

    @Test
    public void testMalformedPGNMove() {
        String pgn = "[Event \"Error Test\"]\n1. e4 e5 2. badMove Nc6 3. Bb5 a6";
        PGNParseResult result = PGNParser.extractMoves(pgn);
        assertFalse(result.errors().isEmpty(), "Expected parsing error for 'badMove'");
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("badMove")));
    }

    @Test
    public void testIllegalSANMove() {
        String pgn = "[Event \"Illegal Move\"]\n1. e4 e5 2. e5";
        PGNParseResult result = PGNParser.extractMoves(pgn);
        assertTrue(result.errors().isEmpty());
        Game game = new Game();
        boolean moveValid = true;
        for (String san : result.moves()) {
            Move move = SANInterpreter.toMove(san, game);
            if (move == null || !game.tryMove(move)) {
                moveValid = false;
                break;
            }
        }
        assertFalse(moveValid, "Move 'e5' by white twice should be invalid");
    }
}