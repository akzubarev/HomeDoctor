package com.akzubarev.homedoctor.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.MedicationStats;
import com.akzubarev.homedoctor.data.models.Profile;
import com.akzubarev.homedoctor.databinding.FragmentMedicationBinding;
import com.akzubarev.homedoctor.ui.adapters.MedicationStatsAdapter;
import com.akzubarev.homedoctor.ui.notifications.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MedicationFragment extends Fragment {
    String TAG = "MedicationFragment";
    private FragmentMedicationBinding binding;
    DataHandler dataHandler;
    String medicationID, medicationStatID;
    Mode mode = Mode.view;
    private boolean working = true;

    enum Mode {view, create, edit, add}

    //    Medication medication;
    private MedicationStats medicationStat;

    ArrayList<Profile> profiles = new ArrayList<>();
    Map<String, Boolean> allowed = new HashMap<>();
    ArrayList<Integer> allowedIdx = new ArrayList<>();
    AlertDialog.Builder builder;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        working = true;
        binding = FragmentMedicationBinding.inflate(inflater, container, false);
        dataHandler = DataHandler.getInstance(getContext());
        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            medicationStatID = bundle.getString("MedicationStat");
            medicationID = bundle.getString("Medication");
            boolean add = bundle.getBoolean("Add");
            if (add)
                mode = Mode.add;
            if (medicationStatID == null && medicationID == null)
                switchMode(Mode.create);
            else {
                dataHandler.getMedicationStat(medicationStatID, this::fillStat);
//            configureSpinner(binding.durationSpinner, R.array.duration_dropdown, (int choice) -> {
//                String[] options = getResources().getStringArray(R.array.duration_dropdown);
//                String duration = options[choice];
////            DataReader.SaveInt(goal_minutes, "", getContext());
//            }, 0); // DataReader.GetInt(DataReader.GOAL, getContext()) / 5 - 1);
            }
        }

        binding.editButton.setOnClickListener(view -> edit());
        binding.cancelButton.setOnClickListener(view -> cancel());
        binding.expiryDateEditText.setOnClickListener(view -> prepareDatePickerDialog());
        binding.allowedProfiles.setOnClickListener(view -> builder.show());
        binding.cancelButton.setVisibility(View.GONE);
        return binding.getRoot();
    }

    private void refill() {
//        fillStat(medicationStats);
        fillMedication(buildMedication());
    }

    private void fillStat(MedicationStats medicationStats) {
        if (working) {
            this.medicationStat = medicationStats;
            binding.nameEditText.setText(medicationStats.getName());
            binding.form.setText(medicationStats.getForm());
            binding.group.setText(medicationStats.getGroup());
            binding.idText.setText(medicationStats.getDbID());
            if (medicationID != null)
                dataHandler.getMedication(medicationID, this::fillMedication);

            switchMode(mode);
            dataHandler.getMedicationStats(analogs -> {
                Stream<MedicationStats> filtered = analogs.stream().filter(ms ->
                        medicationStat.getRelationships().
                                getOrDefault(ms.getDbID(), "None")
                                .equals(MedicationStats.ANALOG)
                );
                Stream<MedicationStats> sorted = filtered.sorted(Comparator.comparing(MedicationStats::getName));
                analogs = (ArrayList<MedicationStats>) sorted.collect(Collectors.toList());

                if (analogs.size() > 0)
                    binding.noAnalogs.setVisibility(View.GONE);
                else
                    binding.noAnalogs.setVisibility(View.VISIBLE);


                RecyclerView medicationsList = binding.analogs;
                medicationsList.setHasFixedSize(true);
                LinearLayoutManager medicationsLayoutManager = new LinearLayoutManager(getContext());
                MedicationStatsAdapter medicationsAdapter = new MedicationStatsAdapter(analogs, getActivity());
                medicationsList.setLayoutManager(medicationsLayoutManager);
                medicationsList.setAdapter(medicationsAdapter);
            });
        }
    }

    private void fillMedication(Medication medication) {
        if (working) {
            binding.medicationLayout.setVisibility(View.VISIBLE);
            binding.amount.setText(String.valueOf(medication.getAmount()));
            binding.expiryDateEditText.setText(medication.getExpiryDate());
            binding.notify.setChecked(medication.getReminders());
            allowed = new HashMap<>(medication.getAllowedProfiles());
            prepareProfileChoiceDialog();
        }
    }


    private void switchMode(Mode mode) {
        Log.d(TAG, mode.name());
        this.mode = mode;
        View[] statViews = new View[]{binding.nameEditText, binding.group,
                binding.form, binding.idText
//                binding.durationSpinner,binding.packEditText, binding.usageEditText
        };
        View[] medViews = new View[]{binding.amount, binding.expiryDateEditText,
                binding.allowedProfiles, binding.notify};

        switch (mode) {
            case add:
                binding.medicationLayout.setVisibility(View.VISIBLE);
                binding.editButton.setText("Сохранить");
                for (View v : medViews) {
                    if (v instanceof EditText)
                        ((EditText) v).setText("");
                    v.setEnabled(true);
                }
                fillMedication(new Medication(medicationStat.getName(), medicationStatID));
                break;
            case view:
                binding.editButton.setText("Добавить");
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
                binding.medicationLayout.setVisibility(View.VISIBLE);
                for (View v : medViews)
                    v.setEnabled(true);
                break;
        }
        binding.editButton.setVisibility(View.VISIBLE);
        if (medicationID == null && mode != Mode.edit) {
            binding.medicationLayout.setVisibility(View.GONE);
//            binding.editButton.setVisibility(View.GONE);
        } else {
            binding.medicationLayout.setVisibility(View.VISIBLE);
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
        if (mode == Mode.create) {
            MedicationStats medStat = buildMedicationStat();
            if (medStat.validate()) {
                dataHandler.saveMedicationStats(medStat, this::onSuccessfulSave);
                Toast.makeText(getContext(), "Успешно сохранено", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getContext(), "Ошибка, проверьте заполненные поля", Toast.LENGTH_LONG).show();
        } else {
            Medication med = buildMedication();
            if (med.validate()) {
                dataHandler.saveMedication(med, this::onSuccessfulSave);
                Toast.makeText(getContext(), "Успешно сохранено", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getContext(), "Ошибка, проверьте заполненные поля", Toast.LENGTH_LONG).show();
        }
    }

    private MedicationStats buildMedicationStat() {
        MedicationStats medicationStats = new MedicationStats();
        medicationStats.setName(binding.nameEditText.getText().toString());
        medicationStats.setForm(binding.form.getText().toString());
        medicationStats.setGroup(binding.group.getText().toString());
        return medicationStats;
    }

    private void onSuccessfulSave() {
        binding.cancelButton.setVisibility(View.GONE);
        if (mode == Mode.create)
            switchMode(Mode.add);
        else
            switchMode(Mode.view);

        NotificationHelper notificationHelper = new NotificationHelper(getContext());
        notificationHelper.setUpNotification(NotificationHelper.EXPIRY);
        notificationHelper.setUpNotification(NotificationHelper.SHORTAGE);
    }

    public void cancel() {
        refill();
        switchMode(Mode.view);
        binding.cancelButton.setVisibility(View.GONE);
    }

    public void edit() {
        if (mode == Mode.view) {
            binding.editButton.setText("Сохранить");
            binding.cancelButton.setVisibility(View.VISIBLE);
            switchMode(Mode.edit);
        } else {
            saveMedication();
        }
    }

    public void prepareProfileChoiceDialog() {
        dataHandler.getProfiles((result -> {
            profiles = result;
            boolean[] checked = new boolean[profiles.size()];
            String[] items = new String[profiles.size()];
            allowedIdx = new ArrayList<>();
            for (int i = 0; i < profiles.size(); i++) {
                String profileID = profiles.get(i).getDbID();
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
                    allowed.put(profiles.get(i).getDbID(), allowedIdx.contains(i));
//            dataHandler.saveAllowed(medicationID, allowed);
            });
        }));
    }

    public void prepareDatePickerDialog() {
        DatePicker datePicker = (DatePicker) DatePicker.inflate(getContext(),
                R.layout.selector_date, null);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(datePicker)
                .setPositiveButton("Ок", (dialog1, which) ->
                        {

                            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy",
                                    new Locale("ru", "RU"));
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(datePicker.getYear(),
                                    datePicker.getMonth(),
                                    datePicker.getDayOfMonth()
                            );
                            String text = format.format(calendar.getTime());
                            binding.expiryDateEditText.setText(text);
                        }
                ).setNegativeButton("Отмена", (dialog1, which) -> {
                }).show();
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_delete).setVisible(true);
        menu.findItem(R.id.action_info).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_delete) {
            deleteMedication();
            return true;
        } else
            return super.onOptionsItemSelected(menuItem);
    }

    private Medication buildMedication() {
        Medication medication = new Medication();
        medication.setName(binding.nameEditText.getText().toString());
        medication.setExpiryDate(binding.expiryDateEditText.getText().toString());
        medication.setAmount(Integer.parseInt(binding.amount.getText().toString()));
        medication.setMedicationStatsID(medicationStat.getDbID());
        medication.setReminders(binding.notify.isChecked());
        medication.setAllowedProfiles(allowed);
        medication.setDbID(medicationID);
        return medication;
    }

    private void deleteMedication() {
        Medication medication = buildMedication();
        Log.d(TAG, "delete medication");
        dataHandler.deleteMedication(medication);
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
    }

    @Override
    public void onDestroyView() {
        working = false;
        super.onDestroyView();
        binding = null;
    }
}