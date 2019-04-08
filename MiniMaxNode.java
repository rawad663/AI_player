package student_player;

import tablut.TablutBoardState;
import tablut.TablutMove;

public class MiniMaxNode {
	private TablutBoardState boardState;
	private int score;
	private TablutMove move;
	
	public MiniMaxNode(TablutBoardState boardState) {
		this.boardState = boardState;
		this.score = 0;
		this.move = null;
	}
	
	public void setBoardState(TablutBoardState bs) {
		this.boardState = bs;
	}
	
	public TablutBoardState getBoardState() {
		return this.boardState;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getScore() {
		return this.score;
	}

	public void setMove(TablutMove move) {
		this.move = move;
	}
	
	public TablutMove getMove() {
		return this.move;
	}
}