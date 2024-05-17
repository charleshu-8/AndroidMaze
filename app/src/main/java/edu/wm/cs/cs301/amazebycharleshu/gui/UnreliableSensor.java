package edu.wm.cs.cs301.amazebycharleshu.gui;

import edu.wm.cs.cs301.amazebycharleshu.generation.CardinalDirection;
import edu.wm.cs.cs301.amazebycharleshu.generation.Floorplan;
import edu.wm.cs.cs301.amazebycharleshu.generation.Wallboard;

/**
 * Class name: UnreliableSensor
 * 
 * Responsibilities: Obtain distance from wall given a position and direction; calculate energy consumption for using a sensor; fail to operate and begin a repair process to restore sensor functionality
 * 
 * Collaborators: ReliableSensor, Floorplan, FailAndRepairProcess
 * 
 * @author Charles Hu
 */

public class UnreliableSensor extends ReliableSensor {
	private FailAndRepairProcess process;
	private Thread thread;
	
	/**
	 * Constructor method for UnreliableSensor; will inherit attributes aside from those used to handle and track the independent thread for the fail and repair process
	 */
	public UnreliableSensor() {
		super();
		this.process = null;
		this.thread = null;
	}
	
	/**
	 * Calculates and returns the distance from the current position to the nearest wall
	 * @param currentPosition as integer array [x, y] coordinates for current tile occupied
	 * @param currentDirection as CardinalDirection indicating direction sensor is facing
	 * @param powersupply as float array size 1 indicating power available for use
	 * @return Distance to nearest wall as integer; if exit is detected instead, return Integer.MAX_VALUE
	 * @throws Exception SensorFailure if sensor is not operational on method call
	 * @throws Exception PowerFailure if power supply is insufficient to carry out operation
	 * @throws IllegalArgumentException if any parameter is null or illegal
	 * @throws IndexOutOfBoundsException if powersupply is range is invalid
	 */
	@Override
	public int distanceToObstacle(int[] currentPosition, CardinalDirection currentDirection, float[] powersupply)
			throws Exception {
		//PSEUDOCODE
		//Given current position, direction, and power supply
		//Throw error if any parameter is null or illegal
		//Throw error if power supply is not satisfactory (<0)
		//Pull instance of floorplan
		//From current position, check if a wall exists in the given direction; move tiles until a wall is encountered
		//Return amount of tiles until wall is encountered; if exit is encountered, return Integer.MAX_VALUE
		//**********
		
		//Need to ensure that the maze attribute is set before using it in following operations
		assert (this.maze != null) : "Maze not properly set";
				
		//Throw error if any parameter is null
		if (currentPosition == null || currentDirection == null || powersupply == null) {
			throw new IllegalArgumentException("No null parameters allowed");
		}
		//Throw error if any parameter is illegal
		if (currentPosition[0] < 0 || currentPosition[0] >= this.maze.getWidth() || currentPosition[1] < 0 || currentPosition[1] >= this.maze.getHeight()) {
			throw new IllegalArgumentException("Parameters are illegal");
		}
		//Throw error if powersupply's index size is not 1
		if (powersupply.length != 1) {
			throw new IndexOutOfBoundsException("Power supply input is illegal");
		}
		//Throw error is powersupply is insufficient for operation
		if (powersupply[0] < 1) {
			throw new Exception("PowerFailure: Insufficient power for operation");
		}
		//Throw error if sensor is currently not operational
		if (!this.getIsOperating()) {
			throw new Exception("SensorFailure: Sensor currently inoperable");
		}
		
		//Pull instance of floorplan & set up Wallboard variable for border check
		Floorplan mazeFloorplan = this.maze.getFloorplan();
		Wallboard wall = new Wallboard(currentPosition[0], currentPosition[1], currentDirection);
		int distanceTo = 0;
		
		//If exit is encountered on this tile, return Integer.MAX_VALUE
		//Checks to see if wall exists to handle when one looks at exit cell but not at exit gap directly
		//Checks to see if wall is part of border to ensure that user is looking directly at gap when on the exit cell
		if (mazeFloorplan.isExitPosition(currentPosition[0], currentPosition[1]) && mazeFloorplan.hasNoWall(currentPosition[0], currentPosition[1], currentDirection) && mazeFloorplan.isPartOfBorder(wall)) {
			return Integer.MAX_VALUE;
		}
		
		//While the cell ahead in oriented direction has no wall and is not part of a border:
		while (mazeFloorplan.hasNoWall(currentPosition[0], currentPosition[1], currentDirection) && !mazeFloorplan.isPartOfBorder(wall)) {
			//Add to the cells that can be traveled without encountering an obstacle
			distanceTo++;
			//Iterate current position by the direction to move forward to the next cell to be checked in that direction
			currentPosition[0] = currentPosition[0] + currentDirection.getDxDyDirection()[0];
			currentPosition[1] = currentPosition[1] + currentDirection.getDxDyDirection()[1];
			//Also update the Wallboard variable for the border check
			wall = new Wallboard(currentPosition[0], currentPosition[1], currentDirection);
			
			//If exit is encountered on next tile, return Integer.MAX_VALUE
			//Checks to see if wall exists to handle when one looks at exit cell but not at exit gap directly
			if (mazeFloorplan.isExitPosition(currentPosition[0], currentPosition[1]) && mazeFloorplan.hasNoWall(currentPosition[0], currentPosition[1], currentDirection)) {
				return Integer.MAX_VALUE;
			}
		}
		
		return distanceTo;
	}
	
	/**
	 * Will set sensor state to broken and initialize process to repair the sensor, blocks usage until fixed
	 * @param meanTimeBetweenFailures as integer representing seconds
	 * @param meanTimeToRepair as integer representing seconds
	 * @throws UnsupportedOperationException if not implemented
	 */
	@Override
	public void startFailureAndRepairProcess(int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		//PSEUDOCODE
		//Wait for given time in between failures
		//Set sensor to failed, block distance sensing (may have to alter parent class to recognize failure state)
		//Wait for given time for repair process
		//Set sensor back to operational
		//Restart
		//**********
		
		//If FailAndRepairProcess object has not yet been initialized for thread usage, create one and track it
		if (this.process == null) {
			this.process = new FailAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
		}
		//If a thread based on the FailAndRepairProcess object is currently not available/running, create one
		if (this.thread == null) {
			this.process.setKillThread(false);
			this.thread = new Thread(this.process);
		}
		
		//Start up the thread based on the FailAndRepairProcess instance
		this.thread.start();
	}

	/**
	 * Stops failure and repair process
	 * @throws UnsupportedOperationException if not implemented or if there is no ongoing process to terminate
	 */
	@Override
	public void stopFailureAndRepairProcess() throws UnsupportedOperationException {
		//PSEUDOCODE
		//Check if sensor is operational and fail & repair process is alive
		//If so, kill it
		//Else alive but not operational, wait for operational and then kill
		//Else, throw error
		//**********
		
		//Check if a thread is currently running, if not then this is a premature method call and should throw an error
		if (this.thread == null) {
			throw new UnsupportedOperationException("Error: No killable thread found");
		}
		
		//Notify thread that we are killing it
		//Thread should now naturally end itself by exiting its run() method
		this.process.setKillThread(true);
		//Wait meanTimeBetweenFailures + meanTimeToRepair (or 6 seconds) for worse case where thread is at beginning of process
		//Allows for thread to naturally run through its course without interference
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//Remove reference to thread so we can reassign it again later
		this.thread = null;
	}
	
	/**
	 * Gets current status of sensor based off of progress in fail and repair process
	 * @return Sensor operating status as boolean
	 */
	public Boolean getIsOperating() {
		return this.process.getIsOperating();
	}
}
