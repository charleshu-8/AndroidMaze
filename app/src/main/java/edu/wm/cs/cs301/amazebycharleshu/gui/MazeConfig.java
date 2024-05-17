package edu.wm.cs.cs301.amazebycharleshu.gui;

import edu.wm.cs.cs301.amazebycharleshu.generation.Maze;

/**
 * Provides a singleton to store a given instance of a Maze object.
 * Only set after maze has just been generated in generating activity; otherwise only ever read by manual/auto playing activities
 *
 * @author Charles Hu
 */

public class MazeConfig {
    //Private variable to hold maze
    private Maze maze;
    //Instantiation of singleton object
    private static final MazeConfig mazeConfig = new MazeConfig();

    /**
     * Gets stored maze object
     * @return Maze as given object
     */
    public Maze getMaze() {
        return maze;
    }

    /**
     * Stores given maze object
     * @param maze as Maze object to store in singleton
     */
    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    /**
     * Used to get instance of self-generated singleton object; needs to be used to access stored item
     * @return mazeConfig as instance of MazeConfig used to store Maze object
     */
    public static MazeConfig getInstance() {
        return mazeConfig;
    }
}
