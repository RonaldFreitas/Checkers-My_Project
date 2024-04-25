package checkers;

import java.util.ArrayList;
import java.util.List;

import boardGame.Board;
import boardGame.Piece;
import boardGame.Position;
import checkers.pieces.Dame;
import checkers.pieces.Queen;

public class CheckersMatch {

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean end;
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
	
	public boolean getEnd() {
		return end;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
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
		CheckersPiece movedPiece = (CheckersPiece)board.piece(target);
		
		// #specialmove promotion
		promoted = null;
		if (movedPiece instanceof Dame) {
			if ((movedPiece.getColor() == Color.CYAN && target.getRow() == 0) || (movedPiece.getColor() == Color.PURPLE && target.getRow() == 7)) {
				promoted = (CheckersPiece)board.piece(target);
				promoted = replacePromotedPiece();
			}
		}
		
		boolean checkWinCondition = piecesOnTheBoard.stream().noneMatch(piece -> ((CheckersPiece) piece).getColor() == opponent(movedPiece.getColor()));
		
		if (checkWinCondition) {
			end = true;
		} else {
			nextTurn();
		}
		return (CheckersPiece)capturedPiece;
	}
	
	public CheckersPiece replacePromotedPiece() {
		
		Position pos = promoted.getcheckersPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		
		CheckersPiece newPiece = newPiece(promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
	}
	
	private CheckersPiece newPiece(Color color) {
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
		currentPlayer = (currentPlayer == Color.CYAN) ? Color.PURPLE : Color.CYAN;
	}
	
	private Color opponent (Color color) {
		return (color == Color.CYAN) ? Color.PURPLE : Color.CYAN;
	}
	
	private void placeNewPiece(char column, int row, CheckersPiece piece) {
		board.placePiece(piece, new CheckersPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup() {
		placeNewPiece('a', 1, new Dame(board, Color.CYAN));
		placeNewPiece('c', 1, new Dame(board, Color.CYAN));
		placeNewPiece('e', 1, new Dame(board, Color.CYAN));
		placeNewPiece('g', 1, new Dame(board, Color.CYAN));
		placeNewPiece('b', 2, new Dame(board, Color.CYAN));
		placeNewPiece('d', 2, new Dame(board, Color.CYAN));
		placeNewPiece('f', 2, new Dame(board, Color.CYAN));
		placeNewPiece('h', 2, new Dame(board, Color.CYAN));
        placeNewPiece('a', 3, new Dame(board, Color.CYAN));
        placeNewPiece('c', 3, new Dame(board, Color.CYAN));
        placeNewPiece('e', 3, new Dame(board, Color.CYAN));
        placeNewPiece('g', 3, new Dame(board, Color.CYAN));

        placeNewPiece('b', 8, new Dame(board, Color.PURPLE));
        placeNewPiece('d', 8, new Dame(board, Color.PURPLE));
        placeNewPiece('f', 8, new Dame(board, Color.PURPLE));
        placeNewPiece('h', 8, new Dame(board, Color.PURPLE));
        placeNewPiece('a', 7, new Dame(board, Color.PURPLE));
        placeNewPiece('c', 7, new Dame(board, Color.PURPLE));
        placeNewPiece('e', 7, new Dame(board, Color.PURPLE));
        placeNewPiece('g', 7, new Dame(board, Color.PURPLE));
        placeNewPiece('b', 6, new Dame(board, Color.PURPLE));
        placeNewPiece('d', 6, new Dame(board, Color.PURPLE));
        placeNewPiece('f', 6, new Dame(board, Color.PURPLE));
        placeNewPiece('h', 6, new Dame(board, Color.PURPLE));
	}
}
