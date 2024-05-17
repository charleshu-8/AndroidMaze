package edu.wm.cs.cs301.amazebycharleshu.gui;

/**
 * 
 * Class name: UnreliableRobot
 * 
 * Responsibilities: Set up and handle initialization and usage of robot using UnreliableSensor class; handle movement operations (move tile, jump, rotate); track current position and room-value of current position;
 * handle & track energy consumption; recognize exit; track and handle distance moved; handle direction of the robot; track and handle sensor breakdown via repair sequence
 * 
 * Collaborators: Control, DistanceSensor, ReliableRobot
 * 
 * @author Charles Hu
 *
 */

public class UnreliableRobot extends ReliableRobot {
	
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
		//Will start up failure and repair process for given sensor
		//**********
		
		//Given a direction, attempt to start up the fail and repair process for the sensor in that direction
		//If the called sensor is not of class UnreliableSensor, will throw UnsupportedOperationException
		switch (direction) {
		case FORWARD:
			this.sensorForward.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		case BACKWARD:
			this.sensorBackward.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		case RIGHT:
			this.sensorRight.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		case LEFT:
			this.sensorLeft.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		}
	}

	/**
	 * Stops failure and repair process
	 * @param direction as Direction of sensor
	 * @throws UnsupportedOperationException if not implemented
	 */
	@Override
	public void stopFailureAndRepairProcess(Direction direction) throws UnsupportedOperationException {
		//PSEUDOCODE
		//Will attempt to kill failure and repair process for given sensor
		//Throw error if attempt fails
		//**********
		
		//Given a direction, attempt to stop the fail and repair process for the sensor in that direction
		//If the called sensor is not of class UnreliableSensor, will throw UnsupportedOperationException
		switch (direction) {
		case FORWARD:
			this.sensorForward.stopFailureAndRepairProcess();
			break;
		case BACKWARD:
			this.sensorBackward.stopFailureAndRepairProcess();
			break;
		case RIGHT:
			this.sensorRight.stopFailureAndRepairProcess();
			break;
		case LEFT:
			this.sensorLeft.stopFailureAndRepairProcess();
			break;
		}
	}
}
