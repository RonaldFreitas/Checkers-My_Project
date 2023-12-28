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
	
	public checkersPosition getcheckersPosition() {
		return checkersPosition.fromPosition(position);
	}
	
	protected boolean isThereOpponentPiece(Position position) {
		checkersPiece p = (checkersPiece)getBoard().piece(position);
		return p != null && p.getColor() != color;
	}
}
