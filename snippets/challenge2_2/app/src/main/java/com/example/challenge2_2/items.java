package com.example.challenge2_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class items extends AppCompatActivity {

    int[] items = {R.id.button_rice, R.id.button_pasta, R.id.button_sausage, R.id.button_tomatoes, R.id.button_chocolate };
    public static final String EXTRA_ITEM =
            "com.example.android.two_activities.extra.REPLY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        for(int i = 0; i < items.length; i++){

            Button b = findViewById(items[i]);
            b.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    backToMain(view);
                }
            });


        }
    }

    public void backToMain(View view){
        String item = ((Button) view).getText().toString();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ITEM, item);
        setResult(RESULT_OK, intent);

        finish();
    }
}