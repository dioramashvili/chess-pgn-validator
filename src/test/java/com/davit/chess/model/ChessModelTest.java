package com.davit.chess.model;

import com.davit.chess.Game;
import com.davit.chess.GameState;
import com.davit.chess.parser.PGNParseResult;
import com.davit.chess.parser.PGNParser;
import com.davit.chess.parser.SANInterpreter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChessModelTest {

    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board();
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) board.setPiece(new Square(r, c), null);
    }

    @Test
    public void testPawnInitialTwoStepMove() {
        board = new Board(); // Use full board for this
        Square from = new Square(6, 4); // e2
        Piece pawn = board.getPiece(from);
        List<Move> legalMoves = pawn.getLegalMoves(board, from);
        assertTrue(legalMoves.stream().anyMatch(m -> m.to().equals(new Square(4, 4)))); // e4
    }

    @Test
    public void testKnightJumpOverPieces() {
        board = new Board();
        Square from = new Square(7, 6); // g1
        Piece knight = board.getPiece(from);
        List<Move> legalMoves = knight.getLegalMoves(board, from);
        assertTrue(legalMoves.stream().anyMatch(m -> m.to().equals(new Square(5, 5)))); // f3
    }

    @Test
    public void testBishopMovement() {
        board.setPiece(new Square(4, 4), new Bishop(Color.WHITE, PieceType.BISHOP));
        Square from = new Square(4, 4);
        Piece bishop = board.getPiece(from);
        List<Move> legalMoves = bishop.getLegalMoves(board, from);
        assertTrue(legalMoves.stream().anyMatch(m -> m.to().equals(new Square(7, 7))));
    }

    @Test
    public void testRookMovement() {
        board.setPiece(new Square(3, 3), new Rook(Color.WHITE, PieceType.ROOK));
        Square from = new Square(3, 3);
        Piece rook = board.getPiece(from);
        List<Move> legalMoves = rook.getLegalMoves(board, from);
        assertTrue(legalMoves.stream().anyMatch(m -> m.to().equals(new Square(3, 7))));
    }

    @Test
    public void testQueenMovement() {
        board.setPiece(new Square(4, 4), new Queen(Color.WHITE, PieceType.QUEEN));
        Square from = new Square(4, 4);
        Piece queen = board.getPiece(from);
        List<Move> legalMoves = queen.getLegalMoves(board, from);
        assertTrue(legalMoves.stream().anyMatch(m -> m.to().equals(new Square(7, 7))));
    }

    @Test
    public void testKingMovement() {
        board.setPiece(new Square(4, 4), new King(Color.WHITE, PieceType.KING));
        Square from = new Square(4, 4);
        Piece king = board.getPiece(from);
        List<Move> legalMoves = king.getLegalMoves(board, from);
        assertEquals(8, legalMoves.size());
    }

    @Test
    public void testCastlingMoveExists() {
        King king = new King(Color.WHITE, PieceType.KING);
        Rook rook = new Rook(Color.WHITE, PieceType.ROOK);
        king.setHasMoved(false);
        rook.setHasMoved(false);

        board.setPiece(new Square(7, 4), king); // e1
        board.setPiece(new Square(7, 7), rook); // h1

        List<Move> kingMoves = king.getLegalMoves(board, new Square(7, 4));
        assertTrue(kingMoves.stream().anyMatch(m -> m.to().equals(new Square(7, 6))));
    }

    @Test
    public void testEnPassantCapture() {
        Pawn whitePawn = new Pawn(Color.WHITE, PieceType.PAWN);
        Pawn blackPawn = new Pawn(Color.BLACK, PieceType.PAWN);
        board.setPiece(new Square(3, 4), whitePawn); // e5
        board.setPiece(new Square(3, 5), blackPawn); // f5
        board.setEnPassantTarget(new Square(2, 5));  // f6

        Game game = new Game(board, Color.WHITE);
        List<Move> legalMoves = whitePawn.getLegalMoves(board, new Square(3, 4));
        assertTrue(legalMoves.stream().anyMatch(m -> m.to().equals(new Square(2, 5))));
    }

    @Test
    public void testPawnPromotionToQueen() {
        Pawn pawn = new Pawn(Color.WHITE, PieceType.PAWN);
        board.setPiece(new Square(1, 0), pawn); // a7

        Game game = new Game(board, Color.WHITE);
        Move promotionMove = new Move(new Square(1, 0), new Square(0, 0), pawn, PieceType.QUEEN);
        assertTrue(game.tryMove(promotionMove));
    }

    @Test
    public void testCheckDetection() {
        King whiteKing = new King(Color.WHITE, PieceType.KING);
        Rook blackRook = new Rook(Color.BLACK, PieceType.ROOK);
        board.setPiece(new Square(7, 4), whiteKing);
        board.setPiece(new Square(0, 4), blackRook);

        Game game = new Game(board, Color.WHITE);
        assertTrue(game.isInCheck(Color.WHITE));
    }

    @Test
    public void testStalemateDetection() {
        board.setPiece(new Square(0, 7), new King(Color.BLACK, PieceType.KING));
        board.setPiece(new Square(2, 6), new Queen(Color.WHITE, PieceType.QUEEN));
        board.setPiece(new Square(1, 5), new King(Color.WHITE, PieceType.KING));

        Game game = new Game(board, Color.BLACK);
        game.evaluateGameState(); // 👈 Force evaluation
        assertEquals(GameState.STALEMATE, game.getGameState());
    }


    @Test
    public void testPGNParserValidMovesExtraction() {
        String pgn = """
        [Event "Test"]
        [Site "Testville"]
        [Date "2023.01.01"]
        [Round "1"]
        [White "Tester"]
        [Black "Bot"]
        [Result "1-0"]
        1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 1-0
        """;
        PGNParseResult result = PGNParser.extractMoves(pgn);
        assertTrue(result.errors().isEmpty());
        assertEquals(6, result.moves().size());
        assertEquals("1-0", result.result());
    }

    @Test
    public void testDisambiguation() {
        board.setPiece(new Square(7, 1), new Knight(Color.WHITE, PieceType.KNIGHT)); // b1
        board.setPiece(new Square(5, 2), new Knight(Color.WHITE, PieceType.KNIGHT)); // c3

        Game game = new Game(board, Color.WHITE);
        Move move = SANInterpreter.toMove("Nbd2", game);

        assertNotNull(move);
        assertEquals(new Square(7, 1), move.from());
    }

    @Test
    public void testInvalidSANMove() {
        String pgn = "1. e4 e5 2. Pxz9";
        PGNParseResult result = PGNParser.extractMoves(pgn);
        assertFalse(result.errors().isEmpty());
    }
}