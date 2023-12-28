package checkers;

import boardGame.Position;

public class checkersPosition {

	private char column;
	private int row;
	
	public checkersPosition(char column, int row) {
		if(column < 'a' || column > 'h' || row < 1 || row > 8) {
			throw new checkersException("Error instantiating checkersPosition. Valid values are from a1 to h8.");
		}
		this.column = column;
		this.row = row;		
	}

	public char getColumn() {
		return column;
	}
	
	public int getRow() {
		return row;
	}
	
	protected Position toPosition() {
		return new Position(8 - row, column - 'a');
	}
	
	protected static checkersPosition fromPosition(Position position) {
		return new checkersPosition((char)('a' + position.getColumn()), 8 - position.getRow());
	}
	
	@Override
	public String toString() {
		return "" + column + row;
	}
}
