package edu.wm.cs.cs301.amazebycharleshu.gui;

import edu.wm.cs.cs301.amazebycharleshu.generation.CardinalDirection;
import edu.wm.cs.cs301.amazebycharleshu.generation.Maze;
import edu.wm.cs.cs301.amazebycharleshu.gui.Robot.Direction;
import edu.wm.cs.cs301.amazebycharleshu.gui.Robot.Turn;

/**
 * Class name: Wizard
 * 
 * Responsibilities: Set up the robot for maze traversal; drive the robot in the maze toward the exit in an iterative manner; handle and track energy consumption; handle and track overall path to exit
 * 
 * Collaborators: Robot
 * 
 * @author Charles Hu
 * 
 */

public class Wizard implements RobotDriver {
	private Robot robot;
	private Maze maze;

	/**
	 * Constructor for Wizard; handles and tracks assigned robot & maze
	 */
	public Wizard() {
		//PSEUDOCODE
		//Initialize attribute for robot
		//Initialize attribute for maze
		//**********
		
		this.robot = null;
		this.maze = null;
	}
	
	/**
	 * Assigns used robot via constructor parameter
	 * @param r as Robot to be assigned
	 */
	@Override
	public void setRobot(Robot r) {
		//PSEUDOCODE
		//Set robot to constructor parameter
		//**********
		
		//Need to make sure that robot is non-null so driver can use
		assert r != null : "Robot object is null";
		this.robot = r;
	}

	/**
	 * Assigns used maze for Wizard algorithm via constructor parameter
	 * @param maze as Maze to be assigned
	 */
	@Override
	public void setMaze(Maze maze) {
		//PSEUDOCODE
		//Set maze to constructor parameter
		//**********
		
		//Need to make sure that maze is non-null so driver can use
		assert maze != null : "Maze object is null";
		this.maze = maze;
	}

	/**
	 * Drive robot to exit via Wizard algorithm, traversing to tiles that are closer to exit via whitebox access to maze data
	 * @return True if driver can reach exit, false if it cannot reach
	 * @throws Exception if robot crashes or lacks energy
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		//PSEUDOCODE
		//Use 1 step movement method to iteratively walk towards exit
		//If obtains false from single step method and no exception is found, return true for whole function
		//Else if energy still available and has not crashed but cannot reach exit, return false; highly unlikely
		//Else if crashed or has no energy, throw exception
		//**********
		
		//Need to make sure that robot has been set before starting method operations
		assert this.robot != null : "Robot is not yet set";
		
		//Check if robot is inoperable
		while (!this.robot.hasStopped()) {
			//If not, reuse the single step function to iteratively walk through the maze
			//Use a try catch in case the function runs into an error during operation which should equally translate over to this function
			try {
				//The single step method returns true if it moves but doesn't reach an exit
				//We can use this to force it to continually repeat itself until it reach an exit and returns false
				if(!this.drive1Step2Exit()) {
					//Once it does, we know we have finished the maze and return true
					return true;
				}
			}
			catch (Exception e) {
				throw new Exception("Robot has stopped");
			}
		}
		//Return false if maze cannot be finished/breaks down before use somehow
		//Reasonably cannot be reached since wizard will always have correct solution to exit
		//More likely for robot to run out of energy and throw exception for larger mazes
		return false;
	}

	/**
	 * Move a single step towards the exit via Wizard algorithm, traversing to tiles that are closer to exit via whitebox access to maze data
	 * @return True if successfully moved, false if it is facing the exit
	 * @throws Exception if robot crashes or lacks energy
	 */
	@Override
	public boolean drive1Step2Exit() throws Exception {	
		//PSEUDOCODE
		//If no energy available or has crashed, throw error
		//If at exit and not oriented towards it, turn towards it
			//Else if at exit and oriented towards it, return false
		//Else, find tile next to current position that has lowest distance value
		//Attempt movement in that direction
			//Possibility for crash here, if so, throw exception
		//If haven't crashed and is not at exit, return true
		//**********
		
		//Need to make sure that robot & maze have been set before starting method operations
		assert this.robot != null : "Robot is not yet set";
		assert this.maze != null : "Maze is not yet set";
		
		//First ensure whether robot is standing on exit cell
		if (this.robot.isAtExit()) {
			//Wrap with try/catch for if sensor becomes inoperable
			try {
				//If so, ensure that it is facing exit space
				while (!this.robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD)) {
					//If not, turn until robot is
					this.robot.rotate(Turn.RIGHT);
				}
			}
			catch (Exception e) {
				throw new Exception("Robot has stopped");
			}
			//If so, return false; at exit and is oriented correctly
			return false;
		}
		//If not, assume robot needs to move to adjacent cell
		else {
			//Use maze method to find a cell closer to the exit
			//Should be a cell that is 1) closer to exit and 2) have no wall between such cell and current cell
			int[] closerCell = this.maze.getNeighborCloserToExit(this.robot.getCurrentPosition()[0], this.robot.getCurrentPosition()[1]);
			
			//Check surrounding cells to find the cell that is closer to the exit
			int lowestDirection = 0;
			CardinalDirection checkDirection = this.robot.getCurrentDirection();
			//Iteratively check surrounding cells using i to represent direction checked
			//0 is forwards, 1 is right, 2 is backwards, 3 is left
			for (int i = 0; i < 4; i++) {
				int checkX = this.robot.getCurrentPosition()[0] + checkDirection.getDxDyDirection()[0];
				int checkY = this.robot.getCurrentPosition()[1] + checkDirection.getDxDyDirection()[1];
				//Check if the checked cell's direction matches with the cell closer to the exit
				if (checkX == closerCell[0] && checkY == closerCell[1]) {
					lowestDirection = i;
				}
				checkDirection = checkDirection.rotateClockwise();
			}
			
			//Once found, turn to the closer cell
			switch (lowestDirection) {
			case 1:
				this.robot.rotate(Turn.RIGHT);
				break;
			case 2:
				this.robot.rotate(Turn.AROUND);
				break;
			case 3:
				this.robot.rotate(Turn.LEFT);
				break;
			}
			//And move into the cell
			this.robot.move(1);
			//Check for hasStopped after using movement methods
			if (this.robot.hasStopped()) {
				throw new Exception("Robot has stopped");
			}
			
			//Has successfully completed a move
			return true;
		}
	}

	/**
	 * Helper function to check whether a sensor is operational in given direction
	 * @param direction as Direction for desired direction to check sensor
	 * @return Boolean for whether sensor is operating in given direction
	 */
	protected Boolean isSensorOperating(Direction direction) {
		//Sensors have a built in fallback which returns -1 when non-operational, such as when in a failed state
		//If we queue a sensor call and get -1, then we know it is not currently operational
		if (this.robot.distanceToObstacle(direction) == -1) {
			return false;
		}
		return true;
	}

	/**
	 * Return total energy used
	 * @return Energy consumed as float
	 */
	@Override
	public float getEnergyConsumption() {
		//PSEUDOCODE
		//Return full energy level - energy level at exit
		//**********
		
		//Need to make sure that robot is non-null before pulling attributes
		assert this.robot != null : "Robot is not yet set";
		//Total energy used is full energy level - energy level at exit
		return 3500 - this.robot.getBatteryLevel();
	}
	
	/**
	 * Return total distance taken during pathing towards exit
	 * @return Total length as integer
	 */
	@Override
	public int getPathLength() {
		//PSEUDOCODE
		//Return odometer attribute
		//**********
		
		//Need to make sure that robot is non-null before pulling attributes
		assert this.robot != null : "Robot is not yet set";
		return this.robot.getOdometerReading();
	}
}
