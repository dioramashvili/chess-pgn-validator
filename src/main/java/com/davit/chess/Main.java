package com.davit.chess;

import com.davit.chess.model.*;
import com.davit.chess.parser.SANInterpreter;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Game game = new Game();

        System.out.println("♟ Welcome to the Chess CLI! Type SAN moves like e4, Nf3, O-O. Type 'exit' to quit.\n");

        while (true) {
            game.getBoard().printBoard();
            System.out.print(game.getPlayerToMove() + " to move (SAN): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) break;

            Move move = SANInterpreter.toMove(input, game);
            if (move == null || !game.tryMove(move)) {
                System.out.println("❌ Illegal move or unknown SAN notation: " + input);
            } else {
                System.out.println("✅ Move played: " + input);
                if (game.getGameState() == GameState.CHECKMATE) {
                    System.out.println("♚ Checkmate! " + game.getPlayerToMove().opposite() + " wins!");
                    break;
                } else if (game.getGameState() == GameState.STALEMATE) {
                    System.out.println("½½ Stalemate. Game drawn.");
                    break;
                } else if (game.getGameState() == GameState.CHECK) {
                    System.out.println("⚠ Check!");
                }
            }
        }
    }
}
