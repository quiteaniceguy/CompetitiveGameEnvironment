import java.util.HashMap;
import java.util.Map;

public class CompetitiveGameEnvironment {
	public static boolean[] X_BOOL_VALUE = {true, false};
	public static boolean[] O_BOOL_VALUE = {false, true};
	public static boolean[] NULL_BOOL_VALUE = {true, true};
	
	public static int CHAR_TO_BOOL_LENGTH = 2;
	
	public static int N_EXTRA_SENSORS = 2;
	
	public static int GAMEBOARD_WIDTH = 3;
	public static int GAMEBOARD_HEIGHT = 3;
	
	public static int PLAYER_ONE_START = 2;
	public static int PLAYER_TWO_START = 6;	
	
	final int[][] MOVES = { {-1, 0}, {1, 0}, {0, -1}, {0, 1}};
	
	private char[][] gameBoard = new char[GAMEBOARD_HEIGHT][GAMEBOARD_WIDTH]; 
	
	
	private int transitions[][];
	private char[] alphabet = {'a', 'b', 'c', 'd'};
	
	public int playerOneCurrPostition;
	public int playerTwoCurrPosition;
	
	Map<Character, Integer> playerPos = new HashMap<Character, Integer>();
	Map<Character, Integer> playerScore = new HashMap<Character, Integer>();
	
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
		printGameBoard(gameBoard);
		//printGameBoard(checkForVictory(gameBoard, 'x'));
		//setBoardPosToEmpty(gameBoard, checkForVictory(gameBoard, 'x'));
		tick('x', 'a');
		System.out.println(playerScore.get('x'));
		printGameBoard(gameBoard);
		
		tick('x', 'c');
		System.out.println(playerScore.get('x'));
		printGameBoard(gameBoard);
		
		tick('x', 'c');
		System.out.println(playerScore.get('x'));
		printGameBoard(gameBoard);
		
		tick('x', 'a');
		System.out.println(playerScore.get('x'));
		printGameBoard(gameBoard);
		
		tick('x', 'b');
		System.out.println(playerScore.get('x'));
		printGameBoard(gameBoard);
		
		tick('o', 'b');
		System.out.println(playerScore.get('o'));
		printGameBoard(gameBoard);
		
		tick('x', 'b');
		System.out.println(playerScore.get('o'));
		printGameBoard(gameBoard);
		*/
	}
	
	private void setTranstions(int transitions[][]){
		for (int i = 0; i < transitions.length; i++){
			for(int j = 0; j < MOVES.length; j++)
				transitions[i][j] = loopAround(i % GAMEBOARD_WIDTH, MOVES[j][0], GAMEBOARD_WIDTH) + loopAround((int)i/GAMEBOARD_WIDTH * GAMEBOARD_WIDTH, MOVES[j][1] * GAMEBOARD_WIDTH, transitions.length);				
			
		}//for
		
	}
	
	private int loopAround(int xPos, int transform, int width){
		if (xPos + transform < 0)
			return width + (transform % width);
		
		return (xPos +  transform) % width;
		
	}
	
	
	
	private void printTransitions(int transitions[][]){
		for (int i = 0; i < transitions.length; i++){
			System.out.println(i + " transitions: ");
			
			for(int j = 0; j < transitions[0].length; j++){
				System.out.print(transitions[i][j] + ", ");
			}
			System.out.println("");
			
		}
	}
	
	private void printGameBoard(char[][] gameBoard){
		for(int i = 0; i < gameBoard.length; i++){
			for(int j = 0; j < gameBoard[0].length; j++){
				System.out.print(gameBoard[i][j]);
			}
			System.out.println("");
		}
		System.out.println();
	}
	
	private boolean setGameboardSquare(char c, int intPosition){
		if (gameBoard[(int)intPosition/GAMEBOARD_WIDTH][intPosition % GAMEBOARD_WIDTH] == c){
			return false;
		}
		gameBoard[(int)intPosition/GAMEBOARD_WIDTH][intPosition % GAMEBOARD_WIDTH] = c;
		return true;
	}
	
	private char[][] checkForVictory(char[][] board, char cToCheck){
		char[][] returnValue = checkForDiagVictory(board, cToCheck);
		if(!boardIsEmpty(returnValue)) return returnValue;
		
		returnValue = checkForVertVictory(board, cToCheck);
		if(!boardIsEmpty(returnValue)) return returnValue;
		
		return checkForHorzVictory(board, cToCheck);
	}
	
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
	
	
	private void setBoardToEmpty(char[][] board){
		for (int i = 0; i < board.length; i++){
			for (int j = 0; j < board[0].length; j++){
				board[i][j] = 0;
			}
		}
	}
	
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
	private void setBoardPosToEmpty(char[][] board, char[][] spotsToSet){
		for ( int i = 0; i < board.length; i++){
			for ( int j = 0; j < board[0].length; j++){
				if( spotsToSet[i][j] == 's')
					board[i][j] = 0;
			}
		}
	}
	
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
	
	private int findAlphabetIndex(char letter) {
		for(int i = 0; i < alphabet.length; i++){
			if(alphabet[i] == letter)
				return i;
		}
		return -1;
		
	}
	
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
	
	public char[] getAlphabet(){
		return alphabet;
	}
	
	public static void main(String args[]){
		CompetitiveGameEnvironment game = new CompetitiveGameEnvironment();
	}
	
	
	
}
