package com.akzubarev.homedoctor.ui.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.adapters.RemindTimeAdapter;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.MedicationStats;
import com.akzubarev.homedoctor.databinding.FragmentMedicationBinding;

import java.util.ArrayList;
import java.util.Date;

public class MedicationFragment extends Fragment implements View.OnClickListener {

    private FragmentMedicationBinding binding;
    private final ArrayList<Date> remindTimes = new ArrayList<>();
    DataHandler dataHandler;
    Medication medication;
    MedicationStats medicationStats;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMedicationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        boolean create_mode = false;
        if (savedInstanceState != null)
            create_mode = savedInstanceState.getBoolean("create", true);

        if (create_mode) {
//            Button b = findViewById(R.id.id);
        } else {
            Bundle bundle = this.getArguments();
            if (bundle != null) {
                String medicationName = bundle.getString("Medication");
                dataHandler = DataHandler.getInstance(getContext());
                dataHandler.getMedication(medicationName, this::getMedicationStat);
            }
        }
        configureSpinner(binding.durationSpinner, R.array.duration_dropdown, (int choice) -> {
            String[] options = getResources().getStringArray(R.array.duration_dropdown);
            String duration = options[choice];
//            DataReader.SaveInt(goal_minutes, "", getContext());
        }, 0); // DataReader.GetInt(DataReader.GOAL, getContext()) / 5 - 1);

        for (ToggleButton toggleButton : new ToggleButton[]{binding.daysMon, binding.daysTue,
                binding.daysWed, binding.daysThu, binding.daysFri, binding.daysSat, binding.daysSun}) {
            toggleButton.setOnClickListener((View v) -> {
                int color = getResources().getColor(R.color.color3);
                int textColor = getResources().getColor(R.color.main);
                String tag = String.valueOf(toggleButton.getTag());
                //TODO: add tag handling
                if (toggleButton.isChecked()) {
                    color = getResources().getColor(R.color.additional);
                    textColor = getResources().getColor(R.color.color1);
                }
                toggleButton.setBackgroundColor(color);
                toggleButton.setTextColor(textColor);
            });
        }

        EditText frequencyEditText = binding.frequencyEditText;
        frequencyEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    changeRemindersNum();
                    return true;
                }
                return false;
            }
        });
        RecyclerView remindList = binding.remindList;
        remindList.setHasFixedSize(false);
        remindList.addItemDecoration(new DividerItemDecoration(
                remindList.getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager remindersLayoutManager = new LinearLayoutManager(getContext());
        RemindTimeAdapter remindersAdapter = new RemindTimeAdapter(remindTimes, getContext());
        remindList.setLayoutManager(remindersLayoutManager);
        remindList.setAdapter(remindersAdapter);
        changeRemindersNum();


        binding.saveButton.setOnClickListener(this);
        return view;
    }

    private void getMedicationStat(Medication medication) {
        this.medication = medication;
        String medicationStatID = medication.getMedicationStatsID();
        dataHandler.getMedicationStat(medicationStatID, this::fill);
    }


    private void fill(MedicationStats medicationStats) {
        this.medicationStats = medicationStats;

        binding.nameEditText.setText(medication.getName());
        binding.frequencyEditText.setText(Integer.toString(medication.getDailyFrequency()));
        String[] course = medicationStats.getCourceLength().split(" ");
        binding.durationEditText.setText(course[0]);
        int index = 0;
        switch (course[1]) {
            case "дней":
                index = 0;
                break;
            case "недель":
                index = 1;
                break;
            case "месяцев":
                index = 2;
                break;
        }
        binding.durationSpinner.setSelection(index);
    }

    private void changeRemindersNum() {
        EditText frequencyEditText = binding.frequencyEditText;
        int consTimes = Integer.parseInt(frequencyEditText.getText().toString());
        if (consTimes > remindTimes.size())
            while (consTimes > remindTimes.size())
                remindTimes.add(new Date());
        else
            while (consTimes < remindTimes.size())
                remindTimes.remove(remindTimes.size() - 1);
    }


    public interface SpinnerCallback {
        void onCallback(int choice);
    }

    private void configureSpinner(Spinner spinner, int arrayID, SpinnerCallback callback, int choice_index) {
        String[] state = getResources().getStringArray(arrayID);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
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
//        int offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -80, getResources().getDisplayMetrics());
//        spinner.setDropDownHorizontalOffset(offset);

    }

    private void saveMedication() {
        String name = binding.nameEditText.getText().toString();
        String courceLength = String.format("%s %s",
                binding.durationEditText.getText().toString(),
                binding.durationSpinner.getSelectedItem().toString());
        int dailyFrequency = Integer.parseInt(binding.frequencyEditText.getText().toString());

//        Medication med = new Medication(name, courceLength, dailyFrequency);
        DataHandler dataBaseHandler = DataHandler.getInstance(getContext());
//        dataBaseHandler.addMedication(med);
        onSuccesfulSave();
    }

    private void onSuccesfulSave() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
    }

    @Override
    public void onClick(View view) {
        saveMedication();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}