package edu.wm.cs.cs301.amazebycharleshu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
//import android.widget.Toast;

import edu.wm.cs.cs301.amazebycharleshu.R;
import edu.wm.cs.cs301.amazebycharleshu.generation.Maze;

public class PlayManuallyActivity extends AppCompatActivity {
    //Constant variables
    private final String TAG = "StatePlayManuallyActivity";
    //Global variables used to track player stats
    private int distanceTravelled = 0;
    private int distanceShortest = 0;
    //Variable to track game state
    private StatePlaying statePlaying;

    /**
     * Instantiates activity for PlayManuallyActivity; sets up manual playing screen for user interaction
     * @param savedInstanceState as saved data of instance of activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_manually_activity);

        //Log playing screen activation
        Log.v(TAG, "Manual playing started");

        //Obtain robot settings from passed intent
        Intent genIntent = getIntent();
        Bundle passedParameters = genIntent.getExtras();
        String driverType = passedParameters.getString("driverType");
        String robotConfig = passedParameters.getString("robotConfig");
        Log.v(TAG, "Obtained driver type parameter: " + driverType);
        Log.v(TAG, "Obtained sensor config parameter: " + robotConfig);

        //Obtain generated maze
        Maze maze = MazeConfig.getInstance().getMaze();
        Log.v(TAG, "Obtained generated maze of size: " + maze.getWidth() + "x" + maze.getHeight());

        //Generate instance of each widget used on playing screen for adding functionality
        MazePanel mazePanel = findViewById(R.id.manualMazePanel);
        Button mapShow = findViewById(R.id.manualMapShow);
        Button mapSolution = findViewById(R.id.manualMapSolution);
        Button mapWalls = findViewById(R.id.manualMapWalls);
        Button sizeOut = findViewById(R.id.manualSizeZoomOut);
        Button sizeIn = findViewById(R.id.manualSizeZoomIn);
        ImageButton moveUp = findViewById(R.id.manualMoveUp);
        ImageButton moveLeft = findViewById(R.id.manualMoveLeft);
        ImageButton moveRight = findViewById(R.id.manualMoveRight);
        ImageButton moveJump = findViewById(R.id.manualMoveJump);

        //Set up listeners on each widget to detect and respond to user interactions
        onMapInteraction(mapShow);
        onMapInteraction(mapSolution);
        onMapInteraction(mapWalls);
        onMapInteraction(sizeOut);
        onMapInteraction(sizeIn);
        onMovementInteraction(moveUp);
        onMovementInteraction(moveLeft);
        onMovementInteraction(moveRight);
        onMovementInteraction(moveJump);

        //Calculate shortest travel path possible by pulling pre-built method in maze object and referencing starting position
        distanceShortest = maze.getDistanceToExit(maze.getStartingPosition()[0], maze.getStartingPosition()[1]);

        //Set up StatePlaying instance and pass maze and GUI interface
        //Game starts here
        statePlaying = new StatePlaying();
        statePlaying.setMaze(maze);
        statePlaying.start(mazePanel);
        //Start up thread to monitor game status
        checkPlayState();
    }

    /**
     * Sets up background thread to continually monitor game status; when user has won, start up win screen activity
     */
    private void checkPlayState() {
        //Set up background thread
        Thread thread = new Thread(() -> {
            //Thread will loop status call on StatePlaying object until player wins and deactivates the game
            Log.v(TAG, "Game started; monitoring game status");
            while(statePlaying.isPlaying()) {
                //Does nothing until we know game has ended
            }
            //Start up intent call for next activity and kill current activity
            startWin();
        });
        thread.start();
    }

    /**
     * Toggles the maze map settings based on user interaction
     * @param button as instance of widget used for map setting toggling
     */
    @SuppressLint("NonConstantResourceId")
    protected void onMapInteraction(Button button) {
        //Set up listener on passed button
        button.setOnClickListener(view -> {
            //Want to determine which button was selected
            //Obtain id and adjust map based on such
            switch (button.getId()) {
                //Toggle map visibility
                case R.id.manualMapShow:
                    Log.v(TAG, "Toggling map visibility");
                    //Toast.makeText(getApplicationContext(), "Toggling map visibility", Toast.LENGTH_LONG).show();
                    statePlaying.handleUserInput(Constants.UserInput.TOGGLELOCALMAP, 0);
                    break;
                //Toggle map solution visibility
                case R.id.manualMapSolution:
                    Log.v(TAG, "Toggling map solution visibility");
                    //Toast.makeText(getApplicationContext(), "Toggling map solution visibility", Toast.LENGTH_LONG).show();
                    statePlaying.handleUserInput(Constants.UserInput.TOGGLESOLUTION, 0);
                    break;
                //Toggle map wall visibility
                case R.id.manualMapWalls:
                    Log.v(TAG, "Toggling map wall visibility");
                    //Toast.makeText(getApplicationContext(), "Toggling map wall visibility", Toast.LENGTH_LONG).show();
                    statePlaying.handleUserInput(Constants.UserInput.TOGGLEFULLMAP, 0);
                    break;
                //Zoom out of map
                case R.id.manualSizeZoomOut:
                    Log.v(TAG, "Zooming out of map");
                    //Toast.makeText(getApplicationContext(), "Zooming out of map", Toast.LENGTH_LONG).show();
                    statePlaying.handleUserInput(Constants.UserInput.ZOOMOUT, 0);
                    break;
                //Zoom in on map
                case R.id.manualSizeZoomIn:
                    Log.v(TAG, "Zooming in on map");
                    //Toast.makeText(getApplicationContext(), "Zooming in on map", Toast.LENGTH_LONG).show();
                    statePlaying.handleUserInput(Constants.UserInput.ZOOMIN, 0);
                    break;
            }
        });
    }

    /**
     * Moves player based on selected movement button
     * @param button as instance of widget used for movement selection
     */
    @SuppressLint("NonConstantResourceId")
    protected void onMovementInteraction(ImageButton button) {
        //Set up listener on passed button
        button.setOnClickListener(view -> {
            //Want to determine which button was selected
            //Obtain id and make movement based on such
            switch (button.getId()) {
                //Move forwards
                case R.id.manualMoveUp:
                    Log.v(TAG, "Moving forwards");
                    //Toast.makeText(getApplicationContext(), "Moving forwards", Toast.LENGTH_LONG).show();
                    statePlaying.handleUserInput(Constants.UserInput.UP, 0);
                    //Add 1 to distance travelled
                    distanceTravelled += 1;
                    break;
                //Turning left
                case R.id.manualMoveLeft:
                    Log.v(TAG, "Moving left");
                    //Toast.makeText(getApplicationContext(), "Moving left", Toast.LENGTH_LONG).show();
                    statePlaying.handleUserInput(Constants.UserInput.LEFT, 0);
                    break;
                //Turning right
                case R.id.manualMoveRight:
                    Log.v(TAG, "Moving right");
                    //Toast.makeText(getApplicationContext(), "Moving right", Toast.LENGTH_LONG).show();
                    statePlaying.handleUserInput(Constants.UserInput.RIGHT, 0);
                    break;
                //Jumping
                case R.id.manualMoveJump:
                    Log.v(TAG, "Jumping");
                    //Toast.makeText(getApplicationContext(), "Jumping", Toast.LENGTH_LONG).show();
                    //Add 1 to distance travelled
                    distanceTravelled += 1;
                    statePlaying.handleUserInput(Constants.UserInput.JUMP, 0);
                    break;
            }
            //Reporting overall distance moved
            Log.v(TAG, "Total distance moved: " + distanceTravelled);
        });
    }

    /**
     * Switch to winning activity upon confirmation of user win
     */
    private void startWin() {
        Log.v(TAG, "Player has won; moving to win screen");
        //Create intent for win screen
        Intent finishGame = new Intent(this, WinningActivity.class);
        //Create and attach bundle to pass over player stats for end game report
        Bundle passedParameters = new Bundle();
        passedParameters.putBoolean("isManual", true);
        passedParameters.putInt("distanceTravelled", distanceTravelled);
        passedParameters.putInt("distanceShortest", distanceShortest);
        finishGame.putExtras(passedParameters);
        //Start win screen activity and finish manual playing activity
        startActivity(finishGame);
        finish();
    }
}