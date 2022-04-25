package com.akzubarev.homedoctor.ui.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.MedicationStats;
import com.akzubarev.homedoctor.databinding.FragmentMedicationBinding;
import com.akzubarev.homedoctor.ui.notifications.NotificationHelper;

import java.util.ArrayList;
import java.util.Date;

public class MedicationFragment extends Fragment implements View.OnClickListener {
    String TAG = "MedicationFragment";
    private FragmentMedicationBinding binding;
    private final ArrayList<Date> remindTimes = new ArrayList<>();
    DataHandler dataHandler;
    String medicationID;
    Mode mode = Mode.view;

    enum Mode {view, create, edit, add}

    Medication medication;
    MedicationStats medicationStats;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMedicationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        switchMode(Mode.view);
        Bundle bundle = this.getArguments();
        dataHandler = DataHandler.getInstance(getContext());
        if (bundle != null) {
            String medicationStatID = bundle.getString("MedicationStat");
            medicationID = bundle.getString("Medication");
            boolean add = bundle.getBoolean("Add");
            dataHandler.getMedicationStat(medicationStatID, this::fillStat);
            if (add)
                switchMode(Mode.add);
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
        frequencyEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                changeRemindersNum();
                return true;
            }
            return false;
        });
        RecyclerView remindList = binding.remindList;
        remindList.setHasFixedSize(false);
        remindList.addItemDecoration(new DividerItemDecoration(
                remindList.getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager remindersLayoutManager = new LinearLayoutManager(getContext());
//        RemindTimeAdapter remindersAdapter = new RemindTimeAdapter(remindTimes, getContext());
        remindList.setLayoutManager(remindersLayoutManager);
//        remindList.setAdapter(remindersAdapter);
        changeRemindersNum();


        binding.saveButton.setOnClickListener(this);
        binding.cancelButton.setOnClickListener(this);
        binding.cancelButton.setVisibility(View.GONE);
        return view;
    }

    private void refill() {
//        fillStat(medicationStats);
        fillMedication(medication);
    }

    private void fillStat(MedicationStats medicationStats) {
        this.medicationStats = medicationStats;
        binding.nameEditText.setText(medicationStats.getName());
        binding.frequencyEditText.setText(Integer.toString(medicationStats.getDailyFrequency()));
        String[] course = medicationStats.getCourseLength().split(" ");
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
        changeRemindersNum();
        if (medicationID != null)
            dataHandler.getMedication(medicationID, this::fillMedication);
        else if (mode != Mode.add)
            binding.medicationLayout.setVisibility(View.GONE);

        switchMode(mode);
    }

    private void fillMedication(Medication medication) {
        this.medication = medication;
        binding.medicationLayout.setVisibility(View.VISIBLE);
        binding.frequencyEditText.setText(String.valueOf(medication.getDailyFrequency()));
        binding.tabletsEditText.setText(String.valueOf(medication.getAmount()));
        binding.expiryDateEditText.setText(medication.getExpiry_date());
        changeRemindersNum();

        switchMode(mode);
    }


    private void changeRemindersNum() {
//        EditText frequencyEditText = binding.frequencyEditText;
//        int consTimes = Integer.parseInt(frequencyEditText.getText().toString());
//        if (consTimes > remindTimes.size())
//            while (consTimes > remindTimes.size())
//                remindTimes.add(new Date());
//        else
//            while (consTimes < remindTimes.size())
//                remindTimes.remove(remindTimes.size() - 1);
    }


    private void switchMode(Mode mode) {
        this.mode = mode;
        View[] statViews = new View[]{binding.nameEditText, binding.frequencyEditText,
                binding.durationEditText, binding.durationSpinner,
                binding.packEditText, binding.usageEditText
        };
        View[] medViews = new View[]{binding.tabletsEditText, binding.expiryDateEditText};

        switch (mode) {
            case add:
                binding.medicationLayout.setVisibility(View.VISIBLE);
                binding.saveButton.setText("Добавить");
                for (View v : medViews) {
                    ((EditText) v).setText("");
                    v.setEnabled(true);
                }
                break;
            case view:
                for (View v : statViews)
                    v.setEnabled(false);
                for (View v : medViews)
                    v.setEnabled(false);
                break;
            case create:
                for (View v : statViews)
                    v.setEnabled(true);
                binding.medicationLayout.setVisibility(View.GONE);
                break;
            case edit:
                for (View v : statViews)
                    v.setEnabled(false);
                for (View v : medViews)
                    v.setEnabled(true);
                break;
        }
        if (medicationID == null && mode != Mode.add)
            binding.saveButton.setVisibility(View.GONE);
        else
            binding.saveButton.setVisibility(View.VISIBLE);
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
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                callback.onCallback(selectedItemPosition);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
//        int offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -80, getResources().getDisplayMetrics());
//        spinner.setDropDownHorizontalOffset(offset);

    }

    private void saveMedication() {
        Medication medToSave;
        if (medication != null)
            medToSave = medication;
        else
            medToSave = new Medication();

        medToSave.setName(binding.nameEditText.getText().toString());
        medToSave.setDailyFrequency(Integer.parseInt(binding.frequencyEditText.getText().toString()));
        medToSave.setExpiry_date(binding.expiryDateEditText.getText().toString());
        medToSave.setMedicationStatsID(medicationStats.getDBID());

//        String courceLength = String.format("%s %s",
//                binding.durationEditText.getText().toString(),
//                binding.durationSpinner.getSelectedItem().toString());

        dataHandler.saveMedication(medToSave);
        onSuccessfulSave();
    }

    private void onSuccessfulSave() {
//        NavController navController = NavHostFragment.findNavController(this);
//        navController.popBackStack();
        binding.saveButton.setText("Редактировать");
        binding.cancelButton.setVisibility(View.GONE);
        switchMode(Mode.view);

        NotificationHelper notificationHelper = new NotificationHelper(getContext());
        notificationHelper.setUpNotification(NotificationHelper.EXPIRY);
        notificationHelper.setUpNotification(NotificationHelper.SHORTAGE);
    }

    @Override
    public void onClick(View view) {
        if (view.getTag().equals("Отмена")) {
            refill();
            switchMode(Mode.view);
            binding.cancelButton.setVisibility(View.GONE);
        } else if (mode == Mode.view) {
            binding.saveButton.setText("Сохранить изменения");
            binding.cancelButton.setVisibility(View.VISIBLE);
            switchMode(Mode.edit);
        } else {
            saveMedication();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}