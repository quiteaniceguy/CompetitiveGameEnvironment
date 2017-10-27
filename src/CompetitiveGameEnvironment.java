import java.util.HashMap;
import java.util.Map;
import java.util.Random;



public class CompetitiveGameEnvironment {
	public boolean[] X_BOOL_VALUE = {true, false};
	public boolean[] O_BOOL_VALUE = {false, true};
	public boolean[] NULL_BOOL_VALUE = {true, true};
	
	///used to convert char values to 2 boolean values
	public int CHAR_TO_BOOL_LENGTH = 2;
	
	public int N_EXTRA_SENSORS = 2;
	
	//defines gameBoard dimensions
	public int GAMEBOARD_WIDTH = 3;
	public int GAMEBOARD_HEIGHT = 3;
	
	public int[][] GAMEBOARD_LAYOUT = { {1,1,1},
										{1,1,1},
										{1,1,1}
	};
	
	///squares that will be blocked to agents
	public int N_BLOCKED_SQUARES = 1;
	public double BLOCKED_CHANGE_CHANCE = .05;
	

	
	//start positions for players
	public int PLAYER_ONE_START = 2;
	public int PLAYER_TWO_START = 6;	
	
	//possible moves for agent, keep these as one for now; otherwise, loopAround function will be wack
	final int[][] MOVES = { {1, 0}, {-1, 0}, {0, 1}, {0, -1}};
	
	//holds gameboard data
	private char[][] gameBoard = new char[GAMEBOARD_HEIGHT][GAMEBOARD_WIDTH]; 
	
	///output options
	public final boolean DISPLAY_BOARD_MOVES = false;
	
	private int transitions[][];
	
	//possible agent moves
	private char[] alphabet = {'a', 'b', 'c', 'd'};
	
	public int playerOneCurrPostition;
	public int playerTwoCurrPosition;
	
	//holds both players positions and players score
	Map<Character, Integer> playerPos = new HashMap<Character, Integer>();
	Map<Character, Integer> playerScore = new HashMap<Character, Integer>();
	
	//
	public int[] squaresToBlock = {4};
	
	Random random = new Random();
	/**
	 * Initializes CompetitiveGameEnvironment for players 'x' and 'o'
	 */
	public CompetitiveGameEnvironment(){
		transitions = new int[GAMEBOARD_LAYOUT.length * GAMEBOARD_LAYOUT[0].length][MOVES.length];
		setTranstions(transitions);
		printTransitions(transitions);
		
		
		Random rand = new Random();
		
		//initialize player position and score
		
		int startPosOne;
		int startPosTwo;
		
		//set x
		while(transitions[(startPosOne = rand.nextInt(transitions.length))][0] == -1)
			continue;
		playerPos.put('x', startPosOne);
		setGameboardSquare('x', startPosOne);
		
		//set o
		while(transitions[(startPosTwo = rand.nextInt(transitions.length))][0] == -1 || startPosTwo == startPosOne)
			continue;
		playerPos.put('o', startPosTwo);
		setGameboardSquare('o', startPosTwo);
		
		//set both player scores
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
		int i = 0;
		
		int gameBoardWidth = GAMEBOARD_LAYOUT[0].length;
		while (i < transitions.length){
			
			
				
			for(int j = 0; j < MOVES.length; j++){
				
				if(GAMEBOARD_LAYOUT[(int)i / gameBoardWidth][i % gameBoardWidth] == 1){
					int[] transform = {MOVES[j][0], MOVES[j][1]};
					System.out.println("transform " + j+ ": " + transform[0] + ", " + transform[1]);
					int x;
					int y;
					
				
					///if the board position doesn't exist, continue in the direction of the current move/vector
					while( GAMEBOARD_LAYOUT
							[y =  loopAround(((int)i/GAMEBOARD_WIDTH) * GAMEBOARD_WIDTH, transform[1] * GAMEBOARD_WIDTH, transitions.length)/GAMEBOARD_WIDTH]
							[x =  loopAround(i % GAMEBOARD_WIDTH, transform[0], GAMEBOARD_WIDTH) ]
							!= 1)
					{
						transform[0] += MOVES[j][0];
						transform[1] += MOVES[j][1];
					}
					
			
					//System.out.println("transitions " + i + "     move " + j + " to " + String.valueOf(x+y) + "     x value " + x + " y value " + y + "    loop around input" + (i % GAMEBOARD_WIDTH)+ "  " + transform[0] + "   " + gameBoardWidth );
					transitions[i][j] = x + y * GAMEBOARD_WIDTH;
				
				}else{
					transitions[i][j] = -1;
				}
				
			}
			i++;
			
		}//fortransitions[i][j]
		
	}
	
	/**
	 * Used to calculate the possible transitions for a position, including tesseract like moves.
	 * @param pos
	 * @param transform
	 * @param width
	 * @return
	 */
	private int loopAround(int pos, int transform, int width){
		
		if (pos + transform < 0){
			//System.out.println("is negative");
			return (width - (Math.abs(pos + transform) % width)) % width;
		}
		//System.out.println("is positive");
		return (pos +  transform) % width;
		
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
	 * Gets the number of 1's in int array
	 * @param gameBoard
	 * @return
	 */
	private int getNStates(int[][] gameBoard){
		int nStates = 0;
		for(int i[]: gameBoard)
			for(int j: i)
				if (j == 1) nStates++;
			
		return nStates;
		
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
	
	private void setRandomBoardBlocks(int[] blocks, char[][] board, int nBlocks){
		for (int i = 0; i < nBlocks; i++ ) {
			blocks[i] = (int)(Math.random() * (board.length * board[0].length));
		}
	}
	
	/**
	 * Checks for vertical horizontal and diagonal victories for player cToCheck
	 * and returns positions part of the winning set
	 * 
	 * @param board
	 * @param cToCheck
	 * @return
	 */
	private char[][] checkForVictory(char[][] board, char[] cToCheck){
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
	private char[][] checkForVertVictory(char[][] board, char[] cToCheck){
		char[][] returnValue = new char[GAMEBOARD_HEIGHT][GAMEBOARD_WIDTH];
		
		boolean breakLoopThrough = true;
		
		for ( int i = 0; i < board[0].length; i++) {
			
			setBoardToEmpty(returnValue);
			
			loopThroughVert:
			for ( int j = 0; j < board.length; j++) {
				
				breakLoopThrough = true;
					
				for( char c: cToCheck)
					if( (board[j][i] == c) )
						breakLoopThrough = false;
				
				if (breakLoopThrough) break loopThroughVert;
				
				
				
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
	private char[][] checkForHorzVictory(char[][] board, char[] cToCheck){
		
		char[][] returnValue = new char[GAMEBOARD_HEIGHT][GAMEBOARD_WIDTH];
		
		boolean breakLoopThrough = true;
		
		for ( int i = 0; i < board.length; i++) {
			setBoardToEmpty(returnValue);
			
			loopThroughHorz:
			for ( int j = 0; j < board[0].length; j++) {
				
				breakLoopThrough = true;
			
				for( char c: cToCheck)
					if( (board[i][j] == c) )
						breakLoopThrough = false;
				
				if (breakLoopThrough) break loopThroughHorz;
				
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
	private char[][] checkForDiagVictory(char[][] board, char[] cToCheck){
		char[][] returnValue = new char[GAMEBOARD_HEIGHT][GAMEBOARD_WIDTH];
		
		boolean breakLoopThrough = true;
		
		loopThroughBoard:
		for ( int i = 0; i < board.length && i < board[0].length; i++){
			
			
				breakLoopThrough = true;
			
				for( char c: cToCheck)
					if( (board[i][i] == c) )
						breakLoopThrough = false;
				
				if (breakLoopThrough) break loopThroughBoard;
					
						
			
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
		if(DISPLAY_BOARD_MOVES){
			System.out.println("////////////////////////\nPLAYER: " + playerChar);
			printGameBoard(gameBoard);
		}
		boolean[] returnSensors = new boolean[N_EXTRA_SENSORS + GAMEBOARD_WIDTH * GAMEBOARD_HEIGHT * CHAR_TO_BOOL_LENGTH + MOVES.length + GAMEBOARD_WIDTH * GAMEBOARD_HEIGHT];
		
		///agent made mark
		returnSensors[0] = true;
		
		///agent got 3 in a row
		returnSensors[1] = false;
		
		//check to see if agent moving into blocked square.
		boolean blockMove = false;
		for(int i: squaresToBlock){
			if (i == transitions[playerPos.get(playerChar)][findAlphabetIndex(playerMove)]){
				blockMove = true;
			}
		}
		
		//try to make new mark
		if(!blockMove){
			playerPos.put(playerChar, transitions[playerPos.get(playerChar)][findAlphabetIndex(playerMove)]);
			if(!setGameboardSquare(playerChar, playerPos.get(playerChar))){
				returnSensors[0] = false;
			}
		}
		
		//checks for victories, clears board of victory positions, and updates score
		char[] charsToCheck = {playerChar};
		char[][] posToEmpty = checkForVictory(gameBoard, charsToCheck);
		boolean scored = !boardIsEmpty(posToEmpty);
		
		if(scored){
			/*
			System.out.println("Scored:  ");
			printGameBoard(posToEmpty);
			*/
		}
			
		
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
		
		//put sensors for blocks in
		for (int i: squaresToBlock){
			returnSensors[N_EXTRA_SENSORS + GAMEBOARD_WIDTH * GAMEBOARD_HEIGHT * CHAR_TO_BOOL_LENGTH + MOVES.length + i] = true;
		}
		
		//
		char oppositeChar = playerChar == 'x' ? 'o': 'x'; 
		for( int i = 0; i < alphabet.length; i++){
			if ( transitions[playerPos.get(playerChar)][i] == playerPos.get(oppositeChar))
				returnSensors[returnSensors.length - 1 - i] = true;
			else
				returnSensors[returnSensors.length - 1 - i] = false;
			
		}
		
		//check to see if square blocks should be changes
		if (random.nextDouble() < BLOCKED_CHANGE_CHANCE) {
			setRandomBoardBlocks(squaresToBlock, gameBoard, N_BLOCKED_SQUARES);
			System.out.println("blocked square = " + squaresToBlock[0]);
		}
		
		
		//printSensors(returnSensors);
		if(DISPLAY_BOARD_MOVES){
			printGameBoard(gameBoard);
			System.out.println("Player Pos: " + playerPos.get(playerChar));
		}
			
		
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
