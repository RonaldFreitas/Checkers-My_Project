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
	private checkersPiece promoted;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public CheckersMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.BLUE;
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
	
	public checkersPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	
	public checkersPiece getPromoted() {
		return promoted;
	}
	
	public checkersPiece[][] getPieces() {
		checkersPiece[][] mat = new checkersPiece[board.getRows()][board.getColumns()];
		for (int i=0;i<board.getRows();i++) {
			for (int j=0;j<board.getColumns();j++) {
				mat[i][j] = (checkersPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	public boolean[][] possibleMoves(checkersPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}
	
	public checkersPiece performcheckersMove(checkersPosition sourcePosition, checkersPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);
		
		if (testCheck(currentPlayer)) {
			undoMove(source,target,capturedPiece);
			throw new checkersException("You can't put yourself in check");
		}
		
		checkersPiece movedPiece = (checkersPiece)board.piece(target);
		
		// #specialmove promotion
		promoted = null;
		if (movedPiece instanceof Pawn) {
			if ((movedPiece.getColor() == Color.BLUE && target.getRow() == 0) || (movedPiece.getColor() == Color.RED && target.getRow() == 7)) {
				promoted = (checkersPiece)board.piece(target);
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
		
		// #specialmove en passant
		if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		}
		else {
			enPassantVulnerable = null;
		}
		
		return (checkersPiece)capturedPiece;
	}
	
	public checkersPiece replacePromotedPiece(String type) {
		if (promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		if (!type.equals("B") && !type.equals("L") && !type.equals("H") && !type.equals("R")) {
			return promoted;
		}
		
		Position pos = promoted.getcheckersPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		
		checkersPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
	}
	
	private checkersPiece newPiece(String type, Color color) {
		if(type.equals("B")) return new Bishop(board, color);
		if(type.equals("H")) return new Knight(board, color);
		if(type.equals("L")) return new Queen(board, color);
		return new Rook(board, color);
	}
	
	private Piece makeMove(Position source, Position target) {
		checkersPiece p = (checkersPiece)board.removePiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);
		
		if(capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		
		// #specialmove castling kingside rook
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			checkersPiece rook = (checkersPiece)board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}
		
		// #specialmove castling queenside rook
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			checkersPiece rook = (checkersPiece)board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}
		
		// #specialmove en passant
		if (p instanceof Pawn) {
			if(source.getColumn() != target.getColumn() && capturedPiece == null) {
				Position pawnPosition;
				if (p.getColor() == Color.BLUE) {
					pawnPosition = new Position(target.getRow() + 1, target.getColumn());
				}
				else {
					pawnPosition = new Position(target.getRow() - 1, target.getColumn());
				}
				capturedPiece = board.removePiece(pawnPosition);
				capturedPieces.add(capturedPiece);
				piecesOnTheBoard.remove(capturedPiece);
			}
		}
		
		return capturedPiece;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		checkersPiece p = (checkersPiece)board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);
		
		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
		
		// #specialmove castling kingside rook
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			checkersPiece rook = (checkersPiece)board.removePiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}
		
		// #specialmove castling queenside rook
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			checkersPiece rook = (checkersPiece)board.removePiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}
		
		// #specialmove en passant
		if (p instanceof Pawn) {
			if(source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
				checkersPiece pawn = (checkersPiece)board.removePiece(target);
				Position pawnPosition;
				if (p.getColor() == Color.BLUE) {
					pawnPosition = new Position(3, target.getColumn());
				}
				else {
					pawnPosition = new Position(4, target.getColumn());
				}
				board.placePiece(pawn, pawnPosition);
			}
		}
	}
	
	private void validateSourcePosition(Position position) {
		if(!board.thereIsAPiece(position)) {
			throw new checkersException("There is no piece on source position");
		}
		if (currentPlayer != ((checkersPiece)board.piece(position)).getColor()) {
			throw new checkersException("The chosen piece is not yours");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new checkersException("There is no possible moves for the chosen piece");
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
			throw new checkersException("The chosen piece can't move to target position");
		}
	}
	
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.BLUE) ? Color.RED : Color.BLUE;
	}
	
	private Color opponent (Color color) {
		return (color == Color.BLUE) ? Color.RED : Color.BLUE;
	}
	
	private checkersPiece king(Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((checkersPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			if(p instanceof King) {
				return (checkersPiece)p;
			}
		}
		throw new IllegalStateException("There is no " + color + " king on the board");
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getcheckersPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((checkersPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
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
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((checkersPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i = 0; i < board.getRows(); i++) {
				for (int j = 0; j < board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = ((checkersPiece)p).getcheckersPosition().toPosition();
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
	
	private void placeNewPiece(char column, int row, checkersPiece piece) {
		board.placePiece(piece, new checkersPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup() {
		placeNewPiece('a', 1, new Rook(board, Color.BLUE));
		placeNewPiece('b', 1, new Knight(board, Color.BLUE));
		placeNewPiece('c', 1, new Bishop(board, Color.BLUE));
		placeNewPiece('d', 1, new Queen(board, Color.BLUE));
		placeNewPiece('e', 1, new King(board, Color.BLUE, this));
		placeNewPiece('f', 1, new Bishop(board, Color.BLUE));
		placeNewPiece('g', 1, new Knight(board, Color.BLUE));
		placeNewPiece('h', 1, new Rook(board, Color.BLUE));
        placeNewPiece('a', 2, new Pawn(board, Color.BLUE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.BLUE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.BLUE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.BLUE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.BLUE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.BLUE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.BLUE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.BLUE, this));

        placeNewPiece('a', 8, new Rook(board, Color.RED));
        placeNewPiece('b', 8, new Knight(board, Color.RED));
        placeNewPiece('c', 8, new Bishop(board, Color.RED));
        placeNewPiece('d', 8, new Queen(board, Color.RED));
        placeNewPiece('e', 8, new King(board, Color.RED,this));
        placeNewPiece('f', 8, new Bishop(board, Color.RED));
        placeNewPiece('g', 8, new Knight(board, Color.RED));
        placeNewPiece('h', 8, new Rook(board, Color.RED));
        placeNewPiece('a', 7, new Pawn(board, Color.RED, this));
        placeNewPiece('b', 7, new Pawn(board, Color.RED, this));
        placeNewPiece('c', 7, new Pawn(board, Color.RED, this));
        placeNewPiece('d', 7, new Pawn(board, Color.RED, this));
        placeNewPiece('e', 7, new Pawn(board, Color.RED, this));
        placeNewPiece('f', 7, new Pawn(board, Color.RED, this));
        placeNewPiece('g', 7, new Pawn(board, Color.RED, this));
        placeNewPiece('h', 7, new Pawn(board, Color.RED, this));
	}
}
