package application;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import checkers.CheckersMatch;
import checkers.CheckersPiece;
import checkers.CheckersPosition;
import checkers.Color;

public class UI {
	
	// https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println

		public static final String ANSI_RESET = "\u001B[0m";
		public static final String ANSI_BLACK = "\u001B[30m";
		public static final String ANSI_RED = "\u001B[31m";
		public static final String ANSI_GREEN = "\u001B[32m";
		public static final String ANSI_YELLOW = "\u001B[33m";
		public static final String ANSI_BLUE = "\u001B[34m";
		public static final String ANSI_PURPLE = "\u001B[35m";
		public static final String ANSI_CYAN = "\u001B[36m";
		public static final String ANSI_WHITE = "\u001B[37m";

		public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
		public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
		public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
		public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
		public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
		public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
		public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
		public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
		
		public static void clearScreen() {
			System.out.print("\033[H\033[2J");
			System.out.flush();
		}
		
		public static CheckersPosition readcheckersPosition(Scanner sc) {
			try {
				String s = sc.nextLine();
				char column = s.charAt(0);
				int row = Integer.parseInt(s.substring(1));
				return new CheckersPosition(column, row);
			}
			catch (RuntimeException e){
				throw new InputMismatchException("Error reading checkersPosition. Valid values are from a1 to h8.");
			}
		}
		
		public static void printMatch(CheckersMatch checkersMatch, List<CheckersPiece> captured) {
			printBoard(checkersMatch.getPieces());
			System.out.println();
			printCapturedPieces(captured);
			System.out.println();
			System.out.println("Turn: " + checkersMatch.getTurn());
			if (!checkersMatch.getEnd()) {
				System.out.println("Waiting Player: " + checkersMatch.getCurrentPlayer());
			}if (checkersMatch.getEnd()) {
				System.out.println("Winner: " + checkersMatch.getCurrentPlayer());
			}
		}

	public static void printBoard(CheckersPiece[][] pieces) {
		for (int i = 0; i < pieces.length; i++) {
			System.out.print((8-i) + " ");
			for (int j = 0; j < pieces.length; j++) {
				printPiece(pieces[i][j], false);
			}
			System.out.println();
		}
		System.out.println("  a b c d e f g h");
	}
	
	public static void printBoard(CheckersPiece[][] pieces, boolean[][] possibleMoves) {
		for (int i = 0; i < pieces.length; i++) {
			System.out.print((8-i) + " ");
			for (int j = 0; j < pieces.length; j++) {
				printPiece(pieces[i][j], possibleMoves[i][j]);
			}
			System.out.println();
		}
		System.out.println("  a b c d e f g h");
	}
	
	private static void printPiece(CheckersPiece piece, boolean background) {
		if (background) {
			System.out.print(ANSI_GREEN_BACKGROUND);
		}
		if (piece == null) {
			System.out.print("-" + ANSI_RESET);
		}
		else {
			if (piece.getColor() == Color.CYAN) {
                System.out.print(ANSI_CYAN + piece + ANSI_RESET);
            }
            else {
                System.out.print(ANSI_PURPLE + piece + ANSI_RESET);
            }
		}
		System.out.print(" ");
	}
	
	private static void printCapturedPieces(List<CheckersPiece> captured) {
		List<CheckersPiece> cyan = captured.stream().filter(x -> x.getColor() == Color.CYAN).collect(Collectors.toList());
		List<CheckersPiece> purple = captured.stream().filter(x -> x.getColor() == Color.PURPLE).collect(Collectors.toList());
		System.out.println("Captured pieces: ");
		System.out.print("Cyan: ");
		System.out.print(ANSI_CYAN);
		System.out.println(Arrays.toString(cyan.toArray()));
		System.out.print(ANSI_RESET);
		System.out.print("Purple: ");
		System.out.print(ANSI_PURPLE);
		System.out.println(Arrays.toString(purple.toArray()));
		System.out.print(ANSI_RESET);
		
	}
}