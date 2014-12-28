package com.pennypop.project;

import java.util.ArrayList;
import java.util.Random;

/**
 * This AI uses the "Deep Blue" strategy of AI by looking at moves ahead
 * to see which moves are better for the AIPlayer and to try and predict 
 * what moves the opponent will do. The lookahead or ply variable of the AI
 * is adjustable. The higher the ply the more into the "future" it can look
 * but slower the AIPlayer performs.
 * 
 * @author Erik Gallegos
 * */
public class AIPlayer {
	//the player to which the checker belongs to
	private int checker;
	//The approach to use for dealing with ties. "LEFT" "RIGHT" "RANDOM"
	private String tieBreakType;
	//the level of turns the AI will be able to look ahead
	private int ply;

	public AIPlayer(int argChecker, String argTieBreak, int argPly){
		checker = argChecker;
		tieBreakType = argTieBreak;
		ply = argPly;
	}
	
	//Method to return the checker number of the opponent
	public int oppColor(){
		if(checker == 1){
			return 2;
		}
			return 1;	
	}
	
	public double scoreBoard(GameBoard b){
		//if the board is at a win for this player return 100
		if(b.winsFor(checker)){
			return 100.0;
		}
		//if the board is at a win for the opposite player return 0
		if(b.winsFor(oppColor())){
			return 0.0;
		}
		//anything else is a neither a win or loss so return 50
		return 50.0;
	}
	
	//Method to deal with ties in the scoring system
	public int tieBreakMove(double[] scores){
		double maxScore = -2.0;
		for(int i = 0; i < scores.length; i++){
			if(scores[i] > maxScore){
				maxScore = scores[i];
			}
		}
		//An arraylist to store the indeces where the maxScore occurs
		ArrayList<Integer> maxIndeces = new ArrayList<Integer>();
		for(int i = 0; i < scores.length; i++){
			if(scores[i] == maxScore){
				maxIndeces.add(i);
			}
		}
		int pickedIndex = 0;
		/*could have used switch statements but didn't want to change project settings
		to allow me to use strings in case*/
		if(tieBreakType == "LEFT"){
			//use the leftmost index to break the tie
			pickedIndex = 0;			
		}
		else if(tieBreakType == "RIGHT"){
			//use the rightmost index to break the tie
			pickedIndex = maxIndeces.size()-1;			
		}
		//Random works best with the AI
		else if(tieBreakType == "RANDOM"){
			Random rand = new Random();
			//Pick the index by random
			pickedIndex = rand.nextInt(maxIndeces.size());
		}
		return maxIndeces.get(pickedIndex);			
	}
	
	/*
	 * Recursive method that goes through every column and sees what is the best move.
	 * A higher ply level allows the AI to look at more turns in the future and
	 * see what the opponent might do and how to respond to that
	 * 
	 * */
	public double[] scoresFor(GameBoard b){
		//Array representing the columns
		double[] scores = new double[b.getNumColumns()];
		//going through all the columns
		for(int c = 0; c < scores.length;c++){
			//a full column is not an option so it gets a score of -1
			if(b.isColumnFull(c)){
				scores[c] = -1.0;
			}
			//if there is a winning line then score appropriately
			else if(b.winsFor(checker) || b.winsFor(oppColor())){
				scores[c] = scoreBoard(b);
			}
			//everything else is a tie so it gets a score of 50
			else if(ply == 0){
				scores[c] = 50.0;
			}
			//if there are more ply levels then there is recursion
			else{
				//simulate adding the move by actually adding it on the board
				b.addMove(c,checker);
				//check for victory
				if(b.winsFor(checker)){
					scores[c] = 100.0;
				}
				//See how this affects the opponent
				else{
					AIPlayer opponent = new AIPlayer(oppColor(),tieBreakType,ply-1);
					double[] oppScore = opponent.scoresFor(b);
					double maxScore = -2.0;
					//See what is the best move for the opponent
					for(int i = 0; i < oppScore.length; i++){
						if(oppScore[i] > maxScore){
							maxScore = oppScore[i];
						}
					}
					//If opponent has a good move then we don't want to make this move
					scores[c] = 100.0-maxScore;	
				}
				//Make sure to delete the piece because it's only simulating
				b.delMove(c);
			}
		}
		return scores;
	}
	
	/*This method puts the other methods together so this is all that is needed to be
	called to make the next move*/
	public int nextMove(GameBoard b){
		double[] scores = scoresFor(b);
		return tieBreakMove(scores);
	}
}
