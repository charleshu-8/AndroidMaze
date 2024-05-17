package edu.wm.cs.cs301.amazebycharleshu.gui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.amazebycharleshu.R;

public class WinningActivity extends AppCompatActivity {
    //Constant variables
    private final String TAG = "StateWinningActivity";
    //Global variables used to track player stats
    private int distanceTravelled;
    private int distanceShortest;
    private float energyConsumed;

    /**
     * Instantiates activity for WinningActivity; sets up win screen for user interaction
     * @param savedInstanceState as saved data of instance of activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.winning_activity);

        //Log winning screen activation
        Log.v(TAG, "Win screen displayed");

        //Receive and store passed game state info from State Playing
        Intent playIntent = getIntent();
        Bundle passedParameters = playIntent.getExtras();
        boolean isManual = passedParameters.getBoolean("isManual");
        distanceTravelled = passedParameters.getInt("distanceTravelled");
        distanceShortest = passedParameters.getInt("distanceShortest");
        Log.v(TAG, "Obtained manual playing stat: " + isManual);
        Log.v(TAG, "Obtained distance travelled stat: " + distanceTravelled);
        Log.v(TAG, "Obtained shortest distance stat: " + distanceShortest);
        //If game was in auto state, also pull energy consumed parameter
        if (!isManual) {
            energyConsumed = passedParameters.getFloat("energyConsumed");
            Log.v(TAG, "Obtained energy consumption stat: " + energyConsumed);
        }

        //Generate instance of TextView widgets for UI manipulation
        TextView playerTitle = findViewById(R.id.winTitlePlayer);
        TextView driverTitle = findViewById(R.id.winTitleDriver);
        TextView playerText = findViewById(R.id.winTextPlayer);
        TextView driverText = findViewById(R.id.winTextDriver);
        TextView statsTravelled = findViewById(R.id.winStatsTravelled);
        TextView statsShortest = findViewById(R.id.winStatsShortest);
        TextView statsEnergyText = findViewById(R.id.winStatsEnergyText);
        TextView statsEnergy = findViewById(R.id.winStatsEnergy);

        //Check if game was played in manual or auto mode to determine the type of win screen to be displayed
        if (isManual) {
            updateForManual(driverTitle, driverText, statsEnergyText, statsEnergy);
        }
        else {
            updateForAuto(playerTitle, playerText, statsEnergy);
        }
        updateSharedStats(statsTravelled, statsShortest);
    }

    /**
     * Updates the win screen for if the player won manually
     * @param driverTitle as instance of widget for displaying driver win title
     * @param driverText as instance of widget for displaying driver win text
     * @param statsEnergyText as instance of widget for displaying text describing energy consumption
     * @param statsEnergy as instance of widget for displaying energy consumption
     */
    protected void updateForManual(TextView driverTitle, TextView driverText, TextView statsEnergyText, TextView statsEnergy) {
        //Hide elements used in auto driver win
        Log.v(TAG, "Displaying win screen for manual play");
        driverTitle.setVisibility(View.INVISIBLE);
        driverText.setVisibility(View.INVISIBLE);
        statsEnergyText.setVisibility(View.INVISIBLE);
        statsEnergy.setVisibility(View.INVISIBLE);
    }

    /**
     * Updates the win screen for if the automatic driver won
     * @param playerTitle as instance of widget for displaying player win title
     * @param playerText as instance of widget for displaying player win text
     * @param statsEnergy as instance of widget for displaying energy consumption
     */
    protected void updateForAuto(TextView playerTitle, TextView playerText, TextView statsEnergy) {
        //Hide elements used in manual driver win
        Log.v(TAG, "Displaying win screen for auto play");
        playerTitle.setVisibility(View.INVISIBLE);
        playerText.setVisibility(View.INVISIBLE);
        //Show energy consumed by driver
        statsEnergy.setText(String.valueOf(energyConsumed));
    }

    /**
     * Updates the distance travelled and shortest path possible stats on screen
     * @param statsTravelled as instance of widget for displaying total distance travelled
     * @param statsShortest as instance of widget for displaying shortest possible distance
     */
    protected void updateSharedStats(TextView statsTravelled, TextView statsShortest) {
        statsTravelled.setText(String.valueOf(distanceTravelled));
        statsShortest.setText(String.valueOf(distanceShortest));
    }
}