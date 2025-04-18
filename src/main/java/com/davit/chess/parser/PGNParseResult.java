package com.davit.chess.parser;

import java.util.List;

public record PGNParseResult(List<String> moves, List<String> errors, String result) {
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
