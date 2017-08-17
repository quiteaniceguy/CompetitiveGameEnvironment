
public class RunCompetitiveEnvironment {
	public static final int N_GAME_ENVIRONMENTS = 20;
	public static final int N_SUCCESSES = 1000;
	
	public static CompetitiveGameEnvironment env;
	
	public static NSMAgent nsm;
	public static MaRzAgent marz;
	
	public static String outputOne = "n: ";
	public static String outputTwo = "m: ";
	public static void main(String args[]){
		
		for ( int i = 0; i < N_GAME_ENVIRONMENTS; i++){
			System.out.println();
			System.out.println("Starting environment number: " +  String.valueOf(i + 1) );
			
			env = new CompetitiveGameEnvironment();
			
			
			exploreEnvironmentWithTwoAgents();
		}
		System.out.println("GAME ENDED");
	}
	
	public static void exploreEnvironmentWithTwoAgents(){
		nsm = new NSMAgent('x');
		marz = new MaRzAgent('o');
		
		nsm.setSensorOutputRandom(nsm.prevStateSensorOutput);
		
		while (nsm.episodicMemory.size() < nsm.MAX_EPISODES && nsm.Successes <= nsm.NUM_GOALS && marz.memory.length() < marz.MAX_EPISODES && marz.Successes <= marz.NUM_GOALS) { 
			
			///MaRz exploreEnvironment also calls NSM exploreEnvironment
			marz .exploreEnvironment();
			
			
			//nsm.exploreEnvironment();
			
			
		}
		System.out.println("");
		System.out.println(outputOne);
		System.out.println(outputTwo);
		
		outputOne = "n: ";
		outputTwo = "m: ";
	}
	
	public static void nsmExploreEnvironment(){
	 nsm.exploreEnvironment();
	}
}
