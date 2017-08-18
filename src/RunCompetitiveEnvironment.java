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
	
	public static Agent nsm;
	public static Agent marz;
	
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
		nsm = new MaRzAgent('x');
		marz = new MaRzAgent('o');
		
		//nsm.setSensorOutputRandom(nsm.prevStateSensorOutput);
		
		while (nsm.episodicMemory.size() < nsm.MAX_EPISODES && nsm.Successes <= nsm.NUM_GOALS && marz.memory.length() < marz.MAX_EPISODES && marz.Successes <= marz.NUM_GOALS) { 
			
			///MaRz exploreEnvironment also calls NSM exploreEnvironment
			marz.exploreEnvironment();
			nsm.exploreEnvironment();
			
			
			System.out.println(nsm.Successes);
			
			
			//nsm.exploreEnvironment();
			
			
		}
		
	}
	
	public static void nsmExploreEnvironment(){
	 nsm.exploreEnvironment();
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
