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
import com.akzubarev.homedoctor.data.adapters.MedicationAdapter;
import com.akzubarev.homedoctor.data.adapters.PrescriptionAdapter;
import com.akzubarev.homedoctor.data.adapters.RemindTimeAdapter;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.Prescription;
import com.akzubarev.homedoctor.data.models.Profile;
import com.akzubarev.homedoctor.data.models.Treatment;
import com.akzubarev.homedoctor.databinding.FragmentMedicationBinding;
import com.akzubarev.homedoctor.databinding.FragmentPrescriptionBinding;
import com.akzubarev.homedoctor.databinding.FragmentProfileBinding;

import java.util.ArrayList;
import java.util.Date;

public class PrescriptionFragment extends Fragment implements View.OnClickListener {
    String TAG = "PrescriptionFragment";
    DataHandler dataHandler;
    String profileID;
    private FragmentPrescriptionBinding binding;
    private ArrayList<Treatment> treatments = new ArrayList<>();
    private ArrayList<String> medicationIDs = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPrescriptionBinding.inflate(inflater, container, false);
        boolean create_mode = false;
        if (savedInstanceState != null)
            create_mode = savedInstanceState.getBoolean("create", true);

        if (create_mode) {
//            Button b = findViewById(R.id.id);
        } else {
            Bundle bundle = this.getArguments();
            if (bundle != null) {
                profileID = bundle.getString("Profile");
                String prescriptionID = bundle.getString("Prescription");
                dataHandler = DataHandler.getInstance(getContext());
                dataHandler.getPrescription(this::fill, profileID, prescriptionID);
            }
        }
        binding.saveButton.setOnClickListener(this);
        return binding.getRoot();
    }

    private void configureRecyclerView(RecyclerView rv) {
        rv.setHasFixedSize(false);
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(lm);
    }

    private void fill(Prescription prescription) {
        binding.name.setText(prescription.getName());
        binding.length.setText(prescription.getLength());
        binding.diagnosis.setText(prescription.getDiagnosis());
//        prescription.getConsumptionTimes();

        dataHandler.getTreatments(this::fillTreatments, prescription.getDBID());
    }

    private void fillTreatments(ArrayList<Treatment> treatments) {
        this.treatments = treatments;
        for (Treatment t : treatments)
            if (!medicationIDs.contains(t.getMedicationId()))
                this.medicationIDs.add(t.getMedicationId());

            dataHandler.getMedications(this::fillMedications, profileID);
    }

    private void fillMedications(ArrayList<Medication> medications) {
        medications = dataHandler.filter(medications, medicationIDs);
        configureRecyclerView(binding.medicationsList);
        binding.medicationsList.setAdapter(new MedicationAdapter(medications, getActivity()));
    }

    private void saveMedication() {
//        String name = binding.nameEditText.getText().toString();
//        String courceLength = String.format("%s %s",
//                binding.durationEditText.getText().toString(),
//                binding.durationSpinner.getSelectedItem().toString());
//        int dailyFrequency = Integer.parseInt(binding.frequencyEditText.getText().toString());
//
////        Medication med = new Medication(name, courceLength, dailyFrequency);
//        DataHandler dataBaseHandler = DataHandler.getInstance(getContext());
////        dataBaseHandler.addMedication(med);
//        onSuccesfulSave();
//    }
//
//    private void onSuccesfulSave() {
//        NavController navController = NavHostFragment.findNavController(this);
//        navController.popBackStack();
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