package checkers.pieces;

import boardGame.Board;
import boardGame.Position;
import checkers.CheckersPiece;
import checkers.Color;

public class Dame extends CheckersPiece{

	public Dame(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "O";
	}
	
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		
		Position p = new Position(0, 0);
		
		if (getColor() == Color.CYAN) {
			//nw
			p.setValues(position.getRow() - 1, position.getColumn() - 1);
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() - 1, position.getColumn() - 1);
			Position p2 = new Position(position.getRow() - 2, position.getColumn() - 2);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p) && getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2)){
				mat[p.getRow() - 1][p.getColumn() - 1] = true;
			}
			
			//ne
			p.setValues(position.getRow() - 1, position.getColumn() + 1);
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() - 1, position.getColumn() + 1);
			Position p3 = new Position(position.getRow() - 2, position.getColumn() + 2);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p) && getBoard().positionExists(p3) && !getBoard().thereIsAPiece(p3)) {
				mat[p.getRow() - 1][p.getColumn() + 1] = true;
			}
		} else {
			//sw
			p.setValues(position.getRow() + 1, position.getColumn() - 1);
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
				p.setValues(p.getRow() + 1, p.getColumn() - 1);
			}
			p.setValues(position.getRow() + 1, position.getColumn() - 1);
			Position p2 = new Position(position.getRow() + 2, position.getColumn() - 2);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p) && getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2)){
				mat[p.getRow() + 1][p.getColumn() - 1] = true;
			}
				
			//se
			p.setValues(position.getRow() + 1, position.getColumn() + 1);
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
				p.setValues(p.getRow() + 1, p.getColumn() + 1);
			}
			p.setValues(position.getRow() - 1, position.getColumn() + 1);
			Position p3 = new Position(position.getRow() + 2, position.getColumn() + 2);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p) && getBoard().positionExists(p3) && !getBoard().thereIsAPiece(p3)) {
				mat[p.getRow() + 1][p.getColumn() + 1] = true;
			}
		}
		
		return mat;
	}
}