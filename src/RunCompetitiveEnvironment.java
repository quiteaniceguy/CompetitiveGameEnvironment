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
	public static final int N_GAME_ENVIRONMENTS = 1;
	public static final int N_SUCCESSES = 1000;
	
	public static CompetitiveGameEnvironment env;
	
	public static Agent agentOne;
	public static Agent agentTwo;
	
	public static boolean agentOneDone;
	public static boolean agentTwoDone; 
	
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
			/*
			System.out.println("writing to files");
			writeToFile(fileName, outputValues.get('x'));
			writeToFile(fileName, outputValues.get('o'));
			*/
			System.out.println("");
			System.out.println(outputValues.get('x'));
			System.out.println(outputValues.get('o'));
			
			
			//reset outputValues
			outputValues.put('x', "");
			outputValues.put('o', "");
			
		}
		System.out.println("GAME ENDED");
	}
	
	
	/**
	 * Runs the two previously defined agents in the competitive game environment
	 */
	public static void exploreEnvironmentWithTwoAgents(){
		agentOne = new NSMAgent('x');
		agentTwo = new NSMAgent('o');
		
		//nsm.setSensorOutputRandom(nsm.prevStateSensorOutput);
		RunCompetitiveEnvironment compEnv = new RunCompetitiveEnvironment();
		 
			
		///MaRz exploreEnvironment also calls NSM exploreEnvironment
		LockObject lock = compEnv.new LockObject();
		Thread runnerOne = new Thread(compEnv.new AgentOneRunner(lock));
		Thread runnerTwo = new Thread(compEnv.new AgentTwoRunner(lock));
		
		runnerTwo.start();
		runnerOne.start();
		try {
			runnerTwo.join();
			runnerOne.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("\nExploring completed\n");	
	}
	
	
	

	
	/**
	 * Runs agentOne in competitive environment 
	 * @author tommyeblen
	 *
	 */
	public class AgentOneRunner implements Runnable{
		LockObject obj;
		public AgentOneRunner(LockObject obj){
			this.obj = obj;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized(obj){
			
				
				while (agentOne.episodicMemory.size() < agentOne.MAX_EPISODES && agentOne.Successes <= agentOne.NUM_GOALS){
					agentOne.exploreEnvironment();
					
					
					//notifies other agent
					obj.notifyAll();
					
					//breaks out of loop if complete
					if(!(agentOne.episodicMemory.size() < agentOne.MAX_EPISODES && agentOne.Successes <= agentOne.NUM_GOALS)){
						obj.agentOneDone = true;
						System.out.println("agent one done");
						break;
					}
					
					//waits for other agent to move
					try {
						if(!obj.agentTwoDone)
							obj.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("agent one got out");
				
				
			}
		}
		
	}
	/**
	 * Runs agentTwo in competitive environment 
	 * @author tommyeblen
	 *
	 */
	public class AgentTwoRunner implements Runnable{
		
		LockObject obj;
		public AgentTwoRunner(LockObject obj){
			this.obj = obj;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized(obj){
				
			
				while (agentTwo.episodicMemory.size() < agentTwo.MAX_EPISODES && agentTwo.Successes <= agentTwo.NUM_GOALS){
					agentTwo.exploreEnvironment();
					
					
					///Notifies other agent 
					obj.notifyAll();
					
					//breaks out of loop if agent is completed
					if(!(agentTwo.episodicMemory.size() < agentTwo.MAX_EPISODES && agentTwo.Successes <= agentTwo.NUM_GOALS)){
						obj.agentTwoDone = true;
						System.out.println("agent two done");
						break;
					}
					//Waits for other agents move completion
					try {
						if(!obj.agentOneDone)
							obj.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}//while
				System.out.println("agent two got out");
				
			}
		}
		
	}
	
	/**
	 * Class used for lock and to hold current agents' status
	 * @author tommyeblen
	 *
	 */
	public class LockObject{
		boolean agentOneDone = false;
		boolean agentTwoDone = false;
	}

	
	
	
	

	
	
	/**
	 * Writes 'output' string to file with 'fileName' in the current directory
	 * @param fileName
	 * @param output
	 * 		what to write to file of given file name
	 */
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
	
	
	/**
	 * Returns current date and time in 'yyyy_MM_dd_HH_mm_ss' format
	 * @return
	 */
	public static String getCurrentDate(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date date = new Date();
		
		return dateFormat.format(date);
	}
}
