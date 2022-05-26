package com.akzubarev.homedoctor.ui.fragments.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.databinding.FragmentMedicationListOwnedBinding;
import com.akzubarev.homedoctor.ui.adapters.MedicationAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class OwnedMedicationListFragment extends Fragment {

    private FragmentMedicationListOwnedBinding binding;
    private boolean working = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        working = true;
        binding = FragmentMedicationListOwnedBinding.inflate(inflater, container, false);
        DataHandler.getInstance(getContext()).getMedications(this::fill);

        NavController navController = NavHostFragment.findNavController(this);
        binding.fab.setOnClickListener(view -> navController.navigate(R.id.MedicationsListFragment));
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return binding.getRoot();
    }

    private void fill(ArrayList<Medication> medications) {
        if (working) {
            medications = (ArrayList<Medication>) medications.stream().sorted(Comparator.comparing(Medication::getName)).collect(Collectors.toList());
            RecyclerView medicationsList = binding.medicationsList;
            medicationsList.setHasFixedSize(true);
            LinearLayoutManager medicationsLayoutManager = new LinearLayoutManager(getContext());

            MedicationAdapter medicationsAdapter = new MedicationAdapter(medications, getActivity());
            medicationsList.setLayoutManager(medicationsLayoutManager);
            medicationsList.setAdapter(medicationsAdapter);

            binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    medicationsAdapter.filter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    medicationsAdapter.filter(query);
                    return true;
                }
            });
            binding.search.setOnClickListener(v -> binding.search.setIconified(false));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        NavController navController = NavHostFragment.findNavController(this);
        if (menuItem.getItemId() == R.id.action_settings) {
            navController.navigate(R.id.SettingsFragment);
            return true;
        } else
            return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onDestroyView() {
        working = false;
        super.onDestroyView();
        binding = null;
    }
}