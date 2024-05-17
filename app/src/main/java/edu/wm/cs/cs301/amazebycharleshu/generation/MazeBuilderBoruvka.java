package edu.wm.cs.cs301.amazebycharleshu.generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class MazeBuilderBoruvka extends MazeBuilder implements Runnable {
	
	private static final Logger LOGGER = Logger.getLogger(MazeBuilderBoruvka.class.getName());
	//Will hold association maps for a wall in the maze and its unique weight
	private HashMap<Wallboard, Integer> weightsList;
	//Will hold trees to track minimal spanning trees for each cell
	private ArrayList<ArrayList<Integer[]>> treesList;
	
	/**
	 * Constructor for MazeBuilderBoruvka class; initializes hashmap for unique weight tracking and arraylist for tree storage
	 */
	public MazeBuilderBoruvka() {
		super();
		LOGGER.config("Using Boruvka's algorithm to generate maze.");
		this.weightsList = new HashMap<Wallboard, Integer>();
		this.treesList = new ArrayList<ArrayList<Integer[]>>();
	}
	/**
	 * Generate randomized weights for every cell in the maze and associate it with every wall in the maze via object hashmap attribute weightsList
	 */
	private void initializeWeights() {
		ArrayList<Integer> cellWeights = new ArrayList<Integer>();
		int cellsFilled = 0;
		//Calculate amount of internal walls in a fully filled maze with no gaps
		int wallCount = (width * (height - 1)) + ((width - 1) * height);
		//Generate unique random numbers until we have a value for every wall in the maze
		while (cellsFilled < wallCount) {
			int weightToAdd = random.nextIntWithinInterval(1, wallCount);
			if (!cellWeights.contains(weightToAdd) && weightToAdd > 0) {
				cellWeights.add(weightToAdd);
				cellsFilled++;
			}
		}
		
		//Want to assign a weight via the random values to each internal-facing wall of a cell
		//First assign corner cell walls
		//(0,0)
		Wallboard wallEast = new Wallboard(0, 0, CardinalDirection.East);
		Wallboard wallSouth = new Wallboard(0, 0, CardinalDirection.South);
		this.weightsList.put(wallEast, cellWeights.remove(0));
		this.weightsList.put(wallSouth, cellWeights.remove(0));
		//(width-1,0)
		wallSouth = new Wallboard(width - 1, 0, CardinalDirection.South);
		Wallboard wallWest = new Wallboard(width - 1, 0, CardinalDirection.West);
		this.weightsList.put(wallSouth, cellWeights.remove(0));
		this.weightsList.put(wallWest, cellWeights.remove(0));
		//(0, height-1)
		Wallboard wallNorth = new Wallboard(0, height - 1, CardinalDirection.North);
		wallEast = new Wallboard(0, height - 1, CardinalDirection.East);
		this.weightsList.put(wallNorth, cellWeights.remove(0));
		this.weightsList.put(wallEast, cellWeights.remove(0));
		//(width-1,height-1)
		wallNorth = new Wallboard(width - 1, height - 1, CardinalDirection.North);
		wallWest = new Wallboard(width - 1, height - 1, CardinalDirection.West);
		this.weightsList.put(wallNorth, cellWeights.remove(0));
		this.weightsList.put(wallWest, cellWeights.remove(0));
		
		//Now intermediate side walls
		//Upward cells; uniquely generate east and south, get existing west
		for (int i = 1; i < width - 2; i++) {
			wallEast = new Wallboard(i, 0, CardinalDirection.East);
			wallSouth = new Wallboard(i, 0, CardinalDirection.South);
			wallWest = new Wallboard(i, 0, CardinalDirection.West);
			this.weightsList.put(wallEast, cellWeights.remove(0));
			this.weightsList.put(wallSouth, cellWeights.remove(0));
			this.weightsList.put(wallWest, getEdgeWeight(i - 1, 0, CardinalDirection.East));
		}
		//Account for cell adjacent to existing corner cell
		wallEast = new Wallboard(width - 2, 0, CardinalDirection.East);
		wallSouth = new Wallboard(width - 2, 0, CardinalDirection.South);
		wallWest = new Wallboard(width - 2, 0, CardinalDirection.West);
		this.weightsList.put(wallEast, getEdgeWeight(width - 1, 0, CardinalDirection.West));
		this.weightsList.put(wallSouth, cellWeights.remove(0));
		this.weightsList.put(wallWest, getEdgeWeight(width - 3, 0, CardinalDirection.East));		
		//Rightward cells; uniquely generate south and west, get existing north
		for (int i = 1; i < height - 2; i++) {
			wallNorth = new Wallboard(width - 1, i, CardinalDirection.North);
			wallSouth = new Wallboard(width - 1, i, CardinalDirection.South);
			wallWest = new Wallboard(width - 1, i, CardinalDirection.West);
			this.weightsList.put(wallNorth, getEdgeWeight(width - 1, i - 1, CardinalDirection.South));
			this.weightsList.put(wallSouth, cellWeights.remove(0));
			this.weightsList.put(wallWest, cellWeights.remove(0));
		}
		//Account for cell adjacent to existing corner cell
		wallNorth = new Wallboard(width - 1, height - 2, CardinalDirection.North);
		wallSouth = new Wallboard(width - 1, height - 2, CardinalDirection.South);
		wallWest = new Wallboard(width - 1, height - 2, CardinalDirection.West);
		this.weightsList.put(wallNorth, getEdgeWeight(width - 1, height - 3, CardinalDirection.South));
		this.weightsList.put(wallSouth, getEdgeWeight(width - 1, height - 1, CardinalDirection.North));
		this.weightsList.put(wallWest, cellWeights.remove(0));
		//Downward cells; uniquely generate north and east, get existing west
		for (int i = 1; i < width - 2; i++) {
			wallNorth = new Wallboard(i, height - 1, CardinalDirection.North);
			wallEast = new Wallboard(i, height - 1, CardinalDirection.East);
			wallWest = new Wallboard(i, height - 1, CardinalDirection.West);
			this.weightsList.put(wallNorth, cellWeights.remove(0));
			this.weightsList.put(wallEast, cellWeights.remove(0));
			this.weightsList.put(wallWest, getEdgeWeight(i - 1, height - 1, CardinalDirection.East));
		}
		//Account for cell adjacent to existing corner cell
		wallNorth = new Wallboard(width - 2, height - 1, CardinalDirection.North);
		wallEast = new Wallboard(width - 2, height - 1, CardinalDirection.East);
		wallWest = new Wallboard(width - 2, height - 1, CardinalDirection.West);
		this.weightsList.put(wallNorth, cellWeights.remove(0));
		this.weightsList.put(wallEast, getEdgeWeight(width - 1, height - 1, CardinalDirection.West));
		this.weightsList.put(wallWest, getEdgeWeight(width - 3, height - 1, CardinalDirection.East));
		//Leftward cells; uniquely generate east and south, get existing north
		for (int i = 1; i < height - 2; i++) {
			wallNorth = new Wallboard(0, i, CardinalDirection.North);
			wallEast = new Wallboard(0, i, CardinalDirection.East);
			wallSouth = new Wallboard(0, i, CardinalDirection.South);
			this.weightsList.put(wallNorth, getEdgeWeight(0, i - 1, CardinalDirection.South));
			this.weightsList.put(wallEast, cellWeights.remove(0));
			this.weightsList.put(wallSouth, cellWeights.remove(0));
		}
		//Account for cell adjacent to existing corner cell
		wallNorth = new Wallboard(0, height - 2, CardinalDirection.North);
		wallEast = new Wallboard(0, height - 2, CardinalDirection.East);
		wallSouth = new Wallboard(0, height - 2, CardinalDirection.South);
		this.weightsList.put(wallNorth, getEdgeWeight(0, height - 3, CardinalDirection.South));
		this.weightsList.put(wallEast, cellWeights.remove(0));
		this.weightsList.put(wallSouth, getEdgeWeight(0, height - 1, CardinalDirection.North));
		
		//Now interior cells
		//Uniquely generate east and south walls, get existing north and west walls
		for (int i = 1; i < width - 2; i++) {
			for (int j = 1; j < height - 2; j++) {
				wallNorth = new Wallboard(i, j, CardinalDirection.North);
				wallEast = new Wallboard(i, j, CardinalDirection.East);
				wallSouth = new Wallboard(i, j, CardinalDirection.South);
				wallWest = new Wallboard(i, j, CardinalDirection.West);
				this.weightsList.put(wallNorth, getEdgeWeight(i, j - 1, CardinalDirection.South));
				this.weightsList.put(wallEast, cellWeights.remove(0));
				this.weightsList.put(wallSouth, cellWeights.remove(0));
				this.weightsList.put(wallWest, getEdgeWeight(i - 1, j, CardinalDirection.East));
			}
		}
		//Account for unique cases below interior cells and between bottom cells
		//Generate east, get existing north, south, west
		for (int i = 1; i < width - 2; i++) {
			wallNorth = new Wallboard(i, height - 2, CardinalDirection.North);
			wallEast = new Wallboard(i, height - 2, CardinalDirection.East);
			wallSouth = new Wallboard(i, height - 2, CardinalDirection.South);
			wallWest = new Wallboard(i, height - 2, CardinalDirection.West);
			this.weightsList.put(wallNorth, getEdgeWeight(i, height - 3, CardinalDirection.South));
			this.weightsList.put(wallEast, cellWeights.remove(0));
			this.weightsList.put(wallSouth, getEdgeWeight(i, height - 1, CardinalDirection.North));
			this.weightsList.put(wallWest, getEdgeWeight(i - 1, height - 2, CardinalDirection.East));
		}
		//Account for unique cases between interior cells rightward cells
		//Generate south, get existing north, east, west
		for (int i = 1; i < height - 2; i++) {
			wallNorth = new Wallboard(width - 2, i, CardinalDirection.North);
			wallEast = new Wallboard(width - 2, i, CardinalDirection.East);
			wallSouth = new Wallboard(width - 2, i, CardinalDirection.South);
			wallWest = new Wallboard(width - 2, i, CardinalDirection.West);
			this.weightsList.put(wallNorth, getEdgeWeight(width - 2, i - 1, CardinalDirection.South));
			this.weightsList.put(wallEast, getEdgeWeight(width - 1, i, CardinalDirection.West));
			this.weightsList.put(wallSouth, cellWeights.remove(0));
			this.weightsList.put(wallWest, getEdgeWeight(width - 3, i, CardinalDirection.East));
		}
		//Account for unique case of remaining cell to diagonal left-up of last cell in maze (width-1, height-1)
		//Get all edges
		wallNorth = new Wallboard(width - 2, height - 2, CardinalDirection.North);
		wallEast = new Wallboard(width - 2, height - 2, CardinalDirection.East);
		wallSouth = new Wallboard(width - 2, height - 2, CardinalDirection.South);
		wallWest = new Wallboard(width - 2, height - 2, CardinalDirection.West);
		this.weightsList.put(wallNorth, getEdgeWeight(width - 2, height - 3, CardinalDirection.South));
		this.weightsList.put(wallEast, getEdgeWeight(width - 1, height - 2, CardinalDirection.West));
		this.weightsList.put(wallSouth, getEdgeWeight(width - 2, height - 1, CardinalDirection.North));
		this.weightsList.put(wallWest, getEdgeWeight(width - 3, height - 2, CardinalDirection.East));
	}
	
	/**
	 * Gets value of unique weight for desired wall edge
	 * @param x integer coordinate of cell
	 * @param y integer coordinate of cell
	 * @param cd direction of edge as CardinalDirection
	 * @return Weight of wall edge as integer value greater than 0; 0 if does not exist
	 */
	public int getEdgeWeight(int x, int y, CardinalDirection cd) {
		//Using given x, y, and cd, pull from weight structure instantiated in generatePathways to get corresponding edge weight
		for (Wallboard wall : this.weightsList.keySet()) {
			if (wall.getX() == x && wall.getY() == y && wall.getDirection() == cd) {
				return this.weightsList.get(wall);
			}
		}
		return 0;
	}
	
	/**
	 * Create a tree for every cell in the maze; place such trees into an overall tree arraylist object treesList
	 */
	private void initializeTrees() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				ArrayList<Integer[]> tree = new ArrayList<Integer[]>();
				Integer[] cell = {i, j};
				tree.add(cell);
				this.treesList.add(tree);
			}
		}
	}
	
	/**
	 * Generates a maze by removing wallboards from a full maze utilizing the Boruvka algorithm.
	 * Will first generate random unique weights for every cell and then create a tree for every cell.
	 * Will follow Boruvka algorithm in maze generation: For each tree, observe the available weights of each cell contained by the tree and remove the walls of the lowest weight to effectively join trees.
	 */
	@Override
	protected void generatePathways() {
		//Instantiate <key,value> pair structure to store weights for edges between cells where key is set of x coordinate, y coordinate, and direction, and value is the edge weight
		//getEdgeWeight will pull from this structure; should ensure consistency in persistent edge weights across maze
		initializeWeights();
		
		//Generate a structure to keep track of each tree on the maze
		//This structure will contain information on 1) which tree is this 2) the cells that are part of this tree
		//This tree will act as a tree tracker and manager for Boruvka during the component merging phase

		//Mark every cell in maze as a tree
		initializeTrees();
		
		//Access a tree from the tracking structure defined above
		//Loop will access each available tree in structure until only one tree remains; should be our MST with correctly generated maze
		while (this.treesList.size() > 1) {
			//Lowest weight value found; arbitrarily assigned high value for comparison
			int lowestWeight = 9999999;
			//Wallboard of lowest weight cell; arbitrarily assigned value, will be overwritten when actually used
			Wallboard lowestWeightCell = new Wallboard(0, 0, CardinalDirection.East);
			//Pull out the first tree available; this will act as our reference point in the maze
			ArrayList<Integer[]> currentTree = this.treesList.remove(0);
			//For our accessed tree, iterate through all cells included in the tree
			//Will check all available weights surrounding every cell that is in the currently observed tree
			for (Integer[] cell : currentTree) {
				//Pull weights that surround the cell
				int weightNorth = getEdgeWeight(cell[0], cell[1], CardinalDirection.North);
				int weightEast = getEdgeWeight(cell[0], cell[1], CardinalDirection.East);
				int weightSouth = getEdgeWeight(cell[0], cell[1], CardinalDirection.South);
				int weightWest = getEdgeWeight(cell[0], cell[1], CardinalDirection.West);
				//Initialize walls that surround the cell
				Wallboard wallNorth = new Wallboard(cell[0], cell[1], CardinalDirection.North);
				Wallboard wallEast = new Wallboard(cell[0], cell[1], CardinalDirection.East);
				Wallboard wallSouth = new Wallboard(cell[0], cell[1], CardinalDirection.South);
				Wallboard wallWest = new Wallboard(cell[0], cell[1], CardinalDirection.West);
				
				//Corner case: When determining the lowest available weight, will sometimes pull a weight which connects to a cell which is already in the current tree
				//Will lead to error where later iterations through remaining trees will result in indexing error due to cell not being found there
				//Mitigate this by setting weight of all adjacent cells in the current tree to 0
				for (Integer[] checkCell : currentTree) {
					if (checkCell[0] == wallNorth.getNeighborX() && checkCell[1] == wallNorth.getNeighborY()) {
						weightNorth = 0;
					}
					if (checkCell[0] == wallEast.getNeighborX() && checkCell[1] == wallEast.getNeighborY()) {
						weightEast = 0;
					}
					if (checkCell[0] == wallSouth.getNeighborX() && checkCell[1] == wallSouth.getNeighborY()) {
						weightSouth = 0;
					}
					if (checkCell[0] == wallWest.getNeighborX() && checkCell[1] == wallWest.getNeighborY()) {
						weightWest = 0;
					}
				}
				
				//Iterate through all weights to find the lowest available one
				if (weightNorth < lowestWeight && weightNorth > 0) {
					lowestWeight = weightNorth;
					lowestWeightCell = wallNorth;
				}
				if (weightEast < lowestWeight && weightEast > 0) {
					lowestWeight = weightEast;
					lowestWeightCell = wallEast;
				}
				if (weightSouth < lowestWeight && weightSouth > 0) {
					lowestWeight = weightSouth;
					lowestWeightCell = wallSouth;
				}
				if (weightWest < lowestWeight && weightWest > 0) {
					lowestWeight = weightWest;
					lowestWeightCell = wallWest;
				}
			}
			
			//Need to find direction of neighbor cell edge that shares the wall found for the lowest weight edge
			//Direction of neighbor cell that shares wall should always be opposite direction
			//Arbitrarily assigned value for direction, will change when actually used
			CardinalDirection neighborDirection = CardinalDirection.West;
			if (lowestWeightCell.getDirection() == CardinalDirection.North) {
				neighborDirection = CardinalDirection.South;
			}
			else if (lowestWeightCell.getDirection() == CardinalDirection.South) {
				neighborDirection = CardinalDirection.North;
			}
			else if (lowestWeightCell.getDirection() == CardinalDirection.West) {
				neighborDirection = CardinalDirection.East;
			}
			//Need to find which tree neighbor cell exists in for later tree union
			//Initial values are arbitrarily assigned, will change when actually used
			Wallboard neighborCell = new Wallboard(lowestWeightCell.getNeighborX(), lowestWeightCell.getNeighborY(), neighborDirection);
			ArrayList<Integer[]> neighborTree = new ArrayList<Integer[]>();
			int neighborTreeIndex = -1;
			//Access the overall tree tracker structure and find the tree that contains the neighbor cell
			for (int i = 0; i < this.treesList.size(); i++) {
				ArrayList<Integer[]> iteratedTree = this.treesList.get(i);
				for (Integer[] cell : iteratedTree) {
					if (cell[0] == neighborCell.getX() && cell[1] == neighborCell.getY()) {
						neighborTree = iteratedTree;
						neighborTreeIndex = i;
					}
				}
			}
			//Append the cells from the newly accessed neighbor tree into the current tree
			currentTree.addAll(neighborTree);
			//Add current tree back into overall tree tracking structure
			this.treesList.add(currentTree);
			//Delete the neighbor tree; essentially trying to mimic tree union in Boruvka
			this.treesList.remove(neighborTreeIndex);
			
			//Remove the wall in the direction of the lowest weight
			floorplan.deleteWallboard(lowestWeightCell);
			//Do the same on the neighbor cell in order to ensure that the wallboard is full removed from all directions
			ArrayList<Wallboard> wallsToDelete = new ArrayList<Wallboard>();
			//First find the weight <K,V> pair
			for (Wallboard wall : this.weightsList.keySet()) {
				if (wall.getX() == lowestWeightCell.getX() && wall.getY() == lowestWeightCell.getY() && wall.getDirection() == lowestWeightCell.getDirection()) {
					wallsToDelete.add(wall);
				}
				if (wall.getX() == neighborCell.getX() && wall.getY() == neighborCell.getY() && wall.getDirection() == neighborCell.getDirection()) {
					wallsToDelete.add(wall);
				}
			}
			//Then delete
			for (Wallboard wall : wallsToDelete) {
				this.weightsList.remove(wall);
			}
		}
	}
}
