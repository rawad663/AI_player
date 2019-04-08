package student_player;

import tablut.TablutBoardState;
import tablut.TablutBoardState.Piece;
import tablut.TablutMove;

import java.util.HashSet;

import boardgame.Board;
import boardgame.Move;
import coordinates.Coord;
import coordinates.Coordinates;

public class MyTools {
	// default ai player is Muskovite
	private static int ai = 0;

	/**
	 * The heuristic function that will evaluate the score of
	 * a particular Board State.
	 * This includes general positioning strategies as well as
	 * player specific strategies
	 * @param tbs
	 * @return board state score (integer)
	 */
	public static int evaluateBoardState(TablutBoardState tbs) {
		int winScore = 500;
		int loseScore = -500;
		int score = 0;

		if (tbs.getWinner() != Board.NOBODY) {
			if (tbs.getWinner() == Board.DRAW) return 0;
			else return tbs.getWinner() == ai
					? winScore - tbs.getTurnNumber() // Penalize the win by match size, so that the MiniMax search prefers immediate wins.
					: loseScore;
		}
		
		int playerPieces = tbs.getTurnPlayer() == ai ? tbs.getPlayerPieceCoordinates().size()
				: tbs.getOpponentPieceCoordinates().size();
		int enemyPieces = tbs.getTurnPlayer() == ai ? tbs.getOpponentPieceCoordinates().size()
				: tbs.getPlayerPieceCoordinates().size();

		score += (playerPieces - enemyPieces);
		int shortestDist = Coordinates.distanceToClosestCorner(tbs.getKingPosition());

		if (ai == 0 && tbs.getTurnPlayer() == 1) {
			evaluateBlack(tbs, score);
			score += shortestDist;
		} else if (ai == 1 && tbs.getTurnPlayer() == 0)
			evaluateWhite(tbs, score);
			score += 9/shortestDist;

		return score;
	}
	
	/**
	 * A helper function to add black strategy considerations to the heuristic
	 * @param tbs
	 * @param score
	 */
	private static void  evaluateBlack(TablutBoardState tbs, int score) {
		HashSet<Coord> coordinates = tbs.getOpponentPieceCoordinates();
		
		for(Coord c: coordinates ) {
			// Favor Diagonal Positioning to cover more ground
			if((c.x != 8 && c.y != 8 && tbs.getPieceAt(c.x + 1, c.y + 1) != null && tbs.getPieceAt(c.x + 1, c.y + 1) == Piece.BLACK)
					|| (c.y != 8 && c.x != 0 && tbs.getPieceAt(c.x - 1, c.y + 1) != null && tbs.getPieceAt(c.x - 1, c.y + 1) == Piece.BLACK)
					|| (c.x != 8 && c.y != 0 && tbs.getPieceAt(c.x + 1, c.y - 1) != null && tbs.getPieceAt(c.x + 1, c.y - 1) == Piece.BLACK)
					|| (c.x != 0 && c.y != 0 && tbs.getPieceAt(c.x - 1, c.y - 1) != null && tbs.getPieceAt(c.x - 1, c.y - 1) == Piece.BLACK)) {
				score += 6;
			}
			
			// We want our Muskovites to move closer to the king.
			int distanceToKing = tbs.getKingPosition().distance(c);
			score += distanceToKing != 0 ? 45/distanceToKing : 0;
			
			for(Coord k: Coordinates.getNeighbors(c)) {
				if(tbs.getPieceAt(k) == Piece.KING) {
					score += 30;
				} else if(tbs.getPieceAt(k) == Piece.BLACK) {
					score -= 2;
				}
			}
		}
	}
	
	/**
	 * A helper function to add white strategy considerations to the heuristic
	 * @param tbs
	 * @param score
	 */
	private static void evaluateWhite(TablutBoardState tbs, int score) {
		HashSet<Coord> coordinates = tbs.getOpponentPieceCoordinates();
		
		for(Coord c: coordinates ) {
			// Favor Diagonal Positioning to cover more ground
			if((c.x != 8 && c.y != 8 && tbs.getPieceAt(c.x + 1, c.y + 1) != null && tbs.getPieceAt(c.x + 1, c.y + 1) == Piece.BLACK)
					|| (c.y != 8 && c.x != 0 && tbs.getPieceAt(c.x - 1, c.y + 1) != null && tbs.getPieceAt(c.x - 1, c.y + 1) == Piece.WHITE)
					|| (c.x != 8 && c.y != 0 && tbs.getPieceAt(c.x + 1, c.y - 1) != null && tbs.getPieceAt(c.x + 1, c.y - 1) == Piece.WHITE)
					|| (c.x != 0 && c.y != 0 && tbs.getPieceAt(c.x - 1, c.y - 1) != null && tbs.getPieceAt(c.x - 1, c.y - 1) == Piece.WHITE)) {
				score += 6;
			}
			
			// We want our knights to stay close to the King
			int distanceToKing = tbs.getKingPosition().distance(c);
			score += distanceToKing != 0 ? 20/distanceToKing : 0;
			
			for(Coord k: Coordinates.getNeighbors(c)) {
				if(tbs.getPieceAt(k) == Piece.KING) {
					score += 15;
				} else if(tbs.getPieceAt(k) == Piece.BLACK) {
					score += 2;
				}
			}
		}
	}


	/**
	 * Create a Max Node for MiniMax
	 * @param tbs
	 * @param depth
	 * @param alpha
	 * @param beta
	 * @return
	 */
	public static MiniMaxNode max(TablutBoardState tbs, int depth, int alpha, int beta) {
		// If there is a winner, or we've reached our max depth
		// Evaluate the board and set the score to that node
		if (tbs.getWinner() != Board.NOBODY || depth == 0) {
			MiniMaxNode leaf = new MiniMaxNode(tbs);
			leaf.setScore(evaluateBoardState(tbs));
			return leaf;
		}

		MiniMaxNode max = null;
		for (TablutMove move : tbs.getAllLegalMoves()) {
			TablutBoardState clone = (TablutBoardState) tbs.clone();
			clone.processMove(move);

			MiniMaxNode result = min(clone, depth - 1, alpha, beta);
			if (max == null || result.getScore() > max.getScore()) {
				max = new MiniMaxNode(tbs);
				max.setScore(result.getScore());
				max.setMove(move);
			}

			alpha = Math.max(alpha, result.getScore());
			if (beta <= alpha)
				break;
		}

		return max;
	}

	/**
	 * Create a Min node for MiniMax
	 * @param tbs
	 * @param depth
	 * @param alpha
	 * @param beta
	 * @return
	 */
	public static MiniMaxNode min(TablutBoardState tbs, int depth, int alpha, int beta) {
		// If there is a winner, or we've reached our max depth
		// Evaluate the board and set the score to that node
		if (tbs.getWinner() != Board.NOBODY || depth == 0) {
			MiniMaxNode leafNode = new MiniMaxNode(tbs);
			leafNode.setScore(evaluateBoardState(tbs));
			
			return leafNode;
		}
		
		// Not a leafNode, look into its children
		MiniMaxNode min = null;
		for (TablutMove move : tbs.getAllLegalMoves()) {
			TablutBoardState clone = (TablutBoardState) tbs.clone();
			clone.processMove(move);

			MiniMaxNode result = max(clone, depth - 1, alpha, beta);
			if (min == null || result.getScore() < min.getScore()) {
				min = new MiniMaxNode(tbs);
				min.setScore(result.getScore());
				min.setMove(move);
			}

			beta = Math.min(beta, result.getScore());
			if (beta <= alpha)
				break;
		}

		return min;
	}

	
	public static Move generateBestMove(TablutBoardState tbs, int searchDepth, int player) {
		ai = player;

		MiniMaxNode max = max(tbs, searchDepth, -Integer.MAX_VALUE, Integer.MAX_VALUE);

		System.out.println("Selected " + max.getMove().toPrettyString() + " with score: " + max.getScore() );
		if (max.getMove() == null) {
			System.out.println("MinimaxStrategy returned null action. Falling back to RandomStrategy.");
			return tbs.getRandomMove();
		}

		return max.getMove();
	}

}
