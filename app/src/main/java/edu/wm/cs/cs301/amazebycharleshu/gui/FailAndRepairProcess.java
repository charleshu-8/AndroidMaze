package edu.wm.cs.cs301.amazebycharleshu.gui;

/**
 * Class name: FailAndRepairProcess
 * 
 * Responsibilities: Create individual thread for sensor fail and repair process; report if sensor is operational
 * 
 * Collaborators: Runnable
 * 
 * @author Charles Hu
 *
 */

public class FailAndRepairProcess implements Runnable{
	Boolean isOperating;
	int meanTimeBetweenFailures;
	int meanTimeToRepair;
	Boolean killThread;
	
	/**
	 * Constructor for FailAndRepairProcess objects; set up time interval variables and thread handling variables for later use when thread is generated
	 * @param meanTimeBetweenFailures as integer for constant time before failure occurs
	 * @param meanTimeToRepair as integer for constant time it takes to repair a failure
	 */
	public FailAndRepairProcess(int meanTimeBetweenFailures, int meanTimeToRepair) {
		this.isOperating = true;
		this.meanTimeBetweenFailures = meanTimeBetweenFailures;
		this.meanTimeToRepair = meanTimeToRepair;
		this.killThread = false;
	}
	
	/**
	 * Return if an associated sensor is currently operable/inoperable per fail and repair process
	 * @return Operating status as boolean
	 */
	public Boolean getIsOperating() {
		return this.isOperating;
	}
	
	/**
	 * Set killThread attribute for thread control
	 * @param killStatus as boolean for desired killing of thread
	 */
	public void setKillThread(Boolean killStatus) {
		this.killThread = killStatus;
	}
	
	/**
	 * Thread generated for an UnreliableSensor class to facilitate independent fail and repair process
	 */
	@Override
	public void run() {
		//Check thread controller attribute to see if user wants to kill it
		//If false, then continue looping process until true
		while (!this.killThread) {
			//Give time before failure occurs
			try {
				Thread.sleep(meanTimeBetweenFailures);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//Failure has occurred, setting operating status to inoperable
			this.isOperating = false;
			
			//Give time before repair occurs
			try {
				Thread.sleep(meanTimeToRepair);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//Repair has occurred, setting operating status to operable
			this.isOperating = true;
		}
	}
}
