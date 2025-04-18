package com.davit.chess;

import com.davit.chess.model.Move;
import com.davit.chess.parser.PGNParseResult;
import com.davit.chess.parser.PGNParser;
import com.davit.chess.parser.SANInterpreter;

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
                System.out.printf("❌ Game #%d: PGN parse error: %s%n", gameIndex, result.errors());
                failed++;
            } else {
                Game game = new Game();
                boolean valid = true;

                for (String san : result.moves()) {
                    Move move = SANInterpreter.toMove(san, game);
                    if (move == null || !game.tryMove(move)) {
                        System.out.printf("❌ Game #%d: Illegal move: %s%n", gameIndex, san);
                        valid = false;
                        break;
                    }
                }

                if (valid) {
                    GameState state = game.getGameState();
                    String expected = result.result(); // e.g., "1-0"

                    System.out.printf("✅ Game #%d: Valid (%s, PGN result: %s)%n", gameIndex, state, expected);
                    passed++;
                }
                else {
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
