package com.example.challenge2_1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private int[] buttonIds = new int[]{R.id.btn_one, R.id.btn_two, R.id.btn_three};
    private HashMap<String, String> songLyrics = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for(int i = 0; i < buttonIds.length; i++){
            Button b = (Button) findViewById(buttonIds[i]);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchSecondActivity(view);
                }
            });
        }

        findViewById(R.id.info_btn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showInfo();
            }
        });

        findViewById(R.id.date_btn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });


        songLyrics.put(getString(R.string.title_one), getString(R.string.lyrics_one));
        songLyrics.put(getString(R.string.title_two), getString(R.string.lyrics_two));
        songLyrics.put(getString(R.string.title_three), getString(R.string.lyrics_three));

    }

    private void showInfo() {
        AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(MainActivity.this);
        myAlertBuilder.setTitle(R.string.info_title);
        myAlertBuilder.setMessage(R.string.info_content);
        myAlertBuilder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        myAlertBuilder.show();
    }


    public void launchSecondActivity(View v) {
        Intent intent = new Intent(this, SecondActivity.class);

        String title = (((Button) v).getText()).toString();

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("lyrics", songLyrics.get(title));

        intent.putExtras(bundle);

        startActivity(intent);
        }

    public void processDatePickerResult(int year, int month, int day) {
        String month_string = Integer.toString(month+1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        String dateMessage = (year_string + "-" + month_string +
                "-" + day_string);

        Toast.makeText(this, "Date: " + dateMessage + " has concerts in Stockholm and Madrid",
                Toast.LENGTH_SHORT).show();
    }

    public void showDatePicker() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(),"datePicker");
    }
    }

