package com.davit.chess;

import com.davit.chess.model.Move;
import com.davit.chess.util.ErrorReporter;
import com.davit.chess.parser.PGNParseResult;
import com.davit.chess.parser.PGNParser;
import com.davit.chess.parser.SANInterpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.davit.chess.parser.SANInterpreter.resetDebugCounter;

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

            System.out.println("🎯 Validating Game #" + gameIndex);
            PGNParseResult result = PGNParser.extractMoves(gamePgn);

            if (result.hasErrors()) {
                System.out.println("❌ Parsing errors found:");
                result.errors().forEach(e -> System.out.println("  - " + e));
                failed++;
            } else {
                Game game = new Game();
                int moveNumber = 1;
                boolean valid = true;

                for (String san : result.moves()) {
                    Move move = SANInterpreter.toMove(san, game);
                    if (move == null || !game.tryMove(move)) {
                        ErrorReporter.reportIllegalMove(gameIndex, moveNumber, san, move == null, "Illegal move");
                        valid = false;
                        break;
                    }
                    moveNumber++;
                }

                if (valid) {
                    GameState state = game.getGameState();
                    System.out.println("✅ Game #" + gameIndex + " valid (" + state + ")");
                    passed++;
                } else {
                    System.out.println("❌ Game #" + gameIndex + " invalid.");
                    failed++;
                }
            }

            System.out.println("=====================================\n");
            resetDebugCounter();
            gameIndex++;
        }

        System.out.println("🧾 Validation Complete:");
        System.out.println("✅ Passed: " + passed);
        System.out.println("❌ Failed: " + failed);
        System.out.println("📦 Total: " + (passed + failed));
    }
}
