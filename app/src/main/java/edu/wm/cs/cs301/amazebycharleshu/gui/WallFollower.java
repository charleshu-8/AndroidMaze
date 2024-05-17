package edu.wm.cs.cs301.amazebycharleshu.gui;

import edu.wm.cs.cs301.amazebycharleshu.generation.Maze;
import edu.wm.cs.cs301.amazebycharleshu.gui.Robot.Direction;
import edu.wm.cs.cs301.amazebycharleshu.gui.Robot.Turn;

/**
 * Class name: WallFollower
 * 
 * Responsibilities: Set up the robot for maze traversal; drive the robot in the maze toward the exit in an iterative manner using leftward bound methods; handle and track energy consumption
 * 
 * Collaborators: Robot
 * 
 * @author Charles Hu
 */

public class WallFollower implements RobotDriver {
	private Robot robot;
	private Maze maze;

	/**
	 * Constructor for WallFollower; handles and tracks assigned robot & maze
	 */
	public WallFollower() {
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
	 * Drive robot to exit via WallFollower algorithm, following the leftmost path as much as possible until the exit is reached
	 * @return True if driver can reach exit, false if it cannot reach
	 * @throws Exception if robot crashes or lacks energy
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		//PSEUDOCODE
		//Use single step method to traverse maze
		//If obtains false from single step method and no exception is found, return true for whole function
		//Else if energy still available and has not crashed but cannot reach exit, return false
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
		return false;
	}

	/**
	 * Move a single step towards the exit by attempting to follow leftmost path as much as possible
	 * @return True if successfully moved, false if it is facing the exit
	 * @throws Exception if robot crashes or lacks energy
	 */
	@Override
	public boolean drive1Step2Exit() throws Exception {
		//PSEUDOCODE
		//Check if at exit, if so orient towards exit
		//Else check if in room, if so hug wall until exit found
		//Else use sensors to check walls to front and left
		//If either sensor inoperable, rotate until possible or force wait
		//Reset direction once sensed
		//Using information, determine movement option via existence of walls:
			//If no left: move left
			//If yes left, no front: move forward
			//If yes left, yes front: rotate clockwise, check again
		//If crashed or runs out of energy, throw error
		//**********
		
		//Need to make sure that robot & maze have been set before starting method operations
		assert this.robot != null : "Robot is not yet set";
		assert this.maze != null : "Maze is not yet set";
		
		//Check whether robot is standing on exit cell
		if (this.robot.isAtExit()) {
			//Wrap with try/catch for if sensor becomes inoperable via lack of power/crash
			try {
				//Check if forward sensor can see exit
				while (!this.robot.canSeeThroughTheExitIntoEternity(Direction.FORWARD)) {
					//If not, then check if forward sensor is operational
					if (this.isSensorOperating(Direction.FORWARD)) {
						//If operational, then not facing exit
						//Turn left until can see it
						this.robot.rotate(Turn.LEFT);
					}
					else {
						//Else is not operational, wait and try checking again
						Thread.sleep(1000);
					}
				}
			}
			catch (Exception e) {
				throw new Exception("Robot has stopped");
			}
			//Else robot is on exit and is facing exit
			return false;
		}
		//If not, then the robot needs to move to an adjacent cell
		else {
			//Variable to track whether this method has resulted in movement
			//Specifically for corner case where left and forward wall exist, so we have to turn clockwise and start process over again until we move a tile
			boolean hasMoved = false;
			while (!hasMoved) {
				//Variable to track which sensors are operational
				//0 is none, 1 is forward and left, 2 is left and backward, 3 is right and backward, 4 is forward and right
				int turnConfig = 0;
				//Variables to track existence of walls; currently set to arbitrary value -1, should be replaced during operation
				//0 indicates wall exists, all other positive integers suggest otherwise
				int forwardWall = -1;
				int leftWall = -1;
				//Want to loop until we find suitable combination of sensors
				//Then turn to have such sensors facing forward and leftward direction and check for walls
				while (turnConfig == 0) {
					//Forward and left sensors work
					if (this.isSensorOperating(Direction.FORWARD) && this.isSensorOperating(Direction.LEFT)) {
						turnConfig = 1;
						forwardWall = this.robot.distanceToObstacle(Direction.FORWARD);
						leftWall = this.robot.distanceToObstacle(Direction.LEFT);
	
					}
					//Left and backward sensors work
					else if (this.isSensorOperating(Direction.LEFT) && this.isSensorOperating(Direction.BACKWARD)) {
						turnConfig = 2;
						this.robot.rotate(Turn.LEFT);
						forwardWall = this.robot.distanceToObstacle(Direction.LEFT);
						leftWall = this.robot.distanceToObstacle(Direction.BACKWARD);
						this.robot.rotate(Turn.RIGHT);
					}
					//Backward and right sensors work
					else if (this.isSensorOperating(Direction.RIGHT) && this.isSensorOperating(Direction.BACKWARD)) {
						turnConfig = 3;
						this.robot.rotate(Turn.AROUND);
						forwardWall = this.robot.distanceToObstacle(Direction.BACKWARD);
						leftWall = this.robot.distanceToObstacle(Direction.RIGHT);
						this.robot.rotate(Turn.AROUND);
					}
					//Right and forward sensors work
					else if (this.isSensorOperating(Direction.RIGHT) && this.isSensorOperating(Direction.FORWARD)) {
						turnConfig = 4;
						this.robot.rotate(Turn.RIGHT);
						forwardWall = this.robot.distanceToObstacle(Direction.RIGHT);
						leftWall = this.robot.distanceToObstacle(Direction.FORWARD);
						this.robot.rotate(Turn.LEFT);
					}
					
					//If no combination of sensors are active, wait 1 second then try again
					if (turnConfig == 0) {
						Thread.sleep(1000);
					}
				}

				//Choose standard movement option based off whether there is a left or forward wall
				//If no left wall, move left
				if (leftWall > 0) {
					this.robot.rotate(Turn.RIGHT);
					this.robot.move(1);
					hasMoved = true;
				}
				//If has left wall but no forward wall, move forward
				else if (leftWall == 0 && forwardWall > 0) {
					this.robot.move(1);
					hasMoved = true;
					
				}
				//If has left and forward wall, turn right and try entire process again
				else if (leftWall == 0 && forwardWall == 0) {
					this.robot.rotate(Turn.LEFT);
				}
				else {
					throw new Exception("Error: Unable to read sensor data");
				}
				
				//Check for hasStopped after using movement methods
				if (this.robot.hasStopped()) {
					throw new Exception("Robot has stopped");
				}
			}
			//Finished movement, return true
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
