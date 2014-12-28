package com.pennypop.project;

/**
 * This class creates a GameBoard object that is used to represent a the
 * board for a Connect 4 game. It uses a 2d array to represent the 
 * GameBoard pieces. The rows and columns can be can be set as well as
 * the number of pieces needed to win the game
 * 
 * @author Erik Gallegos
 * */
public class GameBoard {
	
	private GameBoardPiece[][] board;
	private int numRows;
	private int numColumns;
	private int connectNumber;
	
	public GameBoard(int rows, int columns,int argConnectNumber){
		numRows = rows;
		numColumns = columns;
		connectNumber = argConnectNumber;
		board = new GameBoardPiece[rows][columns];
		for(int r = 0 ;r < numRows; r++){
			for(int c = 0; c < numColumns; c++){
				board[r][c] = new GameBoardPiece(0,r,c);		
			}	
		}
	}
	
	public GameBoardPiece getGameBoardPiece(int row, int column){
		return board[row][column];
	}
	
	public String toString(){
		String str = "";
		for(int r = 0 ;r < numRows; r++){
			for(int c = 0; c < numColumns; c++){
				str += board[r][c].getColor()+" ";	
			}
			str += "\n";
		}
		return str;
	}
	
	//Method to set the board for testing purposes
	public void setBoard(String moveString){
		int nextColor = 1;
		for(int i = 0; i < moveString.length(); i ++){
			int col = Character.getNumericValue(moveString.charAt(i));
			if(0 <= col && col < numColumns){
				addMove(col, nextColor);
			}
			if(nextColor == 1){
				nextColor = 2;
			}
			else{
				nextColor  = 1;
			}
		}
	}
	
	public boolean addMove(int column, int checker){
		//check to see if it's a legal move
		if(!allowsMove(column)){
			return false;
		}
		//accounting for the way rows are in a 2d array
		for(int i = numRows-1; i >= 0;i--){
			if(board[i][column].isEmpty()){
				//changing the piece to the player's piece
				board[i][column].setColor(checker);
				break;
			}
		}
		return true;
	}
	
	public boolean allowsMove(int column){
		//out of range
		if(column < 0 || column >= numColumns)
			return false;
		//if the top row is occupied then the column is full
		if(!board[0][column].isEmpty()){
			return false;
		}
		return true;
	}
	
	//Method important for AI
	public void delMove(int column){
		for(int r = 0; r < numRows; r++){
			if(!board[r][column].isEmpty()){
				//make it an empty piece
				board[r][column].setColor(0);
				break;
			}
		}
	}
	
	public boolean isFull(){
		//check to see if the top row is full
		for(int c = 0; c < numColumns; c++){
			//there is space in the row so the board isn't full
			if(board[0][c].isEmpty()){
				return false;
			}
		}
		return true;
	}
	
	public boolean isColumnFull(int column){
		//if the top piece of that column is not empty then it is full
		if(!board[0][column].isEmpty()){
			return true;
		}
		return false;
	}
	
	
	public boolean winsFor(int checker){
		if(checkHorizontal(checker))
			return true;
		if(checkVertical(checker))
			return true;
		if(checkDiagonalRight(checker))
			return true;
		if(checkDiagonalLeft(checker))
			return true;
		return false;
	}
	
	private boolean checkHorizontal(int checker){
		for (int r = 0; r < numRows; r++){
			for (int c = 0; c < numColumns-(connectNumber-1); c++){
				int piecesConnected = 0;
				//keep checking until you have found connecNumber of pieces together
				while(piecesConnected < connectNumber && !board[r][c+piecesConnected].isEmpty()){
					//the piece is not the same color so it ruins that line
					if (!board[r][c+piecesConnected].isColor(checker)){
						break;
					}
					//you have completed a line
					if(piecesConnected == connectNumber-1){
						return true;
					}
					piecesConnected++;
				}
			}
	    }
		return false;
	}
	
	private boolean checkVertical(int checker){
		for (int c = 0; c < numColumns; c++){
			for (int r = 0; r < numRows-(connectNumber-1); r++){
				int piecesConnected = 0;
				//keep checking until you have found connecNumber of pieces together
				while(piecesConnected < connectNumber && !board[r+piecesConnected][c].isEmpty()){
					//the piece is not the same color so it ruins that line
					if (!board[r+piecesConnected][c].isColor(checker)){
						break;
					}
					//you have completed a line
					if(piecesConnected == connectNumber-1){
						return true;
					}
					piecesConnected++;
				}

			}
	    }
		return false;
	}

	private boolean checkDiagonalRight(int checker){
		for (int c = 0; c < numColumns-(connectNumber-1); c++){
			for (int r = 0; r < numRows-(connectNumber-1); r++){
				int piecesConnected = 0;
				//keep checking until you have found connecNumber of pieces together
				while(piecesConnected < connectNumber && !board[r+piecesConnected][c+piecesConnected].isEmpty()){
					//the piece is not the same color so it ruins that line
					if (!board[r+piecesConnected][c+piecesConnected].isColor(checker)){
						break;
					}
					//you have completed a line
					if(piecesConnected == connectNumber-1){
						return true;
					}
					piecesConnected++;
				}
			}
	    }
		return false;
	}
	
	private boolean checkDiagonalLeft(int checker){
		for (int c = (connectNumber-1); c < numColumns; c++){
			for (int r = 0; r < numRows-(connectNumber-1); r++){
				int piecesConnected = 0;
				//keep checking until you have found connecNumber of pieces together
				while(piecesConnected < connectNumber && !board[r+piecesConnected][c-piecesConnected].isEmpty()){
					//the piece is not the same color so it ruins that line
					if (!board[r+piecesConnected][c-piecesConnected].isColor(checker)){
						break;
					}
					//you have completed a line
					if(piecesConnected == connectNumber-1){
						return true;
					}
					piecesConnected++;
				}
			}
	    }
		return false;
	}
	
	public int getNumRows(){		
		return numRows;
	}
	
	public int getNumColumns(){
		return numColumns;
	}
	
}
