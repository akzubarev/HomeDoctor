package com.akzubarev.homedoctor.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.ui.adapters.MedicationAdapter;
import com.akzubarev.homedoctor.ui.adapters.PrescriptionAdapter;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.Prescription;
import com.akzubarev.homedoctor.data.models.Profile;
import com.akzubarev.homedoctor.databinding.FragmentProfileBinding;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    String TAG = "ProfileFragment";
    //    private Profile profile;
    private FragmentProfileBinding binding;
    DataHandler dataHandler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        Bundle bundle = this.getArguments();
        dataHandler = DataHandler.getInstance(getContext());
        if (bundle != null) {
            String profileID = bundle.getString("Profile");
            if (profileID != null)
                dataHandler.getProfile(profileID, this::fill);
            else
                fill(new Profile());


            NavController navController = NavHostFragment.findNavController(this);
            binding.addPrescription.setOnClickListener((View v) -> {
                Bundle outBundle = new Bundle();
                outBundle.putString("Profile", profileID);
                outBundle.putString("Prescription", null);
                navController.navigate(R.id.PrescriptionFragment, bundle);
            });
            binding.addMedicationButton.setOnClickListener((View v) -> navController.navigate(R.id.OwnedMedicationsListFragment));
            binding.editButton.setOnClickListener(this::saveProfile);
            binding.gender.setOnClickListener(this::genderDialog);
        }
        return binding.getRoot();
    }

    private void configureRecyclerView(RecyclerView rv) {
        rv.setHasFixedSize(false);
//        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(lm);
//        rv.setAdapter(rvAdapter);
    }

    private void fill(Profile profile) {
        binding.name.setText(profile.getName());
        binding.gender.setText(profile.getGender());
        binding.birthday.setText(profile.getBirthday());
        dataHandler.getPrescriptions(profile.getDBID(), this::fillPrescriptions);
        dataHandler.getMedications(profile.getDBID(), this::fillMedications);
    }

    private void fillPrescriptions(ArrayList<Prescription> prescriptions) {
        configureRecyclerView(binding.prescriptionsList);
        binding.prescriptionsList.setAdapter(new PrescriptionAdapter(prescriptions, buildProfile(), getActivity()));
    }

    private void fillMedications(ArrayList<Medication> medications) {
        configureRecyclerView(binding.medicationsList);
        binding.medicationsList.setAdapter(new MedicationAdapter(medications, getActivity()));
    }

    private void saveProfile(View view) {
        DataHandler dataBaseHandler = DataHandler.getInstance(getContext());
        dataBaseHandler.saveProfile(buildProfile());
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_delete).setVisible(true);
        menu.findItem(R.id.action_info).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_delete) {
            deleteProfile();
            return true;
        } else
            return super.onOptionsItemSelected(menuItem);
    }

    private Profile buildProfile() {
        Profile profile = new Profile();
        profile.setName(binding.name.getText().toString());
        profile.setGender(binding.gender.getText().toString());
        profile.setBirthday(binding.birthday.getText().toString());
        return profile;
    }

    private void deleteProfile() {
        Profile profile = buildProfile();
        Log.d(TAG, "delete profile");
//        dataHandler.deleteProfile(profile);
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}