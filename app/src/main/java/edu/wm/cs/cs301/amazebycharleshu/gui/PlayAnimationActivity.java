package edu.wm.cs.cs301.amazebycharleshu.gui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
//import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.amazebycharleshu.R;
import edu.wm.cs.cs301.amazebycharleshu.generation.Maze;

public class PlayAnimationActivity extends AppCompatActivity {
    //Constant variables
    private final String TAG = "StatePlayAnimationActivity";
    private final float BATTERY_ENERGY = 3500;
    //Global variables used to track player stats
    private int distanceShortest = 0;
    private String crashState;
    //Variable to track game state
    private StatePlaying statePlaying;
    private RobotDriver driver;
    boolean isDriving = true;
    private boolean isPlaying = false;
    private int playSpeed = 1000;

    /**
     * Instantiates activity for PlayAnimationActivity; sets up auto playing screen for user interaction
     * @param savedInstanceState as saved data of instance of activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_animation_activity);

        //Log playing screen activation
        Log.v(TAG, "Auto playing started");

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
        MazePanel mazePanel = findViewById(R.id.autoMazePanel);
        Button mapShow = findViewById(R.id.autoMapShow);
        Button sizeOut = findViewById(R.id.autoSizeZoomOut);
        Button sizeIn = findViewById(R.id.autoSizeZoomIn);
        ProgressBar energyProgress = findViewById(R.id.autoEnergyProgressbar);
        ImageButton playPause = findViewById(R.id.autoPlayPause);
        SeekBar speedSlider = findViewById(R.id.speedSlider);
        ImageView statusFront = findViewById(R.id.autoSensorForward);
        ImageView statusLeft = findViewById(R.id.autoSensorLeft);
        ImageView statusRight = findViewById(R.id.autoSensorRight);
        ImageView statusBack = findViewById(R.id.autoSensorBackward);

        //Set up listeners on each widget to detect and respond to user interactions
        onMapInteraction(mapShow);
        onMapInteraction(sizeOut);
        onMapInteraction(sizeIn);
        onSpeedInteraction(speedSlider);
        onPlayPauseInteraction(playPause);

        //Set up default value for energy progress bar
        energyProgress.setMax((int) BATTERY_ENERGY);
        updateProgress(energyProgress, (int) BATTERY_ENERGY);

        //Calculate shortest travel path possible by pulling pre-built method in maze object and referencing starting position
        //Subtract 1 to account for exit cell
        distanceShortest = maze.getDistanceToExit(maze.getStartingPosition()[0], maze.getStartingPosition()[1]) - 1;

        //Set up StatePlaying instance to pass to robot
        statePlaying = new StatePlaying();
        statePlaying.setMaze(maze);

        //Set up robot based on user selected config settings
        //Assume premium setting as default
        Robot robot = new ReliableRobot();
        DistanceSensor sensorF = new ReliableSensor();
        DistanceSensor sensorR = new ReliableSensor();
        DistanceSensor sensorB = new ReliableSensor();
        DistanceSensor sensorL = new ReliableSensor();
        //Determine sensor configuration from previous user selection
        switch (robotConfig) {
            case "Mediocre":
                robot = new UnreliableRobot();
                sensorF = new ReliableSensor();
                sensorR = new UnreliableSensor();
                sensorB = new ReliableSensor();
                sensorL = new UnreliableSensor();
                break;
            case "Soso":
                robot = new UnreliableRobot();
                sensorF = new UnreliableSensor();
                sensorR = new ReliableSensor();
                sensorB = new UnreliableSensor();
                sensorL = new ReliableSensor();
                break;
            case "Shaky":
                robot = new UnreliableRobot();
                sensorF = new UnreliableSensor();
                sensorR = new UnreliableSensor();
                sensorB = new UnreliableSensor();
                sensorL = new UnreliableSensor();
                break;
        }
        //Pass StatePlaying object and set battery
        robot.setController(statePlaying);
        robot.setBatteryLevel(BATTERY_ENERGY);
        //Pass sensors to robot
        robot.addDistanceSensor(sensorF, Robot.Direction.FORWARD);
        robot.addDistanceSensor(sensorR, Robot.Direction.RIGHT);
        robot.addDistanceSensor(sensorB, Robot.Direction.BACKWARD);
        robot.addDistanceSensor(sensorL, Robot.Direction.LEFT);
        //Start up fail and repair process for UnreliableSensor instances
        int meanTimeBetweenFailures = 4000;
        int meanTimeToRepair = 2000;
        switch (robotConfig) {
            case "Mediocre":
                robot.startFailureAndRepairProcess(Robot.Direction.RIGHT, meanTimeBetweenFailures, meanTimeToRepair);
                try {
                    Thread.sleep(1300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                robot.startFailureAndRepairProcess(Robot.Direction.LEFT, meanTimeBetweenFailures, meanTimeToRepair);
                break;
            case "Soso":
                robot.startFailureAndRepairProcess(Robot.Direction.FORWARD, meanTimeBetweenFailures, meanTimeToRepair);
                try {
                    Thread.sleep(1300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                robot.startFailureAndRepairProcess(Robot.Direction.BACKWARD, meanTimeBetweenFailures, meanTimeToRepair);
                break;
            case "Shaky":
                robot.startFailureAndRepairProcess(Robot.Direction.FORWARD, meanTimeBetweenFailures, meanTimeToRepair);
                try {
                    Thread.sleep(1300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                robot.startFailureAndRepairProcess(Robot.Direction.RIGHT, meanTimeBetweenFailures, meanTimeToRepair);
                try {
                    Thread.sleep(1300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                robot.startFailureAndRepairProcess(Robot.Direction.BACKWARD, meanTimeBetweenFailures, meanTimeToRepair);
                try {
                    Thread.sleep(1300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                robot.startFailureAndRepairProcess(Robot.Direction.LEFT, meanTimeBetweenFailures, meanTimeToRepair);
                break;
        }
        //Set up driver from user selection
        driver = new WallFollower();
        if (driverType.equals("Wizard")) {
            driver = new Wizard();
        }
        //Pass maze and robot to driver
        driver.setMaze(maze);
        driver.setRobot(robot);
        //Start game
        statePlaying.start(mazePanel);
        //Set maze as visible on start up
        statePlaying.handleUserInput(Constants.UserInput.TOGGLELOCALMAP, 0);
        statePlaying.handleUserInput(Constants.UserInput.TOGGLEFULLMAP, 0);
        statePlaying.handleUserInput(Constants.UserInput.TOGGLESOLUTION, 0);

        //Start automated driving
        automatedDriving(energyProgress);
        //Start sensor checkers
        checkSensor(statusFront, statusRight, statusBack, statusLeft);
    }

    /**
     * Begins automated driving sequence with a variable delay to create animation
     * @param progressBar as view reference to draw energy progress bar
     */
    private void automatedDriving(ProgressBar progressBar) {
        //Set up background thread to handle automated driving and delay following single step
        Thread thread = new Thread(() -> {
            //Continue automated driving sequence while driver is not inoperable/at exit
            while(isDriving) {
                //Continue automated driving sequence if user has pressed play button
                if (isPlaying) {
                    try {
                        //Have the driver continuously call the method to mimic walking
                        while (driver.drive1Step2Exit()) {
                            Thread.sleep(playSpeed);
                            Log.v(TAG, "Driving successful; moving robot to: " + statePlaying.getCurrentPosition()[0] + "," + statePlaying.getCurrentPosition()[1]);
                            statePlaying.startDrawer();
                            updateProgress(progressBar, (int) (BATTERY_ENERGY - driver.getEnergyConsumption()));
                            //If user stops animation part-way through drive, halt current loop and wait until user starts up animation
                            if (!isPlaying) {
                                break;
                            }
                        }
                        //If single-step driving loop if broken, check user has paused animation
                        //If not, then we know driver has completed drive
                        //Allow exiting of overall driving loop
                        if (isPlaying) {
                            isDriving = false;
                            //Driver wins
                            startWin();
                        }
                    } catch (Exception e) {
                        //Determine source of crash
                        if (driver.getEnergyConsumption() >= BATTERY_ENERGY - 6) {
                            crashState = "Energy";
                        }
                        else {
                            crashState = "Crashed";
                        }
                        isDriving = false;
                        //Driver loses
                        startLoss();
                    }
                }
            }
        });
        //Start thread
        thread.start();
    }

    /**
     * Begins a background thread which continually checks the operation of the robot's sensors and reports their status
     * @param sensorF as ImageView instance representing the front sensor status
     * @param sensorR as ImageView instance representing the right sensor status
     * @param sensorB as ImageView instance representing the back sensor status
     * @param sensorL as ImageView instance representing the left sensor status
     */
    private void checkSensor(ImageView sensorF, ImageView sensorR, ImageView sensorB, ImageView sensorL) {
        //Set up background thread
        Thread thread = new Thread(() -> {
            //Continue checking sensor status while robot is driving
            while (isDriving) {
                //Only update when animation is playing
                if (isPlaying) {
                    //Type guarding for different driver types
                    if (driver instanceof WallFollower) {
                        //Change ImageView object representing sensor status according to pulled status of sensor in respective direction
                        changeStatus(sensorF, ((WallFollower) driver).isSensorOperating(Robot.Direction.FORWARD));
                        changeStatus(sensorR, ((WallFollower) driver).isSensorOperating(Robot.Direction.RIGHT));
                        changeStatus(sensorB, ((WallFollower) driver).isSensorOperating(Robot.Direction.BACKWARD));
                        changeStatus(sensorL, ((WallFollower) driver).isSensorOperating(Robot.Direction.LEFT));
                    } else {
                        //Change ImageView object representing sensor status according to pulled status of sensor in respective direction
                        changeStatus(sensorF, ((Wizard) driver).isSensorOperating(Robot.Direction.FORWARD));
                        changeStatus(sensorR, ((Wizard) driver).isSensorOperating(Robot.Direction.RIGHT));
                        changeStatus(sensorB, ((Wizard) driver).isSensorOperating(Robot.Direction.BACKWARD));
                        changeStatus(sensorL, ((Wizard) driver).isSensorOperating(Robot.Direction.LEFT));
                    }
                    Log.v(TAG, "Updating sensor status");
                    //Wait a bit until next sensor check
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //Start up thread
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
                case R.id.autoMapShow:
                    Log.v(TAG, "Toggling map visibility");
                    //Toast.makeText(getApplicationContext(), "Toggling map visibility", Toast.LENGTH_LONG).show();
                    statePlaying.handleUserInput(Constants.UserInput.TOGGLELOCALMAP, 0);
                    break;
                //Zoom out of map
                case R.id.autoSizeZoomOut:
                    Log.v(TAG, "Zooming out of map");
                    //Toast.makeText(getApplicationContext(), "Zooming out of map", Toast.LENGTH_LONG).show();
                    statePlaying.handleUserInput(Constants.UserInput.ZOOMOUT, 0);
                    break;
                //Zoom in on map
                case R.id.autoSizeZoomIn:
                    Log.v(TAG, "Zooming in on map");
                    //Toast.makeText(getApplicationContext(), "Zooming in on map", Toast.LENGTH_LONG).show();
                    statePlaying.handleUserInput(Constants.UserInput.ZOOMIN, 0);
                    break;
            }
        });
    }

    /**
     * Updates progress bar for energy remaining
     * @param progressBar as instance of widget for energy progress reporting
     * @param value as value to set energy remaining as
     */
    protected void updateProgress(ProgressBar progressBar, int value) {
        Log.v(TAG, "Energy remaining set to: " + value);
        progressBar.setProgress(value);
    }

    /**
     * Toggles speed of automated playing animation
     * @param speedSlider as instance of widget used to handle user selection of animation speed
     */
    @SuppressLint("NonConstantResourceId")
    protected void onSpeedInteraction(SeekBar speedSlider) {
        //Set up listener on passed seekbar
        speedSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //After user changes speed, change the corresponding global variable
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                playSpeed = (10 - i) * 100;
            }

            //Log when the user first begins interacting with the seekbar
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.v(TAG, "User interacting with animation speed settings");
            }

            //Log when the user stops interacting with the seekbar and selected speed
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.v(TAG, "User has selected animation speed setting: " + playSpeed + " ms");
                //Toast.makeText(getApplicationContext(), "Speed set to " + playSpeed, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Toggles whether automated playing animation is running
     * @param button as widget used to handle user selection of play/pause
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    protected void onPlayPauseInteraction(ImageButton button) {
        //Set up listener on passed button
        button.setOnClickListener(view -> {
            //Check if animation is currently playing
            //If so, then pause animation and set button to play image
            if (isPlaying) {
                Log.v(TAG, "Pausing animation");
                //Toast.makeText(getApplicationContext(), "Pausing animation", Toast.LENGTH_LONG).show();
                button.setImageDrawable(getResources().getDrawable(R.drawable.play, null));
                isPlaying = false;
            }
            //If not, then start animation and set button to pause image
            else {
                Log.v(TAG, "Starting animation");
                //Toast.makeText(getApplicationContext(), "Starting animation", Toast.LENGTH_LONG).show();
                button.setImageDrawable(getResources().getDrawable(R.drawable.pause, null));
                isPlaying = true;
            }
        });
    }

    /**
     * Changes status of sensor status reporters
     * @param image as instance of widget used to represent robot sensors
     * @param isOperating as boolean on whether sensor is operating
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    protected void changeStatus(ImageView image, boolean isOperating) {
        //If sensor is operating, then set to green circle
        if (isOperating) {
            image.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_online, null));
        }
        //Else red circle
        else {
            image.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_busy, null));
        }
    }

    /**
     * Switch to winning activity upon confirmation of driver win
     */
    private void startWin() {
        Log.v(TAG, "Driver has won; moving to win screen");
        //Create intent for win screen
        Intent finishGame = new Intent(this, WinningActivity.class);
        //Create and attach bundle to pass over player stats for end game report
        Bundle passedParameters = new Bundle();
        passedParameters.putBoolean("isManual", false);
        passedParameters.putInt("distanceTravelled", driver.getPathLength());
        passedParameters.putInt("distanceShortest", distanceShortest);
        passedParameters.putFloat("energyConsumed", driver.getEnergyConsumption());
        finishGame.putExtras(passedParameters);
        //Start win screen activity and finish auto playing activity
        startActivity(finishGame);
        finish();
    }

    /**
     * Switch to losing activity upon confirmation of driver loss
     */
    private void startLoss() {
        Log.v(TAG, "Driver has lost; moving to losing screen");
        //Create intent for lose screen
        Intent finishGame = new Intent(this, LosingActivity.class);
        //Create and attach bundle to pass over player stats for end game report
        Bundle passedParameters = new Bundle();
        passedParameters.putInt("distanceTravelled", driver.getPathLength());
        passedParameters.putInt("distanceShortest", distanceShortest);
        passedParameters.putFloat("energyConsumed", driver.getEnergyConsumption());
        passedParameters.putString("crashState", crashState);
        finishGame.putExtras(passedParameters);
        //Start lose screen activity and finish auto playing activity
        startActivity(finishGame);
        finish();
    }
}