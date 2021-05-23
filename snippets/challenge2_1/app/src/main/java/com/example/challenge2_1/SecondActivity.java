package com.example.challenge2_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {


    private TextView songLyrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            String title = bundle.getString("title");
            String lyrics = bundle.getString("lyrics");
            setTitle(title);
            setContentView(R.layout.activity_second);
            songLyrics = findViewById(R.id.text_lyrics);


            songLyrics.setText(lyrics);
        }
        else {
            setTitle("ERROR");
            setContentView(R.layout.activity_second);
            songLyrics.setText(R.string.lyrics_error);
        }

        TextView lyrics_text = findViewById(R.id.text_lyrics);
        registerForContextMenu(lyrics_text);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int id = item.getItemId();


            CharSequence itemText = item.getTitle();
            Toast.makeText(getApplicationContext(), itemText, Toast.LENGTH_SHORT).show();



        return super.onOptionsItemSelected(item);
    }
}