package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import checkers.CheckersException;
import checkers.CheckersMatch;
import checkers.CheckersPiece;
import checkers.CheckersPosition;

public class Program {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		checkersMatch checkersMatch = new checkersMatch();
		List<checkersPiece> captured = new ArrayList<>();
		
		while (!checkersMatch.getCheckMate()) {
			try {
				UI.clearScreen();
				UI.printMatch(checkersMatch, captured);
				System.out.println();
				System.out.print("Source: ");
				checkersPosition source = UI.readcheckersPosition(sc);
				
				boolean[][] possibleMoves = checkersMatch.possibleMoves(source);
				UI.clearScreen();
				UI.printBoard(checkersMatch.getPieces(), possibleMoves);				
				System.out.println();
				System.out.print("Target: ");
				checkersPosition target = UI.readcheckersPosition(sc);
				
				checkersPiece capturedPiece = checkersMatch.performcheckersMove(source, target);
				
				if (capturedPiece != null) {
					captured.add(capturedPiece);
				}
				
				if (checkersMatch.getPromoted() != null) {
					System.out.println("Enter piece for promotion (B/H/R/L): ");
					String type = sc.nextLine().toUpperCase();
					while (!type.equals("B") && !type.equals("L") && !type.equals("H") && !type.equals("R")) {
						System.out.println("Invalid value! Enter piece for promotion (B/H/R/L): ");
						type = sc.nextLine().toUpperCase();
					}
					checkersMatch.replacePromotedPiece(type);
				}
			}
			catch(checkersException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
			catch(InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
		UI.clearScreen();
		UI.printMatch(checkersMatch, captured);
	}

}
