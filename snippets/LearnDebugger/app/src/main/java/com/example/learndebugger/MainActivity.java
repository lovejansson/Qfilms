package com.example.learndebugger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    EditText et_number_one;
    EditText et_number_two;
    TextView tv_answer;
    String selected_op = "Addition";
    Calculator calculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_number_one = findViewById(R.id.et_number_one);
        et_number_two = findViewById(R.id.et_number_two);
        tv_answer = findViewById(R.id.tv_answer);
        calculator  = new Calculator();

        Spinner spinnerOperations = findViewById(R.id.spinner_operations);

        if(spinnerOperations != null){
            spinnerOperations.setOnItemSelectedListener(this);

        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_operations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        if(spinnerOperations != null){
            spinnerOperations.setAdapter(adapter);
        }


        findViewById(R.id.btn_calculate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String number_one = et_number_one.getText().toString();
                String number_two = et_number_two.getText().toString();

                if(validInput(number_one, number_two)) {
                    Integer one = Integer.parseInt(number_one);
                    Integer two = Integer.parseInt(number_two);

                    Integer answer = calculator.calculateAnswer(one, two, selected_op);


                    tv_answer.setText(answer.toString());

                }
            }
        });
    }

    private Boolean validInput(String number_one, String number_two) {

        if(number_one.isEmpty()){
            et_number_one.setError(getString(R.string.et_error_msg));
            return false;
        }
        else {
            if (number_two.isEmpty()) {
                et_number_two.setError(getString(R.string.et_error_msg));
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selected_op = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}