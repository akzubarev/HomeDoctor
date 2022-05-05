package com.akzubarev.homedoctor.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.adapters.MedicationAdapter;
import com.akzubarev.homedoctor.data.adapters.PrescriptionAdapter;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.Prescription;
import com.akzubarev.homedoctor.data.models.Profile;
import com.akzubarev.homedoctor.databinding.FragmentProfileBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileFragment extends Fragment {
    String TAG = "ProfileFragment";
    private Profile profile;
    private FragmentProfileBinding binding;
    DataHandler dataHandler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        Bundle bundle = this.getArguments();
        dataHandler = DataHandler.getInstance(getContext());
        if (bundle != null) {
            String profileID = bundle.getString("Profile");
            if (profileID != null)
                dataHandler.getProfile(profileID, this::fill);
            else
                fill(new Profile());
        }

        NavController navController = NavHostFragment.findNavController(this);
        binding.addPrescription.setOnClickListener((View v) -> {
            Bundle outBundle = new Bundle();
            outBundle.putString("Profile", profile.getDBID());
            outBundle.putString("Prescription", null);
            navController.navigate(R.id.PrescriptionFragment, bundle);
        });
        binding.addMedicationButton.setOnClickListener((View v) -> navController.navigate(R.id.OwnedMedicationsListFragment));
        binding.editButton.setOnClickListener(this::saveProfile);
        binding.gender.setOnClickListener(this::genderDialog);
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

    private void saveProfile(View view) {
        String name = binding.name.getText().toString();
        String gender = binding.gender.getText().toString();
        String birthday = binding.birthday.getText().toString();

        profile.setName(name);
        profile.setGender(gender);
        profile.setBirthday(birthday);

        DataHandler dataBaseHandler = DataHandler.getInstance(getContext());
        dataBaseHandler.saveProfile(profile);
    }


    private void genderDialog(View view) {
        int checkedItem = 0;
        String[] genders = new String[]{"Женский", "Мужской", "Другой"};
        switch (binding.gender.getText().toString()) {
            case "Мужской":
                checkedItem = 1;
                break;
            case "Другой":
                checkedItem = 2;
                break;
        }
        new AlertDialog.Builder(getContext())
                .setSingleChoiceItems(genders, checkedItem, null)
                .setPositiveButton("Ок", (dialog, whichButton) -> {
                    int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    binding.gender.setText(genders[selectedPosition]);
                }).setNegativeButton("Отмена", (dialog, whichButton) -> {
            dialog.dismiss();
        }).show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}