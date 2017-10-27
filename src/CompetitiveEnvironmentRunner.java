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


public class CompetitiveEnvironmentRunner {
	public static final int N_GAME_ENVIRONMENTS = 5;
	
	public CompetitiveGameEnvironment env;
	
	///holds data to output to file
	public Map<Character, String> outputValues = new HashMap<Character, String>();
	
	///holds both the agent executors
	public Map<Character, AgentExec> agentDatas = new HashMap<Character, AgentExec>();
	
	
	///is added to the front of the outputed file names
	public String addToFileTitle = "ONE_RANDOM_BLOCKED_ChanceChanges.05_10000MOVES_MaRz";
	
	public static Map<Character, String> fileNames = new HashMap<Character, String>();
	
	public CompetitiveEnvironmentRunner(CompetitiveGameEnvironment env, String currentDate){
		
		//initializes both files names
		fileNames.put('x', "testoutput/" + addToFileTitle + "_AgentOne_" + currentDate + ".csv");
		fileNames.put('o', "testoutput/" + addToFileTitle + "_AgentTwo_" + currentDate + ".csv");
		
		this.env = env;
	}
	
	/**
	 * Runs the two previously defined agents in the competitive game environment
	 */
	public void exploreEnvironmentWithTwoAgents(){
		
		//nsm.setSensorOutputRandom(nsm.prevStateSensorOutput)
		
		Agent agentOne = new MaRzAgent('x', this);
		Agent agentTwo = new MaRzAgent('o', this);
		
		///MaRz exploreEnvironment also calls NSM exploreEnvironment
		LockObject lock = new LockObject();
		
		agentDatas.put('x', new AgentExec(lock, agentOne, agentTwo)  );
		agentDatas.put('o', new AgentExec(lock, agentTwo, agentOne) );
		
		Thread runnerOne = new Thread(agentDatas.get('x'));
		Thread runnerTwo = new Thread(agentDatas.get('o'));
		
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
	 * Executes the agent and alternates between the 'agent' and 'otherAgent' moves,
	 * using the lockobject to alternate between threads. It is the agents responsibility 
	 * to switch to the other thread.
	 * @author tommyeblen
	 *
	 */
	public class AgentExec implements Runnable{
		
		LockObject obj;
		Agent agent;
		Agent otherAgent;
		
		public AgentExec(LockObject obj, Agent agent, Agent otherAgent){
			this.obj = obj;
			this.agent = agent;
			this.otherAgent = otherAgent;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized(obj){
				
			
				while (agent.episodicMemory.size() < agent.MAX_EPISODES && agent.Successes <= agent.NUM_GOALS){
						//System.out.println(agent.playerChar + " exlporing");
						agent.exploreEnvironment();
					
					
					
					///Notifies other agent 
					
					
					//breaks out of loop if agent is completed
					//if(switchMovingAgent())
						//break;
					
						
					
				}//while
				//System.out.println("agent: " + agent.playerChar + " out");
				
			}
			
		}
		
		/**
		 * Switches the currentAgent moving
		 */
		public void switchMovingAgent(){
			obj.notifyAll();
			if(!(agent.episodicMemory.size() < agent.MAX_EPISODES && agent.Successes <= agent.NUM_GOALS)){
				agent.agentDone = true;
				//System.out.println("agent: " + agent.playerChar + " done");
				//return;
			}
			//Waits for other agents move completion
			try {
				if(!otherAgent.agentDone && !agent.agentDone){
					//System.out.println(agent.playerChar + " waiting");
					obj.wait();
				}
					
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	public void writeToFile(String fileName, String output){
		try{
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true), "UTF-8"));
			writer.append(output + "\n");
			writer.flush();
			writer.close();
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}
	
	

	
	public static void main(String args[]){
		
		///gets the current date
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date date = new Date();
		
		String currentDate = dateFormat.format(date);
		
		
		CompetitiveEnvironmentRunner runner;
		
		for ( int i = 0; i < N_GAME_ENVIRONMENTS; i++){
			///create game runner
			runner = new CompetitiveEnvironmentRunner(new CompetitiveGameEnvironment(), currentDate);
			
			System.out.println();
			System.out.println("Starting environment number: " +  String.valueOf(i + 1) );
			
			
			runner.exploreEnvironmentWithTwoAgents();
			
			
			System.out.println("writing to files");
			runner.writeToFile(runner.fileNames.get('x'), runner.outputValues.get('x'));
			runner.writeToFile(runner.fileNames.get('o'), runner.outputValues.get('o'));
			
			System.out.println("");
			System.out.println(runner.outputValues.get('x'));
			System.out.println(runner.outputValues.get('o'));
			
			
			//reset outputValues
			
			
		}
		
		System.out.println("GAME ENDED");
	}
	
}
