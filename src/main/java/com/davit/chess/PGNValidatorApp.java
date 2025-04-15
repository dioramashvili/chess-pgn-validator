package com.davit.chess;

import com.davit.chess.model.Move;
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
            result.errors.forEach(System.out::println);
        } else {
            Game game = new Game();
            int moveNumber = 1;
            for (String san : result.moves){
                Move move = SANInterpreter.toMove(san, game);
                if (move == null || !game.tryMove(move)){
                    System.out.println("Illegal move at #" + moveNumber + ": " + san);
                    game.getBoard().printBoard();
                    return;
                }
                moveNumber++;
            }
            System.out.println("Game is valid");
        }
    }
}
