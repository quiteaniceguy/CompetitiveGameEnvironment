import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RunCompetitiveEnvironment {
	public static final int N_GAME_ENVIRONMENTS = 10;
	public static final int N_SUCCESSES = 1000;
	
	public static CompetitiveGameEnvironment env;
	
	public static Agent agentOne;
	public static Agent agentTwo;
	
	public static String outputOne = "n: ";
	public static String outputTwo = "m: ";
	
	public static Map<Character, String> outputValues = new HashMap<Character, String>();
	
	public static void main(String args[]){
		
		///initialize output values for x and o
		outputValues.put('x', "");
		outputValues.put('o', "");
		
		String fileName= getCurrentDate() + ".csv";
		
		for ( int i = 0; i < N_GAME_ENVIRONMENTS; i++){
			System.out.println();
			System.out.println("Starting environment number: " +  String.valueOf(i + 1) );
			
			env = new CompetitiveGameEnvironment();
			exploreEnvironmentWithTwoAgents();
			
			writeToFile(fileName, outputValues.get('x'));
			writeToFile(fileName, outputValues.get('o'));
			
			System.out.println("");
			System.out.println(outputValues.get('x'));
			System.out.println(outputValues.get('o'));
			
			
			//reset outputValues
			outputValues.put('x', "");
			outputValues.put('o', "");
		}
		System.out.println("GAME ENDED");
	}
	
	public static void exploreEnvironmentWithTwoAgents(){
		agentOne = new NSMAgent('x');
		agentTwo = new NSMAgent('o');
		
		//nsm.setSensorOutputRandom(nsm.prevStateSensorOutput);
		
		while (agentOne.episodicMemory.size() < agentOne.MAX_EPISODES && agentOne.Successes <= agentOne.NUM_GOALS && agentTwo.memory.length() < agentTwo.MAX_EPISODES && agentTwo.Successes <= agentTwo.NUM_GOALS) { 
			
			///MaRz exploreEnvironment also calls NSM exploreEnvironment
			agentTwo.exploreEnvironment();
			agentOne.exploreEnvironment();
			
			
			
			
			//nsm.exploreEnvironment();
			
			
		}
		
	}
	
	public static void agentOneExploreEnvironment(){
		agentOne.exploreEnvironment();
	}
	
	public static void writeToFile(String fileName, String output){
		try{
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true), "UTF-8"));
			writer.append(output + "\n");
			writer.flush();
			writer.close();
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}
	
	public static String getCurrentDate(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date date = new Date();
		
		return dateFormat.format(date);
	}
}
