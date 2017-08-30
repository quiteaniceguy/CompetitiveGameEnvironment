import java.util.HashMap;
import java.util.Map;



public class CompetitiveGameEnvironment {
	public static boolean[] X_BOOL_VALUE = {true, false};
	public static boolean[] O_BOOL_VALUE = {false, true};
	public static boolean[] NULL_BOOL_VALUE = {true, true};
	
	///used to convert char values to 2 boolean values
	public static int CHAR_TO_BOOL_LENGTH = 2;
	
	public static int N_EXTRA_SENSORS = 2;
	
	//defines gameBoard dimensions
	public static int GAMEBOARD_WIDTH = 3;
	public static int GAMEBOARD_HEIGHT = 3;
	
	//start positions for players
	public static int PLAYER_ONE_START = 2;
	public static int PLAYER_TWO_START = 6;	
	
	//possible moves for agent, not sure what will happen if you change these
	final int[][] MOVES = { {-1, 0}, {1, 0}, {0, -1}, {0, 1}};
	
	//holds gameboard data
	private char[][] gameBoard = new char[GAMEBOARD_HEIGHT][GAMEBOARD_WIDTH]; 
	
	
	private int transitions[][];
	
	//possible agent moves
	private char[] alphabet = {'a', 'b', 'c', 'd'};
	
	public int playerOneCurrPostition;
	public int playerTwoCurrPosition;
	
	//holds both players positions and players score
	Map<Character, Integer> playerPos = new HashMap<Character, Integer>();
	Map<Character, Integer> playerScore = new HashMap<Character, Integer>();
	
	/**
	 * Initializes CompetitiveGameEnvironment for players 'x' and 'o'
	 */
	public CompetitiveGameEnvironment(){
		transitions = new int[GAMEBOARD_WIDTH * GAMEBOARD_HEIGHT][MOVES.length];
		setTranstions(transitions);
		
		
		//initialize player position and score
		playerPos.put('x', PLAYER_ONE_START);
		setGameboardSquare('x', PLAYER_ONE_START);
		
		playerPos.put('o', PLAYER_TWO_START);
		setGameboardSquare('o', PLAYER_TWO_START);
		
		playerScore.put('x', 0);
		playerScore.put('o', 0);
		
		
		
		
		
		////testing
		
		//printTransitions(transitions);
		/*
		setGameboardSquare('x', 2);
		setGameboardSquare('x', 5);
		setGameboardSquare('x', 8);
		setGameboardSquare('x', 3);
		*/
		/*
		tick('x', 'b');
		System.out.println(playerScore.get('o'));
		printGameBoard(gameBoard);
		*/
	}
	/**
	 * Sets state machine transitions according to the GAMEBOARD_WIDTH and GAMEBOARD_HEIGHT, allowing tesseract like moves wrapping around the game baord
	 * @param transitions
	 * 		int array to hold transition values
	 */
	private void setTranstions(int transitions[][]){
		for (int i = 0; i < transitions.length; i++){
			for(int j = 0; j < MOVES.length; j++)
				transitions[i][j] = loopAround(i % GAMEBOARD_WIDTH, MOVES[j][0], GAMEBOARD_WIDTH) + loopAround((int)i/GAMEBOARD_WIDTH * GAMEBOARD_WIDTH, MOVES[j][1] * GAMEBOARD_WIDTH, transitions.length);				
			
		}//for
		
	}
	
	/**
	 * Used to calculate the possible transitions for a position, including tesseract like moves.
	 * @param xPos
	 * @param transform
	 * @param width
	 * @return
	 */
	private int loopAround(int xPos, int transform, int width){
		if (xPos + transform < 0)
			return width + (transform % width);
		
		return (xPos +  transform) % width;
		
	}
	
	
	/**
	 * Prints each game state and its transitions to standard output
	 * @param transitions
	 */
	private void printTransitions(int transitions[][]){
		for (int i = 0; i < transitions.length; i++){
			System.out.println(i + " transitions: ");
			
			for(int j = 0; j < transitions[0].length; j++){
				System.out.print(transitions[i][j] + ", ");
			}
			System.out.println("");
			
		}
	}
	
	/**
	 * Prints the current game board to standard output
	 * @param gameBoard
	 */
	private void printGameBoard(char[][] gameBoard){
		for(int i = 0; i < gameBoard.length; i++){
			for(int j = 0; j < gameBoard[0].length; j++){
				System.out.print(gameBoard[i][j]);
			}
			System.out.println("");
		}
		System.out.println();
	}
	
	
	/**
	 * Converts the state value into a game board position and sets it to char c
	 * 
	 * @param c
	 * 		char to be set to
	 * @param intPosition
	 * 		state value
	 * @return
	 */
	private boolean setGameboardSquare(char c, int intPosition){
		if (gameBoard[(int)intPosition/GAMEBOARD_WIDTH][intPosition % GAMEBOARD_WIDTH] == c){
			return false;
		}
		gameBoard[(int)intPosition/GAMEBOARD_WIDTH][intPosition % GAMEBOARD_WIDTH] = c;
		return true;
	}
	
	/**
	 * Checks for vertical horizontal and diagonal victories for player cToCheck
	 * and returns positions part of the winning set
	 * 
	 * @param board
	 * @param cToCheck
	 * @return
	 */
	private char[][] checkForVictory(char[][] board, char cToCheck){
		char[][] returnValue = checkForDiagVictory(board, cToCheck);
		if(!boardIsEmpty(returnValue)) return returnValue;
		
		returnValue = checkForVertVictory(board, cToCheck);
		if(!boardIsEmpty(returnValue)) return returnValue;
		
		return checkForHorzVictory(board, cToCheck);
	}
	
	/**
	 * Checks for a vertical victory for player cToCheck on board 'board'
	 * @param board
	 * @param cToCheck
	 * @return
	 */
	private char[][] checkForVertVictory(char[][] board, char cToCheck){
		char[][] returnValue = new char[GAMEBOARD_HEIGHT][GAMEBOARD_WIDTH];
		for ( int i = 0; i < board[0].length; i++) {
			
			setBoardToEmpty(returnValue);
			
			for ( int j = 0; j < board.length; j++) {
				
				if ( !(board[j][i] == cToCheck) )
					break;
				returnValue[j][i] = 's';
				if ( j == board.length - 1)
					return returnValue;
				
			}
		}
		setBoardToEmpty(returnValue);
		return returnValue;
		
	}
	
	/**
	 * Checks for a vertical victory on board 'board' for player cToCheck
	 * @param board
	 * @param cToCheck
	 * @return
	 */
	private char[][] checkForHorzVictory(char[][] board, char cToCheck){
		
		char[][] returnValue = new char[GAMEBOARD_HEIGHT][GAMEBOARD_WIDTH];
		
		for ( int i = 0; i < board.length; i++) {
			setBoardToEmpty(returnValue);
			for ( int j = 0; j < board[0].length; j++) {
				if ( !(board[i][j] == cToCheck) )
					break;
				returnValue[i][j] = 's';
				if ( j == board[0].length - 1)
					return returnValue;
				
			}
		}
		setBoardToEmpty(returnValue);
		return returnValue;
	}
	
	/**
	 * Checks for a diagonal victory for player cToCheck on board 'board'
	 * @param board
	 * @param cToCheck
	 * @return
	 */
	private char[][] checkForDiagVictory(char[][] board, char cToCheck){
		char[][] returnValue = new char[GAMEBOARD_HEIGHT][GAMEBOARD_WIDTH];
		for ( int i = 0; i < board.length && i < board[0].length; i++){
			if( !(board[i][i] == cToCheck) )
				break;
			returnValue[i][i] = 's';
			if ( i == board[0].length - 1 || i == board.length - 1)
				return returnValue;
				
			
		}
		setBoardToEmpty(returnValue);
		return returnValue;
	}
	
	/**
	 * Sets all positions in give array 'board' to 0
	 * @param board
	 * board to to be emptied
	 */
	private void setBoardToEmpty(char[][] board){
		for (int i = 0; i < board.length; i++){
			for (int j = 0; j < board[0].length; j++){
				board[i][j] = 0;
			}
		}
	}
	
	/**
	 * Checks if board values are all 0
	 * @param board
	 * 		board to be checked
	 * @return
	 */
	private boolean boardIsEmpty(char[][] board){
		for (int i = 0; i < board.length; i++){
			for (int j = 0; j < board[0].length; j++){
				if (board[i][j] != 0) 
					return false;
			}
		}
		
		return true;
	}
	
	///board and spots to set must be the same width and height
	/**
	 * Sets all positions on board 'board to 0 according to those marked on board 'spotsToSet'
	 * @param board
	 * 		board to be altered
	 * @param spotsToSet
	 * 		non 0 values are positions on board 'board to be set to 0
	 */
	private void setBoardPosToEmpty(char[][] board, char[][] spotsToSet){
		for ( int i = 0; i < board.length; i++){
			for ( int j = 0; j < board[0].length; j++){
				if( spotsToSet[i][j] == 's')
					board[i][j] = 0;
			}
		}
	}
	
	/**
	 * Method which takes player given movement, make movement on board, and then supplies data back to player
	 * @param playerChar
	 * @param playerMove
	 * @return
	 */
	public boolean[] tick(char playerChar, char playerMove) {
		boolean[] returnSensors = new boolean[N_EXTRA_SENSORS + GAMEBOARD_WIDTH * GAMEBOARD_HEIGHT * CHAR_TO_BOOL_LENGTH + MOVES.length];
		
		///agent made mark
		returnSensors[0] = true;
		
		///agent got 3 in a row
		returnSensors[1] = false;
		
		//try to make new mark
		playerPos.put(playerChar, transitions[playerPos.get(playerChar)][findAlphabetIndex(playerMove)]);
		if(!setGameboardSquare(playerChar, playerPos.get(playerChar))){
			returnSensors[0] = false;
		}
		
		//checks for victories, clears board of victory positions, and updates score
		char[][] posToEmpty = checkForVictory(gameBoard, playerChar);
		boolean scored = !boardIsEmpty(posToEmpty);
		
		setBoardPosToEmpty(gameBoard, posToEmpty);
		if (scored) {
			playerScore.put(playerChar, playerScore.get(playerChar) + 1);
			returnSensors[1] = true;
		}
	
		
		///return boolean value of gameboard( put in method?)
		for (int i = 0; i < GAMEBOARD_HEIGHT; i++){
			for ( int j = 0; j < GAMEBOARD_WIDTH; j++){
				
				boolean[] sensorValue = NULL_BOOL_VALUE;
				
				switch(gameBoard[i][j]){
				case 'x':
					sensorValue = X_BOOL_VALUE;
					break;
				case 'o':
					sensorValue = O_BOOL_VALUE;
					break;
				}
				
				for( int z = 0; z < CHAR_TO_BOOL_LENGTH; z++){
					returnSensors[N_EXTRA_SENSORS + (i * GAMEBOARD_WIDTH + j) * 2 + z] = sensorValue[z];
				}
			}
			
		}
		
		//
		char oppositeChar = playerChar == 'x' ? 'o': 'x'; 
		for( int i = 0; i < alphabet.length; i++){
			if ( transitions[playerPos.get(playerChar)][i] == playerPos.get(oppositeChar))
				returnSensors[returnSensors.length - 1 - i] = true;
			else
				returnSensors[returnSensors.length - 1 - i] = false;
			
		}
		
		
		
		//printSensors(returnSensors);
		//printGameBoard(gameBoard);
		return returnSensors;
	}
	
	/**
	 * helper method that finds the index of letter 'letter' in array 'alphabet'
	 * @param letter
	 * @return
	 */
	private int findAlphabetIndex(char letter) {
		for(int i = 0; i < alphabet.length; i++){
			if(alphabet[i] == letter)
				return i;
		}
		return -1;
		
	}
	
	/**
	 * Prints boolean array as list of 1s and 0s, where 1 is true and 0 is false
	 * @param boolArray
	 */
	private void printSensors(boolean[] boolArray){
		System.out.println();
		for ( int i = 0; i < boolArray.length; i++) {
			if (i % 2 == 0)
				System.out.print(" ");
			
			if (boolArray[i])
				System.out.print("1");
			
			else
				System.out.print("0");
			
			
		}
		System.out.println();
	}
	
	/**
	 * helper method that returne alphabet
	 * @return
	 */
	public char[] getAlphabet(){
		return alphabet;
	}
	
	public static void main(String args[]){
		CompetitiveGameEnvironment game = new CompetitiveGameEnvironment();
	}
	
	
	
}
