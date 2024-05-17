package edu.wm.cs.cs301.amazebycharleshu.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.wm.cs.cs301.amazebycharleshu.R;

public class LosingActivity extends AppCompatActivity {
    //Constant variables
    private int distanceTravelled;
    private int distanceShortest;
    private float energyConsumed;
    private String crashState;

    /**
     * Instantiates activity for LosingActivity; sets up lose screen for user interaction
     * @param savedInstanceState as saved data of instance of activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.losing_activity);

        //Log losing screen activation
        String TAG = "StateWinningActivity";
        Log.v(TAG, "Lose screen displayed");

        //Receive and store passed game state info from State Playing
        Intent playIntent = getIntent();
        Bundle passedParameters = playIntent.getExtras();
        distanceTravelled = passedParameters.getInt("distanceTravelled");
        distanceShortest = passedParameters.getInt("distanceShortest");
        energyConsumed = passedParameters.getFloat("energyConsumed");
        crashState = passedParameters.getString("crashState");
        Log.v(TAG, "Obtained distance travelled stat: " + distanceTravelled);
        Log.v(TAG, "Obtained shortest distance stat: " + distanceShortest);
        Log.v(TAG, "Obtained energy consumption stat: " + energyConsumed);
        Log.v(TAG, "Obtained crash state stat: " + crashState);

        //Generate instance of widgets for UI manipulation
        TextView statsTravelled = findViewById(R.id.loseStatsTravelled);
        TextView statsShortest = findViewById(R.id.loseStatsShortest);
        TextView statsEnergy = findViewById(R.id.loseStatsEnergy);
        TextView crashedText = findViewById(R.id.crashedText);
        ImageView crashedImg = findViewById(R.id.crashedImage);
        TextView noEnergyText = findViewById(R.id.noEnergyText);
        ImageView noEnergyImg = findViewById(R.id.noEnergyImage);

        //Update screen to account for retrieved info from State Playing
        updateCause(noEnergyText, noEnergyImg, crashedText, crashedImg);
        updateStats(statsTravelled, statsShortest, statsEnergy);
    }

    /**
     * Updates cause of driver loss according to retrieved stats
     * @param noEnergyText as instance of widget for displaying no energy cause text
     * @param noEnergyImg as instance of widget for displaying no energy cause image
     * @param crashedText as instance of widget for displaying crash cause text
     * @param crashedImg as instance of widget for displaying crash cause image
     */
    protected void updateCause(TextView noEnergyText, ImageView noEnergyImg, TextView crashedText, ImageView crashedImg) {
        //If driver crashed, only show crashed cause
        if (crashState.equals("Crashed")) {
            noEnergyText.setVisibility(View.INVISIBLE);
            noEnergyImg.setVisibility(View.INVISIBLE);
        }
        //Else only show no energy cause
        else {
            crashedText.setVisibility(View.INVISIBLE);
            crashedImg.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Updates reported stats according to retrieved info from State Playing
     * @param statsTravelled as instance of widget for displaying total distance travelled
     * @param statsShortest as instance of widget for displaying shortest possible distance
     * @param statsEnergy as instance of widget for displaying total energy consumed
     */
    protected void updateStats(TextView statsTravelled, TextView statsShortest, TextView statsEnergy) {
        statsTravelled.setText(String.valueOf(distanceTravelled));
        statsShortest.setText(String.valueOf(distanceShortest));
        statsEnergy.setText(String.valueOf(energyConsumed));
    }
}