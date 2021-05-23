package com.example.todo_list;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddTodoActivity extends AppCompatActivity {
    public static final String EXTRA_TODO_ADDED =
            "com.example.android.twoactivities.extra.REPLY";
    private EditText todoAdded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        todoAdded = (EditText)findViewById(R.id.edit_add_todo);
        ((Button)findViewById(R.id.button_add_todo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTodo();
            }
        });
    }

    private void addTodo() {
        if(todoAdded.getText().toString().length() == 0){
            todoAdded.setError(getString(R.string.add_todo_error_msg));
        }
        else {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_TODO_ADDED, todoAdded.getText().toString());
            todoAdded.getText().clear();
            setResult( RESULT_OK ,intent);
            finish();
        }
    }
}