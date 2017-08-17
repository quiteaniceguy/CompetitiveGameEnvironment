/**
 * Episode
 *
 * Represents an episode in the agents episodic memory
 */
public class Episode { 
	
	public char command;     //what the agent did
	public int sensorValue;  //what the agent sensed
	boolean[] stateSensorOutput;

	public Episode(char cmd, int sensor) {
		command = cmd;
		sensorValue = sensor;
		this.stateSensorOutput = null;

	}
	public Episode(char cmd, int sensor, boolean[] stateSensorOutput){
		command = cmd;
		sensorValue = sensor;
		this.stateSensorOutput = stateSensorOutput;
	}

    public String toString() {
        return "[Cmd: "+command+"| Sensor: "+sensorValue+"]";
    }
    
    ///assumes sensorOutput and stateSensorOutput are same length
    public double getPercentSensorsMatching(boolean[] sensorOutput){
    	double nMatching = 0;
    	for (int i = 0; i < sensorOutput.length; i++) {
    		if (sensorOutput[i] == stateSensorOutput[i])
    			nMatching++;
    	}
 
    	//System.out.println(nMatching + "/" + sensorOutput.length + " = " + nMatching/sensorOutput.length);
   
    	return nMatching/sensorOutput.length;
    }
    
    public double getEuclideanDistance(boolean[] sensorOutput){
    	double d = 0;
    	
    	for (int i = 0; i < sensorOutput.length; i++) {
    		d += Math.pow( ( convertBoolToDouble(stateSensorOutput[i]) - convertBoolToDouble(sensorOutput[i]) ), 2);
    	}
    	
    	return d;
    }
    
	private double convertBoolToDouble(boolean b){
		if(b)
			return 1;
		
		return 0;
	}
}
