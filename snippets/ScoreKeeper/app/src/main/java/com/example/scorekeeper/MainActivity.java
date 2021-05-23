package com.example.scorekeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
     int score1;
     int score2;
    static final String STATE_SCORE_1 = "Team 1 Score";
    static final String STATE_SCORE_2 = "Team 2 Score";

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(STATE_SCORE_1, score1);
        outState.putInt(STATE_SCORE_2, score2);
        super.onSaveInstanceState(outState);
    }

    private TextView scoreOne;
    private TextView scoreTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreOne = findViewById(R.id.score_one);
        scoreTwo = findViewById(R.id.score_two);

        ((ImageButton) findViewById(R.id.btn_minus_one)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseScore(1);
            }
        });
        ((ImageButton) findViewById(R.id.btn_minus_two)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseScore(2);
            }
        });

        ((ImageButton) findViewById(R.id.btn_plus_one)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseScore(1);
            }
        });
        ((ImageButton) findViewById(R.id.btn_plus_two)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              increaseScore(2);
            }
        });

        if(savedInstanceState != null){
            score1 = savedInstanceState.getInt(STATE_SCORE_1);
            score2 = savedInstanceState.getInt(STATE_SCORE_2);

            scoreOne.setText(String.valueOf(score1));
            scoreTwo.setText(String.valueOf(score2));
        }
    }

    private void decreaseScore( int teamId) {

        if(teamId == 1){
            if(score1 != 0) {
                score1--;
                scoreOne.setText(String.valueOf(score1));
            }
        }
        else if(teamId == 2){
            if(score2 != 0) {
                score2--;
                scoreTwo.setText(String.valueOf(score2));
            }
        }
    }

    private void increaseScore(int teamId) {
        if(teamId == 1){
            score1++;
            scoreOne.setText(String.valueOf(score1));
        }
        else if(teamId == 2){
            score2++;
            scoreTwo.setText(String.valueOf(score2));
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.dark_mode){
            int darkMode = AppCompatDelegate.getDefaultNightMode();
            if(darkMode == AppCompatDelegate.MODE_NIGHT_YES){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

            recreate();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_men, menu);
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if(nightMode == AppCompatDelegate.MODE_NIGHT_YES){
            menu.findItem(R.id.dark_mode).setTitle(R.string.light_mode);
        } else{
            menu.findItem(R.id.dark_mode).setTitle(R.string.dark_mode);
        }
        return true;

    }
}