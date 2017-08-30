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
	public static final int N_SUCCESSES = 200;
	
	public static CompetitiveGameEnvironment env;
	
	public static Agent agentOne;
	public static Agent agentTwo;
	
	public static AgentExec agentRunnerOne;
	public static AgentExec agentRunnerTwo;
	
	public static String outputOne = "n: ";
	public static String outputTwo = "m: ";
	
	public static Map<Character, String> outputValues = new HashMap<Character, String>();
	public static Map<Character, AgentData> agentDatas = new HashMap<Character, AgentData>();
	
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
		
		Agent agentOne = new MaRzAgent('x');
		Agent agentTwo = new MaRzAgent('o');
		
		
		//nsm.setSensorOutputRandom(nsm.prevStateSensorOutput);
		RunCompetitiveEnvironment compEnv = new RunCompetitiveEnvironment();	
		///MaRz exploreEnvironment also calls NSM exploreEnvironment
		LockObject lock = compEnv.new LockObject();
		
		/*
		agentRunnerOne = compEnv.new AgentExec(lock, agentOne, agentTwo);
		agentRunnerTwo = compEnv.new AgentExec(lock, agentTwo, agentOne);
		*/
		
		agentDatas.put('x', compEnv.new AgentData( new NSMAgent('x'), compEnv.new AgentExec(lock, agentOne, agentTwo) ) );
		agentDatas.put('o', compEnv.new AgentData( new NSMAgent('o'), compEnv.new AgentExec(lock, agentTwo, agentOne) ) );
		
		Thread runnerOne = new Thread(agentDatas.get('x').agentRunner);
		Thread runnerTwo = new Thread(agentDatas.get('o').agentRunner);
		
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
	
	
	

	/*
	
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
	*/
	
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
	 * holds both the agent and its runner
	 * @author tommyeblen
	 *
	 */
	public class AgentData{
		Agent agent;
		AgentExec agentRunner;
		
		public AgentData(Agent agent, AgentExec agentRunner){
			this.agent = agent;
			this.agentRunner = agentRunner;
		}
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
