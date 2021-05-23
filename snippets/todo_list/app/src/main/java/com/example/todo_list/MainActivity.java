package com.example.todo_list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private  ArrayList<String> todoList = new ArrayList<String>();
    public static final int TEXT_REQUEST = 1;
    private TodoAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new TodoAdapter(this, todoList);
        recyclerView = findViewById(R.id.todo_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ((FloatingActionButton)findViewById(R.id.fab_add_todo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivityAddTodo();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {
                addTodo(data.getStringExtra(AddTodoActivity.EXTRA_TODO_ADDED));
            }
        }
    }

    private void launchActivityAddTodo() {
        Intent intent = new Intent(this, AddTodoActivity.class);
        startActivityForResult(intent, TEXT_REQUEST);
    }


    private void addTodo(String content) {
        todoList.add(content);
        recyclerView.getAdapter().notifyItemInserted(todoList.size() - 1);
        recyclerView.smoothScrollToPosition(todoList.size() - 1);

        for(int i = 0; i < todoList.size(); i++ ){
            Log.d("MAINACTIVITY", todoList.get(i));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String selected_item = item.getTitle().toString();
        Toast.makeText(getApplicationContext(), selected_item, Toast.LENGTH_SHORT).show();
        return true;
    }
}