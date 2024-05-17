package edu.wm.cs.cs301.amazebycharleshu.gui;

import edu.wm.cs.cs301.amazebycharleshu.generation.CardinalDirection;
import edu.wm.cs.cs301.amazebycharleshu.generation.Floorplan;
import edu.wm.cs.cs301.amazebycharleshu.generation.Maze;
import edu.wm.cs.cs301.amazebycharleshu.generation.Wallboard;
import edu.wm.cs.cs301.amazebycharleshu.gui.Robot.Direction;

/**
 * Class name: ReliableSensor
 * 
 * Responsibilities: Obtain distance from wall given a position and direction; calculate energy consumption for using a sensor
 * 
 * Collaborators: Floorplan
 * 
 * @author Charles Hu
 * 
 */

public class ReliableSensor implements DistanceSensor {
	protected Maze maze;
	private Direction sensorDirection;
	private final float CONSTANT_ENERGY_CONSUMPTION = 1;

	/**
	 * Constructor for ReliableSensor; initialize and store info on sensor object (e.g., direction) or maze operating in
	 */
	public ReliableSensor() {
		//PSEUDOCODE
		//Initialize attribute for maze/floorplan
		//Initialize attribute for sensor mount direction
		//**********
		
		this.maze = null;
		this.sensorDirection = null;
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
	 * Sets maze to be used for further sensor operations
	 * @param maze as Maze object, particular maze used for the current game
	 */
	@Override
	public void setMaze(Maze maze) {
		//PSEUDOCODE
		//Given maze object, set to object attribute in constructor
		//**********
		
		//Ensure that maze is set to non-null object for later use
		assert (maze != null) : "Cannot set maze as null";
		this.maze = maze;
	}

	/**
	 * Sets direction sensor is facing
	 * @param mountedDirection as Direction desired
	 */
	@Override
	public void setSensorDirection(Direction mountedDirection) {
		//PSEUDOCODE
		//Given direction for sensor, set to object attribute in constructor
		//**********
				
		//Ensure that sensor direction is set to non-null object for later use
		assert (mountedDirection != null) : "Cannot set sensor direction as null";
		this.sensorDirection = mountedDirection;
	}
	
	/**
	 * Gets direction of sensor object; method is not from interface
	 * @return Direction of sensor
	 */
	public Direction getSensorDirection() {	
		//Ensure that we aren't getting value before it is set
		assert (this.sensorDirection != null) : "Retrived before set";
		return this.sensorDirection;
	}

	/**
	 * Returns energy consumed for a single sensing operation
	 * @return Energy consumed for operation as float
	 */
	@Override
	public float getEnergyConsumptionForSensing() {
		//PSEUDOCODE
		//Return energy usage for sensing distances one time; should be 1
		//**********
				
		//Constant preset value, refer to class attributes for value
		return CONSTANT_ENERGY_CONSUMPTION;
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
		//Throw UnsupportedOperationException; not implemented as of now
		//**********
				
		//Not implemented as of now
		throw new UnsupportedOperationException("Will be implemented in next project");
	}

	/**
	 * Stops failure and repair process
	 * @throws UnsupportedOperationException if not implemented
	 */
	@Override
	public void stopFailureAndRepairProcess() throws UnsupportedOperationException {
		//PSEUDOCODE
		//Throw UnsupportedOperationException; not implemented as of now
		//**********
				
		//Not implemented as of now
		throw new UnsupportedOperationException("Will be implemented in next project");
	}
}
