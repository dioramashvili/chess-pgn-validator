package com.davit.chess.parser;

import java.util.List;

public class PGNParseResult {
    public final List<String> moves;
    public final List<String> errors;

    public PGNParseResult(List<String> moves, List<String> errors) {
        this.moves = moves;
        this.errors = errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
