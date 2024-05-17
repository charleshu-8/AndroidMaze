package edu.wm.cs.cs301.amazebycharleshu.gui;

import edu.wm.cs.cs301.amazebycharleshu.generation.CardinalDirection;

/**
 * Class name: ReliableRobot
 * 
 * Responsibilities: Set up and handle initialization and usage of robot sensors and driver; handle movement operations (move tile, jump, rotate); track current position and room-value of current position;
 * handle & track energy consumption; recognize exit; track and handle distance moved; handle direction of the robot
 * 
 * Collaborators: Control, DistanceSensor
 * 
 * @author Charles Hu
 * 
 */

public class ReliableRobot implements Robot {
	private StatePlaying controller;
	protected DistanceSensor sensorForward;
	protected DistanceSensor sensorBackward;
	protected DistanceSensor sensorLeft;
	protected DistanceSensor sensorRight;
	private float battery;;
	private boolean hasStopped;
	private int odometer;
	private final int CONSTANT_QUARTER_ROTATE_ENERGY = 3;
	private final int CONSTANT_STEP_MOVEMENT_ENERGY = 6;
	private final int CONSTANT_JUMP_ENERGY = 40;

	/**
	 * Constructor for ReliableRobot; handles & tracks controller, sensors, positional/movement information, and energy levels
	 */
	public ReliableRobot() {
		//PSEUDOCODE
		//Initialize attribute for controller
		//Initialize attributes for sensors
		//Initialize battery
		//Initialize attribute for stopped/inoperable status
		//Initialize odometer
		//**********
		
		this.controller = null;
		this.sensorForward = null;
		this.sensorBackward = null;
		this.sensorLeft = null;
		this.sensorRight = null;
		this.battery = 3500;
		this.hasStopped = false;
		this.odometer = 0;
	}
	
	/**
	 * Sets controller to be used for robot via constructor attribute
	 * @param controller as StatePlaying
	 * @throws IllegalArgumentException is controller is null
	 */
	@Override
	public void setController(StatePlaying controller) {
		//PSEUDOCODE
		//Set given controller parameter to corresponding object attribute
		//**********
		
		if (controller == null) {
			throw new IllegalArgumentException("Controller cannot be null");
		}
		this.controller = controller;
	}

	/**
	 * Adds distance sensor to robot given sensor object and orientation on robot; at most holds 4 sensors, with only one allowed per direction
	 * @param sensor as DistanceSensor to be added to robot
	 * @param mountedDirection as Direction in which sensor is to be oriented
	 */
	@Override
	public void addDistanceSensor(DistanceSensor sensor, Direction mountedDirection) {
		//PSEUDOCODE
		//Set given sensor parameter to corresponding constructor attribute given orientation
		//**********
		
		//Need to ensure that the direction and sensor are actual objects which can be set
		assert (sensor != null) : "Set sensor cannot be null";
		assert (mountedDirection != null) : "Set direction cannot be null";
		
		//Set direction on given sensor
		sensor.setSensorDirection(mountedDirection);
		sensor.setMaze(this.controller.getMaze());
		
		//Assign the sensor to the corresponding object attribute
		//At most only one of each can exist at a time; a reassignment of the same direction will overwrite the old sensor
		switch (mountedDirection) {
		case FORWARD:
			this.sensorForward = sensor;
			break;
		case BACKWARD:
			this.sensorBackward = sensor;
			break;
		case LEFT:
			this.sensorLeft = sensor;
			break;
		case RIGHT:
			this.sensorRight = sensor;
			break;
		}
	}

	/**
	 * Returns current position of robot as coordinates (x,y)
	 * @return Integer array of position styled [x,y]
	 * @throws Exception if position is illegal
	 */
	@Override
	public int[] getCurrentPosition() throws Exception {
		//PSEUDOCODE
		//Return current position from constructor attribute
		//**********
		
		//Ensure the controller exists for method use
		assert (this.controller != null) : "Controller attribute used before being set";
		
		int[] currentPosition = this.controller.getCurrentPosition();
		//Check if pulled position is inside of maze
		if (currentPosition[0] < 0 || currentPosition[0] >= this.controller.getMaze().getWidth() || currentPosition[1] < 0 || currentPosition[1] >= this.controller.getMaze().getHeight()) {
			throw new Exception("Position is outside of maze");
		}
		return currentPosition;
	}

	/**
	 * Return direction robot is facing
	 * @return Direction of robot as CardinalDirection
	 */
	@Override
	public CardinalDirection getCurrentDirection() {
		//PSEUDOCODE
		//Return current direction from constructor attribute
		//**********
		
		//Ensure the controller exists for method use
		assert (this.controller != null) : "Controller attribute used before being set";
		
		return this.controller.getCurrentDirection();
	}

	/**
	 * Returns current state of battery
	 * @return Battery level as float
	 */
	@Override
	public float getBatteryLevel() {
		//PSEUDOCODE
		//Return current battery level from constructor attribute
		//**********
		
		return this.battery;
	}

	/**
	 * Sets battery level via constructor attribute
	 * @param level as float for desired battery level
	 * @throws IllegalArgumentException for illegal inputs
	 */
	@Override
	public void setBatteryLevel(float level) {
		//PSEUDOCODE
		//Set current battery level from constructor attribute
		//If battery < 0, stop
		//**********
		
		//If battery level is below 0, set status to stopped/inoperable
		if (level < 1) {
			this.hasStopped = true;
		}
		this.battery = level;
	}

	/**
	 * Return energy consumption for 360 degree rotation
	 * @return Necessary energy as float
	 */
	@Override
	public float getEnergyForFullRotation() {
		//PSEUDOCODE
		//Return energy consumed for single rotation
		//**********
		
		//Return energy consumed for single rotation (3) * times rotated (4)
		return CONSTANT_QUARTER_ROTATE_ENERGY * 4;
	}

	/**
	 * Return energy consumption for single movement forward
	 * @return Necessary energy as float
	 */
	@Override
	public float getEnergyForStepForward() {	
		//PSEUDOCODE
		//Return energy consumed for single step (6)
		//**********
		
		return CONSTANT_STEP_MOVEMENT_ENERGY;
	}

	/**
	 * Return total movement of robot
	 * @return Total movement as integer
	 */
	@Override
	public int getOdometerReading() {
		//PSEUDOCODE
		//Return total movement from constructor attribute
		//**********
		
		return this.odometer;
	}

	/**
	 * Reset total movement to 0
	 */
	@Override
	public void resetOdometer() {
		//PSEUDOCODE
		//Set constructor attribute value to 0
		//**********
		
		this.odometer = 0;
	}

	/**
	 * Turns the direction of the robot to the specified direction
	 * @param turn as Turn to indicate direction to face
	 */
	@Override
	public void rotate(Turn turn) {
		//PSEUDOCODE
		//If battery level insufficient, do nothing
		//Else, if left or right turn, orient 90 degrees in that direction
		//If turning around, orient 180 degrees
		//Lower energy levels
		//**********
		
		//Ensure that input is usable, user has not passed null object
		assert (turn != null) : "Turn input is not valid, cannot be null";
		//Ensure that controller is valid before use in method
		assert (this.controller != null) : "Controller attribute used before being set";
		
		//Check if robot is stopped
		if (!this.hasStopped()) {
			switch (turn) {
			//If turning around, orient 180 degrees
			case AROUND:
				//Check if battery level insufficient
				if (this.getBatteryLevel() < 2 * CONSTANT_QUARTER_ROTATE_ENERGY) {
					this.hasStopped = true;
					break;
				}
				
				this.controller.setCurrentDirection(this.getCurrentDirection().oppositeDirection());
				
				//Lower energy level for 2 turns
				this.setBatteryLevel(this.getBatteryLevel() - (2 * CONSTANT_QUARTER_ROTATE_ENERGY));
				break;
			//Else, if left or right turn, orient 90 degrees in that direction
			case RIGHT:
				//Check if battery level insufficient
				if (this.getBatteryLevel() < CONSTANT_QUARTER_ROTATE_ENERGY) {
					this.hasStopped = true;
					break;
				}
				
				this.controller.setCurrentDirection(this.getCurrentDirection().rotateClockwise());
				
				//Lower energy level for 1 turn
				this.setBatteryLevel(this.getBatteryLevel() - CONSTANT_QUARTER_ROTATE_ENERGY);
				break;
			case LEFT:
				//Check if battery level insufficient
				if (this.getBatteryLevel() < CONSTANT_QUARTER_ROTATE_ENERGY) {
					this.hasStopped = true;
					break;
				}
				
				this.controller.setCurrentDirection(this.getCurrentDirection().oppositeDirection().rotateClockwise());
				
				//Lower energy level for 1 turn
				this.setBatteryLevel(this.getBatteryLevel() - CONSTANT_QUARTER_ROTATE_ENERGY);
				break;
			}
		}
	}

	/**
	 * Moves robot forward for inputed distance, will prematurely halt if it encounters an obstacle
	 * @param distance as integer for desired distance to travel
	 * @throws IllegalArgumentException for illegal distances
	 */
	@Override
	public void move(int distance) {
		//PSEUDOCODE
		//If battery level insufficient, do nothing
		//Else check tiles within range of distance for walls
		//If no walls exist, move full distance
		//Else stop at nearest wall and set status to stopped
		//Lower energy levels
		//**********
		
		//Ensure that controller is valid before use in method
		assert (this.controller != null) : "Controller attribute used before being set";
		
		//Check for valid input
		if (distance < 1) {
			throw new IllegalArgumentException("Input must be positive");
		}
		
		//Check if robot stopped
		if (!this.hasStopped()) {
			//Walk iteratively through the requested distance
			for (int i = 1; i < distance + 1; i++) {
				//Check if battery level insufficient
				if (this.getBatteryLevel() < CONSTANT_STEP_MOVEMENT_ENERGY) {
					this.hasStopped = true;
					break;
				}
				//Check if encountered a wall
				if (this.controller.getMaze().getFloorplan().hasWall(this.controller.getCurrentPosition()[0], this.controller.getCurrentPosition()[1], this.getCurrentDirection())) {
					//If so, stop and set robot to crashed
					this.hasStopped = true;
					break;
				}
				
				//If no walls exist, move distance
				this.controller.setCurrentPosition(this.controller.getCurrentPosition()[0] + this.getCurrentDirection().getDxDyDirection()[0], this.controller.getCurrentPosition()[1] + this.getCurrentDirection().getDxDyDirection()[1]);
				
				//Add to odometer
				this.odometer++;
				
				//Lower energy levels
				this.setBatteryLevel(this.getBatteryLevel() - CONSTANT_STEP_MOVEMENT_ENERGY);
			}
		}	
	}

	/**
	 * Moves robot forward 1 tile regardless of walls as long as the robot remains within the maze
	 */
	@Override
	public void jump() {
		//PSEUDOCODE
		//If battery level insufficient, do nothing
		//Check forward cell to see if it is within maze
		//If so, move robot forward
		//Lower energy levels
		//**********
		
		//Ensure that controller is valid before use in method
		assert (this.controller != null) : "Controller attribute used before being set";
		
		//Check if robot stopped
		if (!this.hasStopped()) {
			//Check if battery level insufficient
			if (this.getBatteryLevel() < CONSTANT_JUMP_ENERGY) {
				this.hasStopped = true;
			}
			else {
				int newX = this.controller.getCurrentPosition()[0] + this.getCurrentDirection().getDxDyDirection()[0];
				int newY = this.controller.getCurrentPosition()[1] + this.getCurrentDirection().getDxDyDirection()[1];
				//Check forward cell to see if it is within maze
				if (!(newX < 0) && !(newX >= this.controller.getMaze().getWidth()) && !(newY < 0) && !(newY >= this.controller.getMaze().getHeight())) {
					//If so, move robot forward
					this.controller.setCurrentPosition(newX, newY);
					
					//Add to odometer
					this.odometer++;
					
					//Lower energy levels
					this.setBatteryLevel(this.getBatteryLevel() - CONSTANT_JUMP_ENERGY);
				}
				//Else it is crashed
				else {
					this.hasStopped = true;
				}
			}
		}
	}

	/**
	 * Checks if robot is on an exit cell facing any direction
	 * @return True if next to exit, false otherwise
	 */
	@Override
	public boolean isAtExit() {
		//PSEUDOCODE
		//Check all cardinal directions for exit
		//If found return true, else false
		//**********
		
		//Ensure that controller is valid before use in method
		assert (this.controller != null) : "Controller attribute used before being set";
		
		//Doesn't necessarily have to be facing exit, just on a cell that is next to it
		return this.controller.getMaze().getFloorplan().isExitPosition(this.controller.getCurrentPosition()[0], this.controller.getCurrentPosition()[1]);
	}

	/**
	 * Checks if robot is in a room
	 * @return True if in room, false otherwise
	 */
	@Override
	public boolean isInsideRoom() {	
		//PSEUDOCODE
		//Return true if in room, false otherwise
		//**********
		
		//Ensure that controller is valid before use in method
		assert (this.controller != null) : "Controller attribute used before being set";
		return this.controller.getMaze().getFloorplan().isInRoom(this.controller.getCurrentPosition()[0], this.controller.getCurrentPosition()[1]);
	}

	/**
	 * Tells if robot is currently stopped for any reason
	 * @return True if stopped, false otherwise
	 */
	@Override
	public boolean hasStopped() {	
		//PSEUDOCODE
		//Return status from constructor attribute
		//**********
		
		return this.hasStopped;
	}

	/**
	 * Resets hasStopped status to default (false); method not from interface, for testing purposes
	 */
	public void resetHasStopped() {
		this.hasStopped = false;
	}
	
	/**
	 * Reports the distance until an obstacle appears given a direction using mounted sensors
	 * @param direction as Direction for desired direction to observe
	 * @return Distance until obstacle as an integer
	 * @throws UnsupportedOperationException if a sensor is not mounted in that direction or is inoperable
	 */
	@Override
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		//PSEUDOCODE
		//If battery level insufficient, do nothing
		//Check if sensor is mounted in direction
		//If so, call sensor operation
		//Return result
		//Lower energy levels
		//**********
		
		//Ensure that input is not a null object
		assert (direction != null) : "Input cannot be null";
		//Ensure that controller is valid before use in method
		assert (this.controller != null) : "Controller attribute used before being set";
		
		float[] battery = {this.getBatteryLevel()};
		//Set to -1 for error testing; arbitrary value, should get overwritten if method works
		int distanceTo = -1;
		
		//Check if sensor is mounted in direction
		switch (direction) {
		case FORWARD:
			if (this.sensorForward == null) {
				throw new UnsupportedOperationException("Sensor not mounted in forward direction");
			}
			break;
		case BACKWARD:
			if (this.sensorBackward == null) {
				throw new UnsupportedOperationException("Sensor not mounted in backward direction");
			}
			break;
		case RIGHT:
			if (this.sensorRight == null) {
				throw new UnsupportedOperationException("Sensor not mounted in rightward direction");
			}
			break;
		case LEFT:
			if (this.sensorLeft == null) {
				throw new UnsupportedOperationException("Sensor not mounted in leftward direction");
			}
			break;
		}
		//Check if robot is stopped, if so throw error
		if (!this.hasStopped()) {
			//If not, call sensor operation
			switch (direction) {
			case FORWARD:
				//Call distance measurement operation on forward sensor
				try {
					distanceTo = this.sensorForward.distanceToObstacle(this.controller.getCurrentPosition(), this.getCurrentDirection(), battery);
				} catch (Exception e) {
					break;
				}
				
				//Check if returned distance was valid; -1 indicates non-valid sensor call
				if (distanceTo != -1) {
					//Lower energy level from sensing operation
					this.setBatteryLevel(this.getBatteryLevel() - this.sensorForward.getEnergyConsumptionForSensing());
				}
				break;
			case BACKWARD:
				//Call distance measurement operation on backward sensor
				try {
					distanceTo = this.sensorBackward.distanceToObstacle(this.controller.getCurrentPosition(), this.getCurrentDirection().oppositeDirection(), battery);
				} catch (Exception e) {
					break;
				}
				
				//Check if returned distance was valid; -1 indicates non-valid sensor call
				if (distanceTo != -1) {
					//Lower energy level from sensing operation
					this.setBatteryLevel(this.getBatteryLevel() - this.sensorBackward.getEnergyConsumptionForSensing());
				}
				break;
			case RIGHT:
				//Call distance measurement operation on rightward sensor
				try {
					distanceTo = this.sensorRight.distanceToObstacle(this.controller.getCurrentPosition(), this.getCurrentDirection().oppositeDirection().rotateClockwise(), battery);
				} catch (Exception e) {
					break;
				}
				
				//Check if returned distance was valid; -1 indicates non-valid sensor call
				if (distanceTo != -1) {
					//Lower energy level from sensing operation
					this.setBatteryLevel(this.getBatteryLevel() - this.sensorRight.getEnergyConsumptionForSensing());
				}
				break;
			case LEFT:
				//Call distance measurement operation on leftward sensor
				try {
					distanceTo = this.sensorLeft.distanceToObstacle(this.controller.getCurrentPosition(), this.getCurrentDirection().rotateClockwise(), battery);
				} catch (Exception e) {
					break;
				}
				
				//Check if returned distance was valid; -1 indicates non-valid sensor call
				if (distanceTo != -1) {
					//Lower energy level from sensing operation
					this.setBatteryLevel(this.getBatteryLevel() - this.sensorLeft.getEnergyConsumptionForSensing());
				}
				break;
			}
		}
		else {
			throw new UnsupportedOperationException("Robot is currently inoperable");
		}
		return distanceTo;
	}

	/**
	 * Checks given direction to see if an exit can be observed without disruption
	 * @param direction as Direction for desired direction to observe
	 * @return True is exit is observed, false otherwise
	 * @throws UnsupportedOperationException if a sensor is not mounted in that direction or is inoperable
	 */
	@Override
	public boolean canSeeThroughTheExitIntoEternity(Direction direction) throws UnsupportedOperationException {
		//PSEUDOCODE
		//If battery level insufficient, do nothing
		//Check if sensor is mounted in direction
		//If so, call sensor operation
		//Check that result is equal to Integer.MAX_VALUE
		//If so return true, else false
		//**********
		
		//Use existing distance function to observe if an exit is visible, should have value of Integer.MAX_VALUE
		if (this.distanceToObstacle(direction) == Integer.MAX_VALUE) {
			return true;
		}
		return false;
	}

	/**
	 * Will set sensor state to broken and initialize process to repair the sensor, blocks usage until fixed
	 * @param direction as Direction of sensor
	 * @param meanTimeBetweenFailures as integer representing seconds
	 * @param meanTimeToRepair as integer representing seconds
	 * @throws UnsupportedOperationException if not implemented
	 */
	@Override
	public void startFailureAndRepairProcess(Direction direction, int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		//PSEUDOCODE
		//Throw UnsupportedOperationException; not implemented as of now
		//**********
		
		//Not implemented as of now
		throw new UnsupportedOperationException("Will be implemented in next project");
	}

	/**
	 * Stops failure and repair process
	 * @param direction as Direction of sensor
	 * @throws UnsupportedOperationException if not implemented
	 */
	@Override
	public void stopFailureAndRepairProcess(Direction direction) throws UnsupportedOperationException {
		//PSEUDOCODE
		//Throw UnsupportedOperationException; not implemented as of now
		//**********
		
		//Not implemented as of now
		throw new UnsupportedOperationException("Will be implemented in next project");
	}
}
