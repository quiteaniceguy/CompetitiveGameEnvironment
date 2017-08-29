import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Collections;

/**
 * class NSMAgent
 *
 * This agent is an implementation of Andrew McCallum's Nearest Sequence Memory
 * algorithm.  
 *
 * @author: Andrew Nuxoll (with many thanks to Zach Faltersack for his original
 * implementation in C)
 *
 */
public class NSMAgent extends Agent {

   
    /**
     * ************************************************************************************
     * INNER CLASSES
     * ************************************************************************************
     */
    /** 
     * class QEpisode
     *
     * This class extends Episode to have a q-value and a reward
     */
    public static class QEpisode extends Episode {
        public double qValue = 0.0;
        public double reward = 0.0;

        public QEpisode(char cmd, int sensor) {
            super(cmd, sensor);
        }
        
        public QEpisode(char cmd, int sensor, boolean[] stateSensorOutput){
        	super(cmd, sensor, stateSensorOutput);
        }
    }//class QEpisode
    
    /**
     * class NBor
     *
     * describes a "neighbor", specifically a sequence that matches the current
     * sequence ending with the last episode (which represents the present
     * moment) presuming that a specific action will be taken next.
     */
    public static class NBor implements Comparable<NBor> {
        public int end;  // index of the last episode of the sequence
        public int begin; // index of the first episode of the sequence
        public int len;  //length of the sequence
        public double hValue; //heuristic value

        public NBor(int initEnd, int initLen, double hValue) {
            this.begin = initEnd - initLen;
            this.end = initEnd;
            this.len = initLen;
            this.hValue = hValue;
        }

        /** this allows a collection of NBors to be sorted on length */
        public int compareTo(NBor other) {
        	int returnValue = 0;
        	if (this.hValue > other.hValue) returnValue = 1;
        	if (this.hValue < other.hValue) returnValue = -1;
        	
            return returnValue;
        }
    }//class NBor
    
    /**
     * class NHood
     *
     * is a public container class for defining a "neighborhood" in episodic
     * memory.  Specifically this a set of k sequences that have the longest
     * match to the current sequene ending with the last episode (which
     * represents the present moment) presuming that a specific action will be
     * taken next.
     */
    public static class NHood {
        public final int K_NEAREST = 8;  //max allowed size of neighborhood
        
        public char command;           // action associated with this neighborhood
        public ArrayList<NBor> nbors;  // neigbhors in the hood
        public int shortest = 0;       //length of shortest neighbor

        public NHood(char initCmd) {
            this.command = initCmd;
            nbors = new ArrayList<NBor>();
        }

        /** adds a new neighbor to the neighborhood.
         * CAVEAT:  Caller is responsible for checking the neighbor is long
         * enough to belong. */
        public void addNBor(NBor newGuy) {
            //if the nhood is full, drop the shorest neighbor to make room
            while(nbors.size() >= K_NEAREST) {
                this.nbors.remove(0);
            }

            this.nbors.add(newGuy);

            //update the shortest
            Collections.sort(nbors);
            this.shortest = nbors.get(0).len;
        }//addNBor

    }//class NHood
    
    /**
     * ************************************************************************************
     * VARIABLES
     * ************************************************************************************
     */
    // Defines for Q-Learning algorithm
    public static double DISCOUNT         =  0.8;
    public static double LEARNING_RATE    =  0.85;
    public static double REWARD_SUCCESS   =  1.0;
    public static double REWARD_FAILURE   = -0.1;
    public static double INIT_RAND_CHANCE =  0.8;
    public static double RAND_DECREASE    =  0.95;  //mult randChance by this
                                                    //value at each goal
    public static double MIN_RAND_CHANCE  =  0.0;
    
    protected NHood[] nhoods;
    protected double randChance = INIT_RAND_CHANCE;  //how frequently the agent
                                                     //make a random move
    //delete
    
    
    /**
     * ************************************************************************************
     * METHODS 
     * ************************************************************************************
     */

    
        
    /**
	 * The constructor for the agent simply initializes it's instance variables
	 */
	public NSMAgent(char playerChar) {
		super(playerChar);
		
        informationColumns = 2;
        
        nhoods = new NHood[alphabet.length];
        episodicMemory.clear();
  
	}//NSMAgent ctor

    /**
     * populateNHoods
     *
     * creates a neighborhood of k-nearest NBors for each action.  The NHoods
     * must be regenerated each time that that a new episode is added to the
     * store.
     */
    public void populateNHoods() {

        QEpisode ep = (QEpisode)episodicMemory.get(episodicMemory.size() - 1);

        //Create a new neighborhood for each command
        for(int c = 0; c < alphabet.length; ++c)
        {
            nhoods[c] = new NHood(alphabet[c]);
            
            //temporarily set the to-be-issued command to this value
            ep.command = alphabet[c];
            episodicMemory.set(episodicMemory.size() - 1, ep);
            
            
            
            //find the kNN 
            for(int i = 0; i <= episodicMemory.size() - 2; ++i) {
                int matchLen = matchedMemoryStringLength(i);
                double percentSensorsMatch = 0;
                double hValue;
                /*
                for(int j = 0; j < matchLen; j++)
                	percentSensorsMatch += episodicMemory.get(episodicMemory.size() - 1 - j).getPercentSensorsMatching(episodicMemory.get(i - j).stateSensorOutput);
         		
                percentSensorsMatch /= matchLen;
                */
               
                
                
                hValue = matchLen;
                
               
                
                if ( (hValue >0) &&
                    ( (nhoods[c].shortest <= hValue) || (nhoods[c].nbors.size() < nhoods[c].K_NEAREST) ) ) {
                    nhoods[c].addNBor(new NBor(i, matchLen, hValue));
                    //System.out.println((double)(matchLen)/episodicMemory.size());
                    
                   
                    
                }
                
            }//for
            
        
        
            
           
           
        }//for
    }//populateNHoods

    /**
     * calculates a neighborhood's total Q value.  This is the average of the
     * expected future discounted rewards of all the neighbors in the
     * neighborhood
     *
     */
    double calculateQValue(NHood nhood)
    {
        //Don't calculate for empty neighborhoods
        if (nhood.nbors.size() == 0) return 0.0;

        // sum the q-values of each neighbor
        double total = 0.0;
        for(NBor nbor : nhood.nbors)
        {
            QEpisode qep = (QEpisode)episodicMemory.get(nbor.end);
            total += qep.qValue;
        }

        // return the average
        return (total / (double)nhood.nbors.size());
    }//calculateQValue

    /**
     * setNewLittleQ
     *
     * This functions takes an episode and the current utility and updates the episode's
     * expected future discounted reward. 
     *
     * @param ep A pointer to an episode to update
     * @param utility A double that contains the current state's utility used to update
     *              the episodes that voted for the most recent action
     */
    public void setNewLittleQ(QEpisode ep, double utility)
    {
        // Set the new q value for the episode
        //if(!g_statsMode) printf("Calculating and setting new expected future discounted reward\n");
        ep.qValue = (1.0 - LEARNING_RATE) * (ep.qValue)
            + LEARNING_RATE * (ep.reward + DISCOUNT * utility);
    }//setNewLittleQ
    
    /**
     * updateAllLittleQ
     *
     * This function will update the expected future discounted rewards for the
     * action that was most recently executed. We cannot guarantee that the
     * chosen action was not selected randomly because of the exploration
     * rate. To account for this we will index into the vector of neighborhoods
     * and update the neighborhood relevant to the executed action.
     *
     * @arg ep A pointer to the episode containing the most recently executed action
     */
    public void updateAllLittleQ(QEpisode ep)
    {
        // Recalculate the Q value of the neighborhood associated with the
        // episode's action
        NHood nhood = nhoods[ep.command - 'a'];
        double utility = calculateQValue(nhood);

        // Update the q values for each of the voting episodes for the most
        // recent action
        for(int i = 0; i < nhood.nbors.size(); ++i) {
            //Update the root episode
            NBor nbor = nhood.nbors.get(i);
            int index = nbor.end - i;
            if (index < 0) continue;  //don't fall off the end
            QEpisode rootEp = (QEpisode)episodicMemory.get(index);
            setNewLittleQ(rootEp, utility);
            double prevUtility = utility;

            //Update all the root's predecessors that participated in the match
            for(int j = 1; j < nbor.len; ++j)
            {
                QEpisode prevEp = (QEpisode)episodicMemory.get(episodicMemory.size() - j);
                setNewLittleQ(prevEp, prevUtility);
                prevUtility = prevEp.qValue;
            }
        }//for

        // Update the given (most recent) episode's Q value 
        setNewLittleQ(ep, utility);

    }//updateAllLittleQ

    //Used to print steps to success for each goal at the console for teh HOO-maans
    int lastSuccess = 0;

    /**
     * exploreEnvironment
     *
     * Main Driver Method of Program
     *
     */
    ///for exploreEnvironment
    int prevSensors = 0; //what was sensed last time
    boolean[] prevStateSensorOutput = new boolean[StateMachineEnvironment.N_SENSORS]; //the sensors given from each state, used to identify the state
   
    @Override
    public void exploreEnvironment() {
    	
       
       
        //while (episodicMemory.size() < MAX_EPISODES && Successes <= NUM_GOALS) { 
        
        //add an episode to represent the current moment
        char cmd = alphabet[random.nextInt(alphabet.length)];  //default is random for now
        QEpisode nowEp = new QEpisode(cmd, prevSensors, prevStateSensorOutput);
        //Update the q-values for the previous iteration of this loop
        if (nhoods[0] != null) updateAllLittleQ(nowEp);
		episodicMemory.add(nowEp);
        
        // We can't use NSM until we've found the goal at least once
        populateNHoods();
        if(Successes > 0) {
            // (if not using random action) select the action that has the
            // neighborhood with the highest Q-value
            if (random.nextDouble() >= this.randChance) {
                double bestQ = calculateQValue(nhoods[0]);
                cmd = nhoods[0].command;
                for(NHood nhood : nhoods) {
                    double qVal = calculateQValue(nhood);
                    if (qVal > bestQ) {
                        bestQ = qVal;
                        cmd = nhood.command;
                    }
                }//for
            }//if
        }//if
        
        //execute the command
        nowEp.command = cmd;
        ///make random move
        int i = random.nextInt(alphabet.length);
        char randMove = alphabet[i];
        /////
        
        //boolean[] sensors = env.tick(cmd);
        boolean[] sensors = RunCompetitiveEnvironment.env.tick(playerChar, cmd);
        System.out.println(playerChar + "move: " + cmd);
        //Setup for next iteration
        
        
        prevSensors = encodeSensors(sensors);
        prevStateSensorOutput = getSensorOutput(sensors);
        

       
        
        if (sensors[IS_GOAL]){

            nowEp.reward = REWARD_SUCCESS;
            Successes++;
            if (randChance > MIN_RAND_CHANCE)
            {
                randChance *= RAND_DECREASE;
            }

            //Inform the user of steps that were required
             
            RunCompetitiveEnvironment.outputValues.put(playerChar, RunCompetitiveEnvironment.outputValues.get(playerChar) + String.valueOf(episodicMemory.size() - lastSuccess) + ",");
            //System.out.print("n: " + String.valueOf(episodicMemory.size() - lastSuccess));
            //System.out.print(",");
            lastSuccess = episodicMemory.size();
        }
        else
        {
            nowEp.reward = REWARD_FAILURE;
        }
        
        //while
    }//exploreEnvironment
    
    /**
	 * recordLearningCurve
	 * 
	 * examine's the agents memory and prints out how many steps the agent took
	 * to reach the goal each time
	 * 
     * @param csv         an open file to write to
	 */
	protected void recordLearningCurve(FileWriter csv) {
        try {
            csv.append("" + episodicMemory.size() + ",");
            
            int prevGoalPoint = 0; //which episode I last reached the goal at
            for(int i = 0; i < episodicMemory.size(); ++i) {
                Episode ep = episodicMemory.get(i);
                if (ep.sensorValue == GOAL) {
                    csv.append(i - prevGoalPoint + ",");
                    csv.flush();
                    prevGoalPoint = i;
                }//if
            }//for

            csv.append("\n");
            csv.flush();
        }
        catch (IOException e) {
            System.out.println("recordLearningCurve: Could not write to given csv file.");
            System.exit(-1);
        }
                
	}//recordLearningCurve

	/**
	 * main
     *
	 */
	public static void main(String [ ] args) {
        
            try{
                String fname = "AIReport_" + makeNowString() + ".csv";
                FileWriter csv = new FileWriter(fname);
                
                //Record the the configuration
                String config = "";
                config += "NUM_GOALS," + NUM_GOALS + "\n";
                config += "MAX_EPISODES," + MAX_EPISODES + "\n";
                config += "DISCOUNT," + DISCOUNT + "\n";
                config += "LEARNING_RATE," + LEARNING_RATE + "\n";
                config += "INIT_RAND_CHANCE," + INIT_RAND_CHANCE + "\n";
                config += "RAND_DECREASE," + RAND_DECREASE + "\n";
                config += "MIN_RAND_CHANCE," + MIN_RAND_CHANCE + "\n";
                config += "NUM_STATES," + StateMachineEnvironment.NUM_STATES + "\n";
                config += "ALPHABET_SIZE," + StateMachineEnvironment.ALPHABET_SIZE + "\n";
                config += "TRANSITIONS_PERCENT," + StateMachineEnvironment.TRANSITIONS_PERCENT + "\n";
                config += "MAX_EPISODES," + MAX_EPISODES + "\n";
                config += "NUM_GOALS," + NUM_GOALS + "\n";
                config += "NUM_MACHINES," + NUM_MACHINES + "\n";
                config += "NUM_SENSORS," + StateMachineEnvironment.N_SENSORS + "\n"; 
                config += "INITIAL SENSOR SETTING," + StateMachineEnvironment.CUR_INIT_SENSOR_SETTING + "\n";
                config += "cmd line:,";
                for(String s : args) config += s + ",";
                config += "\n";
                System.out.println(config);
                csv.append(config);
                informationRows += 16; //we just added 16 header rows to the csv
                
                //header row
                csv.append("Opt Path Len,EpMem Size,");
                for(int i = 1; i <= NUM_GOALS; ++i)
                {
                    csv.append("run " + i + ",");
                }
                csv.append("\n");
                informationColumns++;  //see Agent.java

                //initialize the blindLengthSum for calc'ing average later
                int blindLengthSum = 0;
                
                for(int i = 0; i < NUM_MACHINES; ++i) {
                    NSMAgent skipper = new NSMAgent('x');

                    //Print blind path length 
                    String sbPath = skipper.env.shortestPathToGoal();
                    csv.append("" + sbPath.length() + ",");
                    blindLengthSum += skipper.env.avgStepsToGoalWithPath(sbPath);
                    csv.flush();

                    //A little reassurance for the humans
                    System.out.println("Beginning machine " + (i+1) + " of " + NUM_MACHINES);
                    System.out.println("shortest blind path len: " + sbPath.length());
                    
                    //This is what takes forever...
                    skipper.exploreEnvironment();

                    skipper.recordLearningCurve(csv);
                    System.out.println();
                    System.out.println(skipper.memoryToString());
                }
                //Record the average steps per goal
                recordAverage(csv);

                //use the average optimal blind path length as a baseline
                int blindLengthAvg = (int)((0.5 + blindLengthSum) / NUM_MACHINES);
                recordBaseline(csv, blindLengthAvg);
                
                // end of file
                csv.close();
                System.out.println("End of " + fname);
            }
            catch(IOException e){
                
            }
            
	}

}//class NSMAgent
