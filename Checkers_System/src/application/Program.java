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
		CheckersMatch checkersMatch = new CheckersMatch();
		List<CheckersPiece> captured = new ArrayList<>();
		
		while (!checkersMatch.getEnd()) {
			try {
				UI.clearScreen();
				UI.printMatch(checkersMatch, captured);
				System.out.println();
				System.out.print("Source: ");
				CheckersPosition source = UI.readcheckersPosition(sc);
				
				boolean[][] possibleMoves = checkersMatch.possibleMoves(source);
				UI.clearScreen();
				UI.printBoard(checkersMatch.getPieces(), possibleMoves);				
				System.out.println();
				System.out.print("Target: ");
				CheckersPosition target = UI.readcheckersPosition(sc);
				
				CheckersPiece capturedPiece = checkersMatch.performcheckersMove(source, target);
				
				if (capturedPiece != null) {
					captured.add(capturedPiece);
				}
				
				if (checkersMatch.getPromoted() != null) {
					checkersMatch.replacePromotedPiece();
				}
			}
			catch(CheckersException e) {
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
