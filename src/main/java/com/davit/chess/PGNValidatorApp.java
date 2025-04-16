package com.davit.chess;

import com.davit.chess.Game;
import com.davit.chess.model.Move;
import com.davit.chess.parser.PGNParseResult;
import com.davit.chess.parser.PGNParser;
import com.davit.chess.GameState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PGNValidatorApp {
    public static void main(String[] args) throws IOException {
        // Load the full PGN file with multiple games
        String pgnText = Files.readString(Path.of("src/main/resources/Tbilisi2015.pgn"));

        // Split on [Event to separate games
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
                result.errors.forEach(e -> System.out.println("  - " + e));
                failed++;
            } else {
                Game game = new Game();
                int moveNumber = 1;
                boolean valid = true;

                for (String san : result.moves) {
                    System.out.println("🔍 Move #" + moveNumber + ": " + san);
                    Move move = com.davit.chess.parser.SANInterpreter.toMove(san, game);
                    if (move == null || !game.tryMove(move)) {
                        System.out.println("❌ Illegal move at #" + moveNumber + ": " + san);
                        game.getBoard().printBoard();
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
            gameIndex++;
        }

        System.out.println("🧾 Validation Complete:");
        System.out.println("✅ Passed: " + passed);
        System.out.println("❌ Failed: " + failed);
        System.out.println("📦 Total: " + (passed + failed));
    }
}
