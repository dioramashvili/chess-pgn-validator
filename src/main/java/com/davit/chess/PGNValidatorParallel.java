package com.davit.chess;

import com.davit.chess.model.Game;
import com.davit.chess.model.Move;
import com.davit.chess.parser.PGNParseResult;
import com.davit.chess.parser.PGNParser;
import com.davit.chess.parser.SANInterpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class PGNValidatorParallel {

    private record ValidationResult(int index, boolean isValid, String message) {}

    private record PGNValidationTask(int index, String pgn) implements Callable<ValidationResult> {
        @Override
        public ValidationResult call() {
            try {
                PGNParseResult result = PGNParser.extractMoves(pgn);
                if (!result.errors().isEmpty()) {
                    return new ValidationResult(index, false, "PGN parse error: " + result.errors());
                }

                Game game = new Game();
                for (String san : result.moves()) {
                    Move move = SANInterpreter.toMove(san, game);
                    if (move == null || !game.tryMove(move)) {
                        return new ValidationResult(index, false, "Illegal move: " + san);
                    }
                }

                return new ValidationResult(index, true, "Valid");
            } catch (Exception e) {
                return new ValidationResult(index, false, "Exception: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String pathToFile = "src/main/resources/Tbilisi2015.pgn";
        List<String> allPGNs = readGamesFromFile(pathToFile);

        System.out.println("📦 Loaded PGN chunks: " + allPGNs.size());

        int threadCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        System.out.println("🔄 Validating " + allPGNs.size() + " PGN games using " + threadCount + " threads...");

        List<Future<ValidationResult>> futures = new ArrayList<>();
        for (int i = 0; i < allPGNs.size(); i++) {
            futures.add(executor.submit(new PGNValidationTask(i + 1, allPGNs.get(i))));
        }

        AtomicInteger valid = new AtomicInteger();
        AtomicInteger invalid = new AtomicInteger();
        List<ValidationResult> results = new ArrayList<>();

        for (Future<ValidationResult> future : futures) {
            ValidationResult result = future.get();
            results.add(result);
            if (result.isValid()) valid.incrementAndGet();
            else invalid.incrementAndGet();
        }

        executor.shutdown();

        System.out.println("\n===== Validation Summary =====");
        for (ValidationResult r : results) {
            if (r.isValid()) {
                System.out.printf("✅ Game #%d: %s%n", r.index(), r.message());
            } else {
                System.out.printf("❌ Game #%d: %s%n", r.index(), r.message());
            }
        }

        System.out.printf("\n✅ Total Valid: %d%n❌ Total Invalid: %d%n", valid.get(), invalid.get());
    }

    public static List<String> readGamesFromFile(String filePath) throws IOException {
        List<String> allGames = new ArrayList<>();
        StringBuilder currentGame = new StringBuilder();
        boolean insideGame = false;

        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                if (insideGame && currentGame.toString().contains("1.")) {
                    allGames.add(currentGame.toString().trim());
                    currentGame.setLength(0);
                    insideGame = false;
                }
            } else {
                currentGame.append(line).append("\n");
                if (line.matches("^\\d+\\.\\s*.*")) {
                    insideGame = true;
                }
            }
        }

        // Handle last game
        if (insideGame && currentGame.toString().contains("1.")) {
            allGames.add(currentGame.toString().trim());
        }

        return allGames;
    }
}
