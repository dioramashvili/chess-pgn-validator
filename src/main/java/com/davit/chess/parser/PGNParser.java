package com.davit.chess.parser;

import java.util.ArrayList;
import java.util.List;

public class PGNParser {
    public static PGNParseResult extractMoves(String pgn) {
        List<String> moves = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        String result = "*"; // default to unknown

        // 1. Remove headers and join all move lines
        StringBuilder movesOnly = new StringBuilder();
        for (String line : pgn.split("\n")) {
            if (!line.startsWith("[")) {
                movesOnly.append(line).append(" ");
            }
        }

        // 2. Clean PGN: Remove move numbers, normalize spacing
        String cleaned = movesOnly.toString()
                .replaceAll("\\d+\\.", "")   // Remove move numbers like 1.
                .replaceAll("\\s+", " ")     // Normalize whitespace
                .trim();

        // 3. Tokenize and extract moves + result
        for (String token : cleaned.split(" ")) {
            if (token.matches("1-0|0-1|1/2-1/2|\\*")) {
                result = token;
                continue;
            }

            if (isValidSAN(token)) {
                moves.add(token);
            } else {
                errors.add("Invalid SAN token: " + token);
            }
        }

        return new PGNParseResult(moves, errors, result);
    }

    private static boolean isValidSAN(String move) {
        return move.matches("^(O-O(-O)?|[NBRQK]?[a-h]?[1-8]?x?[a-h][1-8](=[QRBN])?[+#]?)$");
    }
}
