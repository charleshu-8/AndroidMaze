package edu.wm.cs.cs301.amazebycharleshu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
//import android.widget.Toast;

import edu.wm.cs.cs301.amazebycharleshu.R;

public class AMazeActivity extends AppCompatActivity {
    //Constant variables
    private final String TAG = "StateTitleActivity";
    private final int DEFAULT_COMPLEXITY = 0;
    //Global variables used to track maze parameters for later generation
    private int mazeComplexity = DEFAULT_COMPLEXITY;
    private Boolean includeRooms = true;
    private String mazeAlgorithm;

    /**
     * Instantiates activity for AMazeActivity; sets up title screen for user interaction
     * @param savedInstanceState as saved data of instance of activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set up title screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_maze_activity);

        //Log title screen activation
        Log.v(TAG, "Application started; user currently at title screen");

        //Generate instance of each widget used on title screen for adding functionality
        SeekBar complexitySeekBar = findViewById(R.id.genComplexitySeekbar);
        RadioButton roomsYesRadio = findViewById(R.id.genRoomsYesRadio);
        RadioButton roomsNoRadio = findViewById(R.id.genRoomsNoRadio);
        Spinner algorithmSpinner = findViewById(R.id.genAlgorithmSpinner);
        Button exploreButton = findViewById(R.id.genExploreButton);
        Button revisitButton = findViewById(R.id.genRevisitButton);

        //We want complexity seekbar to default to value 0
        complexitySeekBar.setProgress(DEFAULT_COMPLEXITY);
        //For room inclusion selection, we want 'Yes' option to be default
        roomsYesRadio.setChecked(true);
        //Set default maze algorithm to use; should be DFS
        mazeAlgorithm = getResources().getStringArray(R.array.maze_gen_algorithm_list)[0];

        //Set up listeners on each widget to detect and respond to user interactions
        onSeekBarInteraction(complexitySeekBar);
        onRadioButtonInteraction(roomsYesRadio);
        onRadioButtonInteraction(roomsNoRadio);
        onSpinnerInteraction(algorithmSpinner);
        onButtonInteraction(exploreButton);
        onButtonInteraction(revisitButton);
    }

    /**
     * Handles user interaction with seekbar widget and set complexity variable to selected value
     * @param seekBar as instance of widget used for complexity selection
     */
    protected void onSeekBarInteraction(SeekBar seekBar) {
        //Set up listener on seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //After user changes complexity, change the corresponding global variable
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mazeComplexity = i;
            }

            //Log when the user first begins interacting with the seekbar
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.v(TAG, "User interacting with complexity settings");
            }

            //Log when the user stops interacting with the seekbar and selected complexity
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.v(TAG, "User has selected complexity setting: " + mazeComplexity);
                //Toast.makeText(getApplicationContext(), "Complexity set to " + mazeComplexity, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Handles user interaction with radio buttons used to determine if rooms are to be generated
     * @param radioButton as instance of widget used for room inclusion selection
     */
    @SuppressLint("NonConstantResourceId")
    protected void onRadioButtonInteraction(RadioButton radioButton) {
        //Set up listener on passed radio button
        radioButton.setOnClickListener(view -> {
            //Want to determine which button was selected
            //Obtain id of selected radio button and assign the global variable as such
            switch(radioButton.getId()) {
                //If we detect if the 'yes' option was selected
                case R.id.genRoomsYesRadio:
                    includeRooms = true;
                    break;
                //If we detect if the 'no' option was selected
                case R.id.genRoomsNoRadio:
                    includeRooms = false;
                    break;
            }
            //Log selection
            Log.v(TAG, "User has selected room inclusion: " + includeRooms);
            //Toast.makeText(getApplicationContext(), "Include rooms: " + includeRooms, Toast.LENGTH_LONG).show();
        });
    }

    /**
     * Handles user interaction with spinner widget used for maze algorithm selection
     * @param spinner as instance of widget used for maze algorithm selection
     */
    protected void onSpinnerInteraction(Spinner spinner) {
        //Set up listener on passed spinner widget
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //When user selects a value from the spinner, change the global variable to reflect
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mazeAlgorithm = (String) adapterView.getItemAtPosition(i);
                //Log such the selected maze algorithm
                Log.v(TAG, "User has selected maze algorithm: " + mazeAlgorithm);
                //Toast.makeText(getApplicationContext(), "Algorithm set to " + mazeAlgorithm, Toast.LENGTH_LONG).show();
            }

            //Logs if user doesn't make selection
            //Shouldn't happen, so instance of log indicates unexpected behavior
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.v(TAG, "User has cancelled maze algorithm selection");
            }
        });
    }

    /**
     * Handles user interaction with button widgets used for initiation of maze generation
     * @param button as instance of widget used for maze generation initiation
     */
    @SuppressLint("NonConstantResourceId")
    protected void onButtonInteraction(Button button) {
        //Set up listener on passed button widget
        button.setOnClickListener(view -> {
            //Create intent for GeneratingActivity class (State Generating)
            Intent generateMaze = new Intent(this, GeneratingActivity.class);
            //Create bundle with passed maze settings
            Bundle passedParameters = new Bundle();

            //Instantiate app preference object and editor for persistent reading/writing of maze settings
            SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();

            //Need to identify which button (of Explore and Revisit) was selected for next step
            switch(button.getId()) {
                //If Explore button was selected, pass the inputted maze parameters to the intent
                //Then start up maze generation activity where user can then set up the robot
                case R.id.genExploreButton:
                    //Pass selected parameters to bundle and pass bundle to intent
                    passedParameters.putInt("mazeComplexity", mazeComplexity);
                    passedParameters.putBoolean("includeRooms", includeRooms);
                    passedParameters.putString("mazeAlgorithm", mazeAlgorithm);
                    passedParameters.putBoolean("genNew", true);
                    generateMaze.putExtras(passedParameters);

                    //Pass selected parameters to shared preference for later reference if maze is revisited
                    editor.putInt("mazeComplexity", mazeComplexity);
                    editor.putBoolean("includeRooms", includeRooms);
                    editor.putString("mazeAlgorithm", mazeAlgorithm);
                    editor.apply();

                    //Log maze generation activation
                    Log.v(TAG, "User has begun maze generation");
                    //Toast.makeText(getApplicationContext(), "Maze generation started", Toast.LENGTH_LONG).show();
                    startActivity(generateMaze);
                    break;
                //If Revisit was selected, use previously generated maze's settings and ignore current settings
                //Then start intent and pass to next activity
                case R.id.genRevisitButton:
                    //Check if shared preference exists
                    //If does (indicated by containing selected parameters), pull previous settings and pass as bundle
                    if (sharedPrefs.contains("mazeComplexity")) {
                        passedParameters.putInt("mazeComplexity", sharedPrefs.getInt("mazeComplexity", mazeComplexity));
                        passedParameters.putBoolean("includeRooms", sharedPrefs.getBoolean("includeRooms", includeRooms));
                        passedParameters.putString("mazeAlgorithm", sharedPrefs.getString("mazeAlgorithm", mazeAlgorithm));
                        passedParameters.putBoolean("genNew", false);
                        //Report previous maze found
                        Log.v(TAG, "Previous maze found, using previous parameters");
                    }
                    //If does not exist, use currently selected settings and pass to bundle and shared preference
                    else {
                        //Pass into bundle for immediate use in maze generation
                        passedParameters.putInt("mazeComplexity", mazeComplexity);
                        passedParameters.putBoolean("includeRooms", includeRooms);
                        passedParameters.putString("mazeAlgorithm", mazeAlgorithm);
                        passedParameters.putBoolean("genNew", true);

                        //Pass into shared preference and treat as new instance of maze generation
                        //Next revisit will view this cycle's generated maze as the previous maze to use
                        editor.putInt("mazeComplexity", mazeComplexity);
                        editor.putBoolean("includeRooms", includeRooms);
                        editor.putString("mazeAlgorithm", mazeAlgorithm);
                        editor.apply();

                        //Report previous maze not found
                        Log.v(TAG, "No previous maze found, defaulting to currently selected parameters");
                    }
                    //Pass bundle to intent
                    generateMaze.putExtras(passedParameters);
                    //Log usage of previously generated maze
                    Log.v(TAG, "User revisited previously generated maze");
                    //Toast.makeText(getApplicationContext(), "Reusing previous maze", Toast.LENGTH_LONG).show();
                    startActivity(generateMaze);
                    break;
            }
        });
    }
}
