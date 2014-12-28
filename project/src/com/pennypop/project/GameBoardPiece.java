package com.pennypop.project;


/**
 * This class represents the individual pieces in the game board. It
 * contains information on what player it belongs to, the row and column number
 * it is located on the board and the x and y of the location in the screen
 * 
 * @author Erik Gallegos
 *
 */
public class GameBoardPiece {
	//Color of the piece represented by a 1 or 2 for player
	private int colorOfPiece;
	private int rowNumber;
	private int columnNumber;
	//Location on the screen for the game piece
	private int coorX;
	private int coorY;
	
	
	public GameBoardPiece(int argColor,int argRow, int argColumn){
		colorOfPiece = argColor;
		rowNumber = argRow;
		columnNumber = argColumn;
	}
	
	//The colors are represented by 1 and 2. A 0 is empty
	public void setColor(int argColor){
		colorOfPiece = argColor;
	}
	
	public int getColor(){
		return colorOfPiece;
	}
	
	public boolean isEmpty()
	{
		if(colorOfPiece == 0)
			return true;
		else
			return false;
	}
	
	public boolean isColor(int argColor){
		return colorOfPiece == argColor;
	}
	
	public int getRow(){
		return rowNumber;
	}
	
	public int getColumn(){
		return columnNumber;
	}
	
	public void setCoor(int argX, int argY){
		coorX = argX;
		coorY = argY;
	}
	
	public int getCoorX(){
		return coorX;
	}	
	
	public int getCoorY(){
		return coorY;
	}
}
