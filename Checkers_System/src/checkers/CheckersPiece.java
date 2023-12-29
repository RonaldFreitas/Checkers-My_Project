package checkers;

import boardGame.Board;
import boardGame.Piece;
import boardGame.Position;

public abstract class CheckersPiece extends Piece{
	
	private Color color;
	private int moveCount;

	public CheckersPiece(Board board, Color color) {
		super(board);
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
	
	public int getMoveCount() {
		return moveCount;
	}
	
	public void increaseMoveCount() {
		moveCount++;
	}
	
	public void decreaseMoveCount() {
		moveCount--;
	}
	
	public CheckersPosition getcheckersPosition() {
		return CheckersPosition.fromPosition(position);
	}
	
	protected boolean isThereOpponentPiece(Position position) {
		CheckersPiece p = (CheckersPiece)getBoard().piece(position);
		return p != null && p.getColor() != color;
	}
}
