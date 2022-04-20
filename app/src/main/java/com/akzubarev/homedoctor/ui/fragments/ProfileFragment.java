package com.akzubarev.homedoctor.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.data.adapters.MedicationAdapter;
import com.akzubarev.homedoctor.data.adapters.PrescriptionAdapter;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.Prescription;
import com.akzubarev.homedoctor.data.models.Profile;
import com.akzubarev.homedoctor.databinding.FragmentProfileBinding;

import java.util.ArrayList;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    String TAG = "ProfileFragment";
    private Profile profile;
    private FragmentProfileBinding binding;
    DataHandler dataHandler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        boolean create_mode = false;
        if (savedInstanceState != null)
            create_mode = savedInstanceState.getBoolean("create", true);

        if (create_mode) {
//            Button b = findViewById(R.id.id);
        } else {
            Bundle bundle = this.getArguments();
            if (bundle != null) {
                String profileID = bundle.getString("Profile");
                dataHandler = DataHandler.getInstance(getContext());
                dataHandler.getProfile(profileID, this::fill);
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
//        rv.setAdapter(rvAdapter);
    }

    private void fill(Profile profile) {
        this.profile = profile;
        binding.name.setText(profile.getName());
        binding.gender.setText(profile.getGender());
        binding.birthday.setText(profile.getBirthday());
        dataHandler.getPrescriptions(this::fillPrescriptions, profile.getDBID());
        dataHandler.getMedications(this::fillMedications, profile.getDBID());
    }

    private void fillPrescriptions(ArrayList<Prescription> prescriptions) {
        configureRecyclerView(binding.prescriptionsList);
        binding.prescriptionsList.setAdapter(new PrescriptionAdapter(prescriptions, profile, getActivity()));
    }

    private void fillMedications(ArrayList<Medication> medications) {
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
    }

    private void onSuccesfulSave() {
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