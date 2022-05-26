package com.akzubarev.homedoctor.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.ui.adapters.TreatmentTimeAdapter;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.Prescription;
import com.akzubarev.homedoctor.data.models.Treatment;
import com.akzubarev.homedoctor.databinding.FragmentPrescriptionBinding;
import com.akzubarev.homedoctor.ui.notifications.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class PrescriptionFragment extends Fragment implements View.OnClickListener {
    String TAG = "PrescriptionFragment";
    DataHandler dataHandler;
    String profileID, prescriptionID;
    private FragmentPrescriptionBinding binding;
    HashMap<String, ArrayList<Treatment>> treatments;
    ArrayList<Treatment> oldTreatments = new ArrayList<>();
    ArrayList<Medication> allMedications = new ArrayList<>();
    HashMap<String, Medication> medicationsMap;
    TreatmentTimeAdapter adapter;

    private boolean working = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        working = true;
        setHasOptionsMenu(true);
        binding = FragmentPrescriptionBinding.inflate(inflater, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            profileID = bundle.getString("Profile");
            prescriptionID = bundle.getString("Prescription");
            dataHandler = DataHandler.getInstance(getContext());
            if (prescriptionID != null)
                Log.d(TAG, profileID);
            if (prescriptionID != null)
                Log.d(TAG, prescriptionID);
            if (prescriptionID != null)
                dataHandler.getPrescription(profileID, prescriptionID, this::fill);
            else
                fill(new Prescription());
        }

        binding.editButton.setOnClickListener(this);
        binding.addMedicationButton.setOnClickListener(this);
        binding.endDate.setOnClickListener(view -> prepareDatePickerDialog());
        return binding.getRoot();
    }

    private void configureRecyclerView(RecyclerView rv) {
        rv.setHasFixedSize(false);
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(lm);
    }

    private void fill(Prescription prescription) {
        if (working) {
            binding.name.setText(prescription.getName());
            binding.endDate.setText(prescription.getEndDate());
            binding.diagnosis.setText(prescription.getDiagnosis());
            binding.autoOff.setChecked(prescription.getAutoDisable());

            dataHandler.getTreatments(prescriptionID, this::fillTreatments);
        }
    }

    private void fillMedications(ArrayList<Medication> medications) {
        if (working) {
            allMedications = medications;
            medicationsMap = new HashMap<>();
            for (Medication med : dataHandler.filter(medications, new ArrayList<>(treatments.keySet())))
                medicationsMap.put(med.getDbID(), med);

            configureRecyclerView(binding.medicationsList);
            adapter = new TreatmentTimeAdapter(treatments, medicationsMap, getActivity());
            binding.medicationsList.setAdapter(adapter);
        }
    }

    private void fillTreatments(ArrayList<Treatment> treatmentsList) {
        if (working) {
            treatments = new HashMap<>();
            for (Treatment treatment : treatmentsList) {
                if (treatment.getPrescriptionId().equals(prescriptionID)) {
                    if (!treatments.containsKey(treatment.getMedicationId()))
                        treatments.put(treatment.getMedicationId(), new ArrayList<>());
                    Objects.requireNonNull(treatments.get(treatment.getMedicationId())).add(treatment);
                    oldTreatments.add(treatment);
                }
            }
            dataHandler.getMedications(profileID, this::fillMedications);
        }
    }

    private Prescription buildPrescription() {
        Prescription prescription = new Prescription();
        prescription.setName(binding.name.getText().toString());
        prescription.setDiagnosis(binding.diagnosis.getText().toString());
        prescription.setEndDate(binding.endDate.getText().toString());
        prescription.setAutoDisable(binding.autoOff.isChecked());
        prescription.setDbID(prescriptionID);
        return prescription;
    }

    private void savePrescription() {
        Prescription prescription = buildPrescription();
        dataHandler.savePrescription(prescription, profileID);
        prescriptionID = prescription.getDbID();

        HashMap<String, ArrayList<Pair<String, String>>> treatmentsMap = adapter.gatherTreatments();
        ArrayList<Treatment> treatments = new ArrayList<>();
        for (String medicationID : treatmentsMap.keySet()) {
            ArrayList<Pair<String, String>> dayTimes = treatmentsMap.get(medicationID);
            for (Pair<String, String> dayTime : Objects.requireNonNull(dayTimes))
                treatments.add(new Treatment(medicationID, prescriptionID, profileID, dayTime.first, dayTime.second, 1));
        }
        dataHandler.deleteTreatments(oldTreatments);
        dataHandler.saveTreatments(treatments, this::setAlarm);
        oldTreatments = treatments;
        setAlarm();
    }

    private void setAlarm() {
        new NotificationHelper(getContext()).setUpNotification(NotificationHelper.REMIND);
        Log.d("notifications", "SetUp");
    }

    @Override
    public void onClick(View view) {
        if (view.getTag().equals("save"))
            savePrescription();
        else {
            List<String> ops = allMedications.stream().map(Medication::getName).collect(Collectors.toList());
            if (ops.size() > 0)
                new AlertDialog.Builder(getContext())
                        .setSingleChoiceItems(arrayListToArray(ops), 0, null)
                        .setPositiveButton("Ок", (dialog, whichButton) -> {
                            dialog.dismiss();
                            int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                            addMedication(allMedications.get(selectedPosition));
                        }).setNegativeButton("Отмена", (dialog, whichButton) -> dialog.dismiss())
                        .show();
            else
                Toast.makeText(getContext(), "Нет доступных лекарств", Toast.LENGTH_LONG).show();
        }
    }

    public void prepareDatePickerDialog() {
        DatePicker datePicker = (DatePicker) DatePicker.inflate(getContext(),
                R.layout.selector_date, null);

        new AlertDialog.Builder(getContext()).setView(datePicker)
                .setPositiveButton("Ок", (dialog1, which) -> {
                            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy",
                                    new Locale("ru", "RU"));
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(datePicker.getYear(),
                                    datePicker.getMonth(),
                                    datePicker.getDayOfMonth()
                            );
                            String text = format.format(calendar.getTime());
                            binding.endDate.setText(text);
                        }
                ).setNegativeButton("Отмена", (dialog1, which) -> {
                }).show();
    }

    private String[] arrayListToArray(List<String> allMedications) {
        String[] ops = new String[allMedications.size()];
        for (int i = 0; i < ops.length; i++)
            ops[i] = allMedications.get(i);
        return ops;
    }

    private void addMedication(Medication medication) {
        adapter.add(medication);
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
            deletePrescription();
            return true;
        } else
            return super.onOptionsItemSelected(menuItem);
    }

    private void deletePrescription() {
        Prescription prescription = buildPrescription();
        Log.d(TAG, "delete prescription");
        dataHandler.deletePrescription(prescription);
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