package com.example.sports;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        TextView sportTitle = findViewById(R.id.sport_title_detail);
        ImageView sportImage = findViewById(R.id.sports_image_detail);
        String title = intent.getStringExtra("title");
        sportTitle.setText(title);

        Glide.with(this).load(intent.getIntExtra("image",0))
                .into(sportImage);
    }
}