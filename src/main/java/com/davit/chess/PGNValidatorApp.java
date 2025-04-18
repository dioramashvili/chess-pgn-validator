package com.davit.chess;

import com.davit.chess.model.Board;
import com.davit.chess.model.Move;
import com.davit.chess.parser.PGNParseResult;
import com.davit.chess.parser.PGNParser;
import com.davit.chess.parser.SANInterpreter;
import com.davit.chess.util.ErrorReporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PGNValidatorApp {
    public static void main(String[] args) throws IOException {
        String pgnText = Files.readString(Path.of("src/main/resources/Tbilisi2015.pgn"));
        String[] games = pgnText.split("(?=\\[Event )");

        int gameIndex = 1;
        int passed = 0;
        int failed = 0;

        for (String gamePgn : games) {
            gamePgn = gamePgn.trim();
            if (gamePgn.isEmpty()) continue;

            PGNParseResult result = PGNParser.extractMoves(gamePgn);

            if (result.hasErrors()) {
                for (String error : result.errors()) {
                    ErrorReporter.reportSyntaxError(gameIndex, error.replace("Syntax error: ", ""));
                }
                failed++;
            } else {
                Game game = new Game();
                int moveNumber = 1;
                boolean valid = true;

                for (String san : result.moves()) {
                    Move move = com.davit.chess.parser.SANInterpreter.toMove(san, game);
                    Board snapshot = new Board(game.getBoard()); // Capture board before the move

                    if (move == null || !game.tryMove(move)) {
                        ErrorReporter.reportIllegalMove(gameIndex, moveNumber, san, move, game, snapshot);
                        valid = false;
                        break;
                    }
                    moveNumber++;
                }

                if (valid) {
                    GameState state = game.getGameState();
                    System.out.printf("✅ Game #%d: Valid (%s, PGN result: %s)%n", gameIndex, state, result.result());
                    passed++;
                } else {
                    System.out.printf("❌ Game #%d is invalid due to the error above.%n", gameIndex);
                    failed++;
                }
            }


            gameIndex++;
        }

        System.out.println("\n🧾 Validation Summary:");
        System.out.println("✅ Passed: " + passed);
        System.out.println("❌ Failed: " + failed);
        System.out.println("📦 Total: " + (passed + failed));
    }
}
