package checkers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardGame.Board;
import boardGame.Piece;
import boardGame.Position;
import checkers.pieces.Queen;
import checkers.pieces.Dame;

public class CheckersMatch {

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	private CheckersPiece promoted;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public CheckersMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.CYAN;
		initialSetup();
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
	}
	
	public CheckersPiece getPromoted() {
		return promoted;
	}
	
	public CheckersPiece[][] getPieces() {
		CheckersPiece[][] mat = new CheckersPiece[board.getRows()][board.getColumns()];
		for (int i=0;i<board.getRows();i++) {
			for (int j=0;j<board.getColumns();j++) {
				mat[i][j] = (CheckersPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	public boolean[][] possibleMoves(CheckersPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}
	
	public CheckersPiece performcheckersMove(CheckersPosition sourcePosition, CheckersPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);
		
		if (testCheck(currentPlayer)) {
			undoMove(source,target,capturedPiece);
			throw new CheckersException("You can't put yourself in check");
		}
		
		CheckersPiece movedPiece = (CheckersPiece)board.piece(target);
		
		// #specialmove promotion
		promoted = null;
		if (movedPiece instanceof Pawn) {
			if ((movedPiece.getColor() == Color.CYAN && target.getRow() == 0) || (movedPiece.getColor() == Color.PURPLE && target.getRow() == 7)) {
				promoted = (CheckersPiece)board.piece(target);
				promoted = replacePromotedPiece("L");
			}
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		}
		else {
			nextTurn();
		}		
		return (CheckersPiece)capturedPiece;
	}
	
	public CheckersPiece replacePromotedPiece(String type) {
		if (promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		if (!type.equals("B") && !type.equals("L") && !type.equals("H") && !type.equals("R")) {
			return promoted;
		}
		
		Position pos = promoted.getcheckersPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		
		CheckersPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
	}
	
	private CheckersPiece newPiece(String type, Color color) {
		return new Queen(board, color);
	}
	
	private Piece makeMove(Position source, Position target) {
		CheckersPiece p = (CheckersPiece)board.removePiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);
		
		if(capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		return capturedPiece;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		CheckersPiece p = (CheckersPiece)board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);
		
		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
	}
	
	private void validateSourcePosition(Position position) {
		if(!board.thereIsAPiece(position)) {
			throw new CheckersException("There is no piece on source position");
		}
		if (currentPlayer != ((CheckersPiece)board.piece(position)).getColor()) {
			throw new CheckersException("The chosen piece is not yours");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new CheckersException("There is no possible moves for the chosen piece");
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
			throw new CheckersException("The chosen piece can't move to target position");
		}
	}
	
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.CYAN) ? Color.PURPLE : Color.PURPLE;
	}
	
	private Color opponent (Color color) {
		return (color == Color.PURPLE) ? Color.PURPLE : Color.CYAN;
	}
	
	private CheckersPiece king(Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((CheckersPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			if(p instanceof King) {
				return (CheckersPiece)p;
			}
		}
		throw new IllegalStateException("There is no " + color + " king on the board");
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getcheckersPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((CheckersPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		for (Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((CheckersPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i = 0; i < board.getRows(); i++) {
				for (int j = 0; j < board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = ((CheckersPiece)p).getcheckersPosition().toPosition();
						Position target = new Position(i,j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	private void placeNewPiece(char column, int row, CheckersPiece piece) {
		board.placePiece(piece, new CheckersPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup() {
		placeNewPiece('a', 1, new Dame(board, Color.CYAN));
		placeNewPiece('b', 1, new Dame(board, Color.CYAN));
		placeNewPiece('c', 1, new Dame(board, Color.CYAN));
		placeNewPiece('d', 1, new Dame(board, Color.CYAN));
		placeNewPiece('e', 1, new Dame(board, Color.CYAN));
		placeNewPiece('f', 1, new Dame(board, Color.CYAN));
		placeNewPiece('g', 1, new Dame(board, Color.CYAN));
		placeNewPiece('h', 1, new Dame(board, Color.CYAN));
        placeNewPiece('a', 2, new Dame(board, Color.CYAN));
        placeNewPiece('b', 2, new Dame(board, Color.CYAN));
        placeNewPiece('c', 2, new Dame(board, Color.CYAN));
        placeNewPiece('d', 2, new Dame(board, Color.CYAN));
        placeNewPiece('e', 2, new Dame(board, Color.CYAN));
        placeNewPiece('f', 2, new Dame(board, Color.CYAN));
        placeNewPiece('g', 2, new Dame(board, Color.CYAN));
        placeNewPiece('h', 2, new Dame(board, Color.CYAN));

        placeNewPiece('a', 8, new Dame(board, Color.PURPLE));
        placeNewPiece('b', 8, new Dame(board, Color.PURPLE));
        placeNewPiece('c', 8, new Dame(board, Color.PURPLE));
        placeNewPiece('d', 8, new Dame(board, Color.PURPLE));
        placeNewPiece('e', 8, new Dame(board, Color.PURPLE));
        placeNewPiece('f', 8, new Dame(board, Color.PURPLE));
        placeNewPiece('g', 8, new Dame(board, Color.PURPLE));
        placeNewPiece('h', 8, new Dame(board, Color.PURPLE));
        placeNewPiece('a', 7, new Dame(board, Color.PURPLE));
        placeNewPiece('b', 7, new Dame(board, Color.PURPLE));
        placeNewPiece('c', 7, new Dame(board, Color.PURPLE));
        placeNewPiece('d', 7, new Dame(board, Color.PURPLE));
        placeNewPiece('e', 7, new Dame(board, Color.PURPLE));
        placeNewPiece('f', 7, new Dame(board, Color.PURPLE));
        placeNewPiece('g', 7, new Dame(board, Color.PURPLE));
        placeNewPiece('h', 7, new Dame(board, Color.PURPLE));
	}
}
