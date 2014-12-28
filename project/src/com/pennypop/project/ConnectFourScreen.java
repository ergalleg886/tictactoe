package com.pennypop.project;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * This class is where the UI for the Connect 4 is located. The game board and
 * AIPlayer come together here. This is where the size of the board, the location of
 * the board, the "intelligence" of the AI, number of pieces needed to win and other
 * UI elements are picked. The class renders and draws what is necessary for the
 * Connect 4 game
 * 
 * @author Erik Gallegos
 *
 */
public class ConnectFourScreen implements Screen
{
	private final Stage gameStage;
	private final SpriteBatch spriteBatch;
	private Game game;
	private final BitmapFont font;

	//Variables for the information on the game board
	private int numRows;
	private int numColumns; 
	private int connectNumber;
	
	private GameBoard gameBoard;
	private ShapeRenderer boardLineShape;
	
	//variables for the rectangle to make the board
	private int rectangleWidth;
	private int rectangleHeight;
	private int rectangleLocX;
	private int rectangleLocY;
	
	private final TextureAtlas pieceAtlas;
	private final Skin pieceSkin;
	
	private Sprite redPieceSprite;
	private Sprite yellowPieceSprite;
	
	//Variable that controls whose turn it is
	private int playerTurn;
	//Message displayed on game
	private String turnMessage;
	
	//lets know when game is over to stop game
	private boolean gameOver;
	
	private AIPlayer aiPlayer;
	//Switch this to false to make a two player game with no AI
	private final boolean AI = true;

	
	public ConnectFourScreen(Game game){
		this.game = game;
		spriteBatch = new SpriteBatch();
		numRows = 6;
		numColumns = 7;
		connectNumber = 4;
		gameOver = false;
		//So these variables can easily be modified to change the AI
		final int PLY_LEVEL = 5;
		final String TIE_BREAKER_STRATEGY = "RANDOM"; 
		aiPlayer = new AIPlayer(2,TIE_BREAKER_STRATEGY,PLY_LEVEL);
		
		//choosing size and coordinates for rectangle
		rectangleWidth = 600;
		rectangleHeight = 400;
		rectangleLocX = 450;
		rectangleLocY = 200;
		
		gameBoard = new GameBoard(numRows,numColumns,connectNumber); 
		
		gameStage = new Stage(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),false,spriteBatch);
		
		font = new BitmapFont(Gdx.files.internal("font.fnt"),Gdx.files.internal("font.png"),false);
		
		pieceAtlas = new TextureAtlas(Gdx.files.internal("Buttons.pack"));
		pieceSkin = new Skin(pieceAtlas);
		pieceSkin.addRegions(pieceAtlas);

		redPieceSprite = new Sprite(pieceSkin.getRegion("red"));
		yellowPieceSprite = new Sprite(pieceSkin.getRegion("yellow"));
		
		turnMessage = "Player One Go!";
		playerTurn = 1;
		
		setGamePieceLocations();
		setTheBoard(Color.BLACK);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		gameStage.dispose();
		font.dispose();
		pieceAtlas.dispose();
		pieceSkin.dispose();
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		gameStage.act(delta);
		gameStage.draw();
		//if the game is over and the user clicks then screen should change
		if(Gdx.input.justTouched() && gameOver){
			game.setScreen(new MainScreen(game));
		}
		//let the system know when a move was successfully made
		boolean wasMoveSuccessful = false;
		//Play the next move. Player or AI
		wasMoveSuccessful = playerMove();
		//check for a winner
		if(wasMoveSuccessful && gameBoard.winsFor(playerTurn)){
			turnMessage = "Player " + playerTurn + " Wins!!!";
			gameOver = true;
		}
		//check for a tie
		else if(wasMoveSuccessful && gameBoard.isFull()){
			turnMessage = "Tie!";
			gameOver = true;
		}
		else{
			//will only switch if move was successful
			switchPlayers(wasMoveSuccessful);
		}
		//drawing the game board
		boardLineShape.begin(ShapeType.Line);
		drawBoard();
		boardLineShape.end();
		
		spriteBatch.begin();
		drawPieces();
		drawFont();
		spriteBatch.end();
	}
	
	public void drawFont(){
		if(gameOver){
			font.setColor(Color.BLACK);
		}
		else if(playerTurn == 1){
			font.setColor(Color.RED);
		}
		else if (playerTurn == 2){
			font.setColor(Color.YELLOW);
		}
		font.draw(spriteBatch, turnMessage, 100, 400);
	}

	@Override
	public void resize(int width, int height) {
		gameStage.setViewport(width, height, false);
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(gameStage);
	}
	
	private void setGamePieceLocations() {
		//Get the dimensions of the individual rectangles on the board
		int miniRecH = rectangleHeight/numRows;
		int miniRecW = rectangleWidth/numColumns;
		//Resize pieces according to the number of rows and columns
		resizePieces();
		//finding the center of the rectangles
		int centerX = miniRecW/2;
		int centerY = miniRecH/2;
		//finding where the GameBaordPiece coordinates for drawing will be
		int pieceLocX = (int)(centerX - redPieceSprite.getWidth()/2);
		int pieceLocY = (int)(centerY - redPieceSprite.getHeight()/2);
		
		int lineRowSpacer = rectangleLocY;
		int rowPadding = rectangleHeight/numRows;
		
		int lineColumnSpacer;
		int columnPadding = rectangleWidth/numColumns;
		//setting up the locations of the game pieces in the game screen
		for(int r = numRows-1; r >= 0; r--){
			lineColumnSpacer = rectangleLocX;
			for(int c = 0; c < numColumns; c++){
				gameBoard.getGameBoardPiece(r, c).setCoor(lineColumnSpacer+pieceLocX,lineRowSpacer+pieceLocY);
				lineColumnSpacer += columnPadding;
			}
			lineRowSpacer += rowPadding;
		}
		
	}
	
	private void resizePieces(){
		//get the dimensions of the individual rectangle on the board
		int miniRecH = rectangleHeight/numRows;
		int miniRecW = rectangleWidth/numColumns;
		//we need to know the dimensions of the pieces
		int spriteWidth = (int)redPieceSprite.getWidth();
		int spriteHeight = (int)redPieceSprite.getWidth();
		//if the piece is bigger than one of the dimensions then resize
		if(spriteWidth > miniRecW){
			//resize by making it fit snugly in the rectangle
			int diff = spriteWidth-miniRecW;
			redPieceSprite.setSize(spriteWidth-diff,spriteWidth-diff);
			yellowPieceSprite.setSize(spriteWidth-diff,spriteWidth-diff);
		}
		if(spriteHeight > miniRecH){
			int diff = (int)spriteHeight-miniRecH;
			redPieceSprite.setSize(spriteHeight-diff,spriteHeight-diff);
			yellowPieceSprite.setSize(spriteHeight-diff,spriteHeight-diff);
		}
		
	}

	private void setTheBoard(Color lineColor) {
		boardLineShape = new ShapeRenderer();
		boardLineShape.setColor(lineColor);
	}
	
	private void drawPieces(){
		GameBoardPiece checkPiece;
		
 		for(int r = 0; r < numRows; r++){
			for(int c = 0; c < numColumns; c++){
				checkPiece = gameBoard.getGameBoardPiece(r, c); 
				//if this piece belongs to player one
				if(checkPiece.getColor() == 1){
					redPieceSprite.setX(checkPiece.getCoorX());
					redPieceSprite.setY(checkPiece.getCoorY());
					redPieceSprite.draw(spriteBatch);
				}
				//this piece belongs to player 2
				else if(checkPiece.getColor() == 2){
					yellowPieceSprite.setX(checkPiece.getCoorX());
					yellowPieceSprite.setY(checkPiece.getCoorY());
					yellowPieceSprite.draw(spriteBatch);
				}
			}
		}
	}
	
	private void drawBoard(){
		int lineRowStart = rectangleLocY;
		int rowSpace = rectangleHeight/numRows;
		//draws the rows in the board
		for(int i = 0; i <= numRows; i++){
			boardLineShape.line(rectangleLocX, lineRowStart, rectangleLocX+rectangleWidth, lineRowStart);
			//moving on to the next row
			lineRowStart += rowSpace;
		}
		int lineColumnStart = rectangleLocX;
		int columnSpace = rectangleWidth/numColumns;
		//draws the columns in the board
		for(int i = 0; i <= numColumns; i++){
			boardLineShape.line(lineColumnStart, rectangleLocY, lineColumnStart, rectangleLocY+rectangleHeight);
			//moving on to the next column
			lineColumnStart += columnSpace;
		}
	}
	
	private boolean addChecker(int xCoor,int yCoor){
		//switching the coordinates to correspond with other coordinates
		int newY = Gdx.graphics.getHeight() - yCoor;
		boolean wasMoveSuccessful = false;
		//check if the click is in y boundaries
		if(newY < rectangleLocY || newY > rectangleLocY+rectangleHeight){
			return wasMoveSuccessful;
		}
		//the location of the column pressed
		int columnLocation = -1;
		//the width of the columns
		int columnSpace = rectangleWidth/numColumns;		
		int lineColumnStart =rectangleLocX;
		int lineColumnEnd = lineColumnStart + columnSpace;
		//check which column the click was made in
		for(int i = 0; i < numColumns; i++){
			//found the column the click was in
			if(xCoor > lineColumnStart && xCoor < lineColumnEnd){
				columnLocation = i;
				break;
			}
			//moving on to the next column
			lineColumnEnd += columnSpace;
			lineColumnStart += columnSpace;
		}
		//the click was not within the rectangle
		if(columnLocation == -1){
			//going to return false
			return wasMoveSuccessful;	
		}
		//uses the gameBoard's method to see if it can add the piece
		wasMoveSuccessful = gameBoard.addMove(columnLocation, playerTurn);
		return wasMoveSuccessful;		
	}
	
	private void switchPlayers(boolean wasMoveSuccessful){
		if(!wasMoveSuccessful){
			return;
		}
		if(playerTurn == 1){
			playerTurn = 2;
			turnMessage = "Player Two Go!";
		}
		else if(playerTurn == 2){
			playerTurn = 1;
			turnMessage = "Player One Go!";
		}
		
	}
	
	public boolean playerMove(){
		boolean wasMoveSuccessful = false;
		//Game is over so no more moves are allowed
		if(gameOver){
			return wasMoveSuccessful;
		}
		if(playerTurn == 1){
			if(Gdx.input.justTouched()){
				wasMoveSuccessful = addChecker(Gdx.input.getX(),Gdx.input.getY());	
			}
		}
		else if(AI && playerTurn == 2){
			int columnLocation = aiPlayer.nextMove(gameBoard);
			wasMoveSuccessful = gameBoard.addMove(columnLocation, playerTurn);
		}
		else{
			//If AI is deactivated then there is input for player 2
			if(Gdx.input.justTouched() && gameOver){
				this.dispose();
				game.setScreen(new MainScreen(game));
			}
			if(Gdx.input.justTouched()){
				wasMoveSuccessful = addChecker(Gdx.input.getX(),Gdx.input.getY());	
			}
		}
		return wasMoveSuccessful;
	}
	
	
}
