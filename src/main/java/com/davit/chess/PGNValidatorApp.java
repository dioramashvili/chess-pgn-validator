package com.davit.chess;

import com.davit.chess.parser.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PGNValidatorApp {
    public static void main(String[] args) throws IOException {
        String pgnText =  Files.readString(Path.of("src/main/resources/test.pgn"));

        PGNParseResult result = PGNParser.extractMoves(pgnText);

        if (result.hasErrors()) {
            System.out.println("Parsing errors found:");
            for (String error : result.errors){
                System.out.println(" - " + error);
            }
        } else {
            System.out.println("Moves extracted:");
            for (String move : result.moves){
                System.out.println(" - " + move);
            }
        }
    }
}
