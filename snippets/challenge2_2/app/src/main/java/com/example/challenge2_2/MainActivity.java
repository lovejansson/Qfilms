package com.example.challenge2_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button buttonAdd;
    private int itemsCount = 0;
    public static final int TEXT_REQUEST = 1;
     private LinearLayout itemsLayout;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(!((TextView)itemsLayout.getChildAt(0)).getText().toString().startsWith("i")) {
            String[] itemsContent = new String[itemsLayout.getChildCount()];
            for (int i = 0; i < itemsLayout.getChildCount(); i++) {
                itemsContent[i] = ((TextView) itemsLayout.getChildAt(i)).getText().toString();
            }
            outState.putStringArray("items", itemsContent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAdd = (Button) findViewById(R.id.button_add);
        itemsLayout = findViewById(R.id.items);

        if (savedInstanceState != null) {
            String[] itemsss = savedInstanceState.getStringArray("items");
            for(int i = 0; i < itemsLayout.getChildCount()-1; i ++){
                ((TextView)itemsLayout.getChildAt(i)).setText(itemsss[i]);
            }
        }

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchItemActivity(view);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TEXT_REQUEST) {

            if (resultCode == RESULT_OK) {
                String newItem = data.getStringExtra(items.EXTRA_ITEM);
                TextView item = (TextView) itemsLayout.getChildAt(itemsCount);
                item.setText(newItem);
                itemsCount++;
            }
        }
    }

    public void launchItemActivity(View view){
        Intent intent = new Intent(this, items.class);
        startActivityForResult(intent, TEXT_REQUEST);
    }
}