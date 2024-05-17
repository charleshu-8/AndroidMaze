package edu.wm.cs.cs301.amazebycharleshu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
//import android.widget.Toast;

import java.util.Objects;
import java.util.Random;

import edu.wm.cs.cs301.amazebycharleshu.R;
import edu.wm.cs.cs301.amazebycharleshu.generation.DefaultOrder;
import edu.wm.cs.cs301.amazebycharleshu.generation.Maze;
import edu.wm.cs.cs301.amazebycharleshu.generation.MazeFactory;
import edu.wm.cs.cs301.amazebycharleshu.generation.Order;

public class GeneratingActivity extends AppCompatActivity {
    //Constant variables
    private final String TAG = "StateGeneratingActivity";
    private final boolean IS_DETERMINISTIC = false;
    //Global variables to store widget instances
    private TextView genCompleteNotice;
    private TextView genIncompleteNotice;
    private TextView genCompleteContinue;
    private TextView genCompleteWarn;
    //Global variables used to track robot parameters for later generation
    private boolean isGenerationDone = false;
    private String driverType = null;
    private String robotConfig = null;
    //Global variables for maze generation
    private MazeFactory factory;
    private DefaultOrder order;

    /**
     * Instantiates activity for GeneratingActivity; sets up generation screen for user interaction
     * @param savedInstanceState as saved data of instance of activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generating_activity);

        //Log generating screen activation
        Log.v(TAG, "Maze generation started; user currently at generation screen");

        //Obtain maze generation settings from passed intent
        Intent titleIntent = getIntent();
        Bundle passedParameters = titleIntent.getExtras();
        int mazeComplexity = passedParameters.getInt("mazeComplexity");
        boolean includeRooms = passedParameters.getBoolean("includeRooms");
        String mazeAlgorithm = passedParameters.getString("mazeAlgorithm");
        boolean genNewMaze = passedParameters.getBoolean("genNew");
        Log.v(TAG, "Obtained complexity parameter: " + mazeComplexity);
        Log.v(TAG, "Obtained room inclusion parameter: " + includeRooms);
        Log.v(TAG, "Obtained algorithm parameter: " + mazeAlgorithm);
        Log.v(TAG, "Will generate new maze: " + genNewMaze);

        //Generate instance of each widget used on generation screen for adding functionality
        ProgressBar genProgressBar = findViewById(R.id.progressBar);
        genCompleteNotice = findViewById(R.id.genProgressDoneText);
        genIncompleteNotice = findViewById(R.id.genProgressNotDoneText);
        genCompleteContinue = findViewById(R.id.genProgressContinueText);
        genCompleteWarn = findViewById(R.id.genProgressWarnText);
        RadioButton robotDriverManual = findViewById(R.id.robotDriverManualRadio);
        RadioButton robotDriverWallfollower = findViewById(R.id.robotDriverWallfollowerRadio);
        RadioButton robotDriverWizard = findViewById(R.id.robotDriverWizardRadio);
        RadioButton robotConfigPremium = findViewById(R.id.robotConfigPremiumRadio);
        RadioButton robotConfigMediocre = findViewById(R.id.robotConfigMediocreRadio);
        RadioButton robotConfigSoso = findViewById(R.id.robotConfigSosoRadio);
        RadioButton robotConfigShaky = findViewById(R.id.robotConfigShakyRadio);

        //Begin generating maze from received settings
        generateMaze(mazeComplexity, mazeAlgorithm, includeRooms, genNewMaze);
        //Start up progress bar in conjunction with maze generation
        updateProgressBar(genProgressBar);

        //Set up listeners on each widget to detect and respond to user interactions
        onDriverInteraction(robotDriverManual);
        onDriverInteraction(robotDriverWallfollower);
        onDriverInteraction(robotDriverWizard);
        onConfigInteraction(robotConfigPremium);
        onConfigInteraction(robotConfigMediocre);
        onConfigInteraction(robotConfigSoso);
        onConfigInteraction(robotConfigShaky);
    }

    /**
     * Begin generation of a new maze based off given parameters
     * @param mazeComplexity as int on desired skillLevel for maze
     * @param mazeAlgorithm as String for desired algorithm to generate maze
     * @param includeRooms as boolean for whether maze is perfect
     * @param genNewMaze as boolean for if new random maze is generated or old one is used
     */
    private void generateMaze(int mazeComplexity, String mazeAlgorithm, boolean includeRooms, boolean genNewMaze) {
        //Log start of maze generation
        Log.v(TAG, "Starting maze generation");

        //Set up initial settings for maze generation from given parameters
        Order.Builder builder = Order.Builder.DFS;
        switch (mazeAlgorithm) {
            case "Prim":
                builder = Order.Builder.Prim;
                break;
            case "Boruvka":
                builder = Order.Builder.Boruvka;
                break;
        }
        boolean perfect = !includeRooms;
        //Instantiate shared preference reference for use in seed reading/writing
        SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //Default seed of 13 if in deterministic mode
        int seed = 13;
        //Want to determine if we should use old seed from previous generation
        //If intent signals new maze generation, then use new seed
        if (genNewMaze) {
            //If set to deterministic mode, seed will always be 13, else randomly generate one
            if (!IS_DETERMINISTIC) {
                Random random = new Random();
                seed = random.nextInt();
            }

            //Put newly generated seed into shared preference for use in maze revisiting
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt("seed", seed);
            editor.apply();
        }
        //Else, get old seed and set as seed for generation
        else {
            seed = sharedPrefs.getInt("seed", seed);
        }
        //Report used seed
        Log.v(TAG, "Using seed value: " + seed);

        //Set up maze factory and order for maze
        factory = new MazeFactory();
        order = new DefaultOrder(mazeComplexity, builder, perfect, seed);

        //Separate thread for maze generation
        Thread thread = new Thread(() -> {
            //Queue order for maze generation
            factory.order(order);
            //Wait until maze is generated, then set maze to singleton
            factory.waitTillDelivered();
            Maze maze = order.getMaze();
            MazeConfig.getInstance().setMaze(maze);

            //Log end of maze generation
            Log.v(TAG, "Finished maze generation");

            //Report that maze generation has finished
            isGenerationDone = true;
            //Check if settings have been selected so game can move into State Playing
            //NOTE: Running check on UI thread to avoid view hierarchy error
            runOnUiThread(this::checkFinishedGeneration);
        });
        //Start thread
        thread.start();
    }

    /**
     * Updates the progress bar in conjunction with maze generation
     * @param progressBar as instance of widget used for progress representation
     */
    protected void updateProgressBar(ProgressBar progressBar) {
        //Set default value of 0 for progress bar
        progressBar.setProgress(0);
        //Define thread task; will continually check maze generation progress every 200 ms
        //Continues until 100% reached
        Thread thread = new Thread(() -> {
            while (progressBar.getProgress() < 100) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Pull progress info from DefaultOrder instance sent to MazeFactory
                progressBar.setProgress(order.getProgress());
                //Logging for continuous updates
                Log.v(TAG, "Progress at " + progressBar.getProgress() + "%");
            }
        });
        //Start thread
        thread.start();
    }

    /**
     * Handles user interaction with radio buttons used for driver type selection
     * @param radioButton as instance of widget used for driver type selection
     */
    @SuppressLint("NonConstantResourceId")
    protected void onDriverInteraction(RadioButton radioButton) {
        //Set up listener on passed radio button
        radioButton.setOnClickListener(view -> {
            //Want to determine which button was selected
            //Obtain id of selected radio button and assign the global variable as such
            switch (radioButton.getId()) {
                //If user selects 'manual' setting, set global variable as such
                case R.id.robotDriverManualRadio:
                    driverType = "Manual";
                    break;
                //If 'wallfollower' selected, set as such
                case R.id.robotDriverWallfollowerRadio:
                    driverType = "Wallfollower";
                    break;
                //If 'wizard' selected, set as such
                case R.id.robotDriverWizardRadio:
                    driverType = "Wizard";
                    break;
            }
            //Log selection
            Log.v(TAG, "User has selected driver: " + driverType);
            //Toast.makeText(getApplicationContext(), "Driver set to " + driverType, Toast.LENGTH_LONG).show();
            //Check if maze generation has finished so game can move into State Playing
            checkFinishedGeneration();
        });
    }

    /**
     * Handles user interaction with radio buttons used for robot config selection
     * @param radioButton as instance of widget used for robot config selection
     */
    @SuppressLint("NonConstantResourceId")
    protected void onConfigInteraction(RadioButton radioButton) {
        //Set up listener on passed radio button
        radioButton.setOnClickListener(view -> {
            //Want to determine which button was selected
            //Obtain id of selected radio button and assign the global variable as such
            switch (radioButton.getId()) {
                //If user selects 'premium' config, set global variable as such
                case R.id.robotConfigPremiumRadio:
                    robotConfig = "Premium";
                    break;
                //If 'mediocre' selected, set as such
                case R.id.robotConfigMediocreRadio:
                    robotConfig = "Mediocre";
                    break;
                //If 'soso' selected, set as such
                case R.id.robotConfigSosoRadio:
                    robotConfig = "Soso";
                    break;
                //If 'shaky' selected, set as such
                case R.id.robotConfigShakyRadio:
                    robotConfig = "Shaky";
                    break;
            }
            //Log selection
            Log.v(TAG, "User has selected robot config: " + robotConfig);
            //Toast.makeText(getApplicationContext(), "Config set to " + robotConfig, Toast.LENGTH_LONG).show();
            //Check if maze generation has finished so game can move into State Playing
            checkFinishedGeneration();
        });
    }

    /**
     * Checks if generation has completed and if driver settings have been selected
     * If so, start up game using maze and settings and send user to State Playing screen
     */
    protected void checkFinishedGeneration() {
        //Check if maze generation has completed
        if (isGenerationDone) {
            //If so, check for driver setting selection
            Log.v(TAG, "Generation complete, checking robot settings");
            //Show text indicating generation has completed
            genCompleteNotice.setVisibility(View.VISIBLE);
            genIncompleteNotice.setVisibility(View.INVISIBLE);
            //If has not yet been selected, show prompt asking user to select
            if (driverType == null || robotConfig == null) {
                Log.v(TAG, "Settings not yet chosen, cannot continue");
                genCompleteWarn.setVisibility(View.VISIBLE);
            }
            //Else, start up game and send user to State Playing
            else {
                //Show prompt notifying user of screen change
                Log.v(TAG, "Settings chosen, starting game");
                genCompleteContinue.setVisibility(View.VISIBLE);
                genCompleteWarn.setVisibility(View.INVISIBLE);

                //LATER: Set up maze and game for use in State Playing

                //Declare intent object
                Intent playGame;
                Bundle passedParameters = new Bundle();
                passedParameters.putString("driverType", driverType);
                passedParameters.putString("robotConfig", robotConfig);
                //Assign intent to either PlayManuallyActivity or PlayAnimationActivity depending on driver type
                if (Objects.equals(driverType, "Manual")) {
                    playGame = new Intent(this, PlayManuallyActivity.class);
                    playGame.putExtras(passedParameters);
                }
                else {
                    playGame = new Intent(this, PlayAnimationActivity.class);
                    playGame.putExtras(passedParameters);
                }
                //Start up State Playing screen
                startActivity(playGame);
                //Close this activity
                finish();
            }
        }
        //If not finished, check if settings are selected
        else if (driverType != null && robotConfig != null) {
            Log.v(TAG, "Generation not complete, cannot continue");
            genIncompleteNotice.setVisibility(View.VISIBLE);
            genCompleteContinue.setVisibility(View.VISIBLE);
        }
        //If not, do nothing
        else {
            Log.v(TAG, "Generation not complete, cannot continue");
        }
    }
}