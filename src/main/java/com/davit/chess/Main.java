package com.davit.chess;

import com.davit.chess.model.*;
import com.davit.chess.*;


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Game game = new Game();

        while (true) {
            // Step 1: print the board
            game.getBoard().printBoard();

            // Step 2: prompt for move
            System.out.print(game.getPlayerToMove() + " to move. Enter your move (e.g., e2 e4): ");
            String input = scanner.nextLine();

            // Step 3: exit condition
            if (input.equalsIgnoreCase("exit")) break;

            // Step 4: parse the move and try it...
            String[] parts = input.split(" ");
            if (parts.length != 2) {
                System.out.println("Invalid input. Use format: e2 e4");
                continue;
            }

            Square from = parseSquare(parts[0]);
            Square to = parseSquare(parts[1]);
            Piece piece = game.getBoard().getPiece(from);
            if (piece == null){
                System.out.println("No piece at " + parts[0]);
                continue;
            }

            Move move = new Move(from, to, piece);
            if(game.tryMove(move)){
                System.out.println("Move accepted");
            } else {
                System.out.println("Illegal move");
            }
        }
    }

    private static Square parseSquare(String notation) {
        int col = notation.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(notation.charAt(1));
        return new Square(row, col);
    }

}
