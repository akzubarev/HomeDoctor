package com.akzubarev.homedoctor.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.MedicationStats;
import com.akzubarev.homedoctor.data.models.Profile;
import com.akzubarev.homedoctor.databinding.FragmentMedicationBinding;
import com.akzubarev.homedoctor.ui.notifications.NotificationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MedicationFragment extends Fragment implements View.OnClickListener {
    String TAG = "MedicationFragment";
    private FragmentMedicationBinding binding;
    DataHandler dataHandler;
    String medicationID, medicationStatID;
    Mode mode = Mode.view;

    enum Mode {view, create, edit, add}

    Medication medication;
    MedicationStats medicationStat;

    ArrayList<Profile> profiles = new ArrayList<>();
    Map<String, Boolean> allowed = new HashMap<>();
    ArrayList<Integer> allowedIdx = new ArrayList<>();
    AlertDialog.Builder builder;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMedicationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Bundle bundle = this.getArguments();
        dataHandler = DataHandler.getInstance(getContext());
        if (bundle != null) {
            medicationStatID = bundle.getString("MedicationStat");
            medicationID = bundle.getString("Medication");
            boolean add = bundle.getBoolean("Add");
            if (add)
                mode = Mode.add;
            dataHandler.getMedicationStat(medicationStatID, this::fillStat);
            configureSpinner(binding.durationSpinner, R.array.duration_dropdown, (int choice) -> {
                String[] options = getResources().getStringArray(R.array.duration_dropdown);
                String duration = options[choice];
//            DataReader.SaveInt(goal_minutes, "", getContext());
            }, 0); // DataReader.GetInt(DataReader.GOAL, getContext()) / 5 - 1);

            binding.editButton.setOnClickListener(this);
            binding.cancelButton.setOnClickListener(this);
            binding.allowedProfiles.setOnClickListener((View v) -> builder.show());
            binding.cancelButton.setVisibility(View.GONE);
        }

        return view;
    }

    private void refill() {
//        fillStat(medicationStats);
        fillMedication(medication);
    }

    private void fillStat(MedicationStats medicationStats) {
        this.medicationStat = medicationStats;
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
        if (medicationID != null)
            dataHandler.getMedication(medicationID, this::fillMedication);

        switchMode(mode);
    }

    private void fillMedication(Medication medication) {
        this.medication = medication;
        binding.medicationLayout.setVisibility(View.VISIBLE);
        binding.tabletsEditText.setText(String.valueOf(medication.getAmount()));
        binding.expiryDateEditText.setText(medication.getExpiryDate());
        prepareProfileChoiceDialog();
    }


    private void switchMode(Mode mode) {
        this.mode = mode;
        View[] statViews = new View[]{binding.nameEditText, binding.frequencyEditText,
                binding.durationEditText, binding.durationSpinner,
                binding.packEditText, binding.usageEditText
        };
        View[] medViews = new View[]{binding.tabletsEditText, binding.expiryDateEditText, binding.allowedProfiles};

        switch (mode) {
            case add:
                binding.medicationLayout.setVisibility(View.VISIBLE);
                binding.editButton.setText("Добавить");
                for (View v : medViews) {
                    if (v instanceof EditText)
                        ((EditText) v).setText("");
                    v.setEnabled(true);
                }
                fillMedication(new Medication(medicationStat.getName(), medicationStatID));
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
        if (medicationID == null && mode != Mode.add) {
            binding.medicationLayout.setVisibility(View.GONE);
            binding.editButton.setVisibility(View.GONE);
        } else {
            binding.medicationLayout.setVisibility(View.VISIBLE);
            binding.editButton.setVisibility(View.VISIBLE);
            if (mode == Mode.view)
                binding.editButton.setText("Редактировать");
            else if (mode == Mode.edit)
                binding.editButton.setText("Сохранить");
        }
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
        medToSave.setExpiryDate(binding.expiryDateEditText.getText().toString());
        medToSave.setMedicationStatsID(medicationStat.getDBID());
        medToSave.setAllowedProfiles(allowed);

        dataHandler.saveMedication(medToSave);
        onSuccessfulSave();
    }

    private void onSuccessfulSave() {
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
            binding.editButton.setText("Сохранить изменения");
            binding.cancelButton.setVisibility(View.VISIBLE);
            switchMode(Mode.edit);
        } else {
            saveMedication();
        }
    }

    public void prepareProfileChoiceDialog() {
        dataHandler.getProfiles((result -> {
            profiles = result;
            allowed = new HashMap<>(medication.getAllowedProfiles());
            boolean[] checked = new boolean[profiles.size()];
            String[] items = new String[profiles.size()];
            allowedIdx = new ArrayList<>();
            for (int i = 0; i < profiles.size(); i++) {
                String profileID = profiles.get(i).getDBID();
                boolean allows = allowed.getOrDefault(profileID, false);
                checked[i] = allows;
                items[i] = profiles.get(i).getName();
                if (allows)
                    allowedIdx.add(i);
            }
            handleDialogResult();
            builder = new AlertDialog.Builder(getContext());

            builder.setMultiChoiceItems(items, checked, (dialog, indexSelected, isChecked) -> {
                if (isChecked)
                    allowedIdx.add(indexSelected);
                else if (allowedIdx.contains(indexSelected))
                    allowedIdx.remove((Object) indexSelected);

            }).setPositiveButton("OK", (dialog, id) -> {
                handleDialogResult();
                for (int i = 0; i < profiles.size(); i++)
                    allowed.put(profiles.get(i).getDBID(), allowedIdx.contains(i));
//            dataHandler.saveAllowed(medicationID, allowed);
            });
        }));
    }


    private void handleDialogResult() {
        StringBuilder text = new StringBuilder();
        if (allowedIdx.size() == profiles.size())
            text.append("Все");
        else if (allowedIdx.size() == 0)
            text.append("Никто");
        else {
            for (int idx : allowedIdx) {
                if (text.length() > 0)
                    text.append(", ");
                text.append(profiles.get(idx).getName());
            }
        }
        binding.allowedProfiles.setText(text);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}