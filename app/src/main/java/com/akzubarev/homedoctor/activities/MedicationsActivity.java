package com.akzubarev.homedoctor.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.DataReader;

public class MedicationsActivity extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Intent intent = getIntent();
        boolean create_mode = intent.getBooleanExtra("create", true);

        if (create_mode) {
            return;
//            Button b = findViewById(R.id.id);
        } else {
            EditText emailEditText = findViewById(R.id.emailEditText);
            EditText passwordEditText = findViewById(R.id.passwordEditText);
            EditText repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);
            TextView toggleLoginSignUpTextView = findViewById(R.id.toggleLoginSignUpTextView);
            Button loginSignUpButton = findViewById(R.id.loginSignUpButton);
        }
        configureSpinner(R.id.layout_spinner, R.array.layout_dropdown, (int choice) -> {
            int[] options = new int[]{5, 10, 15};
            int goal_minutes = options[choice];
            DataReader.SaveInt(goal_minutes, DataReader.GOAL, context);
        }, DataReader.GetInt(DataReader.GOAL, context) / 5 - 1);

        context = this;
    }

    public interface SpinnerCallback {
        void onCallback(int choice);
    }

    private void configureSpinner(int spinnerId, int arrayID, SpinnerCallback callback, int choice_index) {
        Spinner spinner = findViewById(spinnerId);
        String[] state = getResources().getStringArray(arrayID);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.spinner_row, R.id.spinner_row_text, state);
        spinner.setGravity(Gravity.END);
        adapter.setDropDownViewResource(R.layout.spinner_row_unfolded);

        spinner.setAdapter(adapter);
        spinner.setSelection(choice_index);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected,
                                       int selectedItemPosition,
                                       long selectedId) {
                callback.onCallback(selectedItemPosition);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        int offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -80, getResources().getDisplayMetrics());
        spinner.setDropDownHorizontalOffset(offset);

    }
}