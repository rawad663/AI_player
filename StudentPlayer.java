package student_player;

import boardgame.Move;
import coordinates.Coord;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutMove;
import tablut.TablutPlayer;

/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260603780");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(TablutBoardState boardState) {
    		int player = boardState.getTurnPlayer();
    	
    		if(boardState.getTurnNumber() == 0 && boardState.getTurnPlayer() == 0) {
    			// AI seemed to prefer the same first move. Due to symmetry we can select that move on any symmetric axis.
    			// Assuming that strategy, we cut down the initial tree size.
    			// This is a safe strategy since we always have the first move.
    			TablutMove firstMove = new TablutMove(Coordinates.get(7, 4), Coordinates.get(7, 3), boardState.getTurnPlayer());
    			
    			return firstMove;
    		} else if(boardState.getTurnNumber() == 0 && boardState.getTurnPlayer() == 1) {
    			return MyTools.generateBestMove(boardState, 5, player);
    		}
    	
        return MyTools.generateBestMove(boardState, 3, player);
    }
}