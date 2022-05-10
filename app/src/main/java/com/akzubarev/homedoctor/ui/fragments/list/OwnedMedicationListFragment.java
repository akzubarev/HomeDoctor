package com.akzubarev.homedoctor.ui.fragments.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.ui.adapters.MedicationAdapter;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.databinding.FragmentMedicationListOwnedBinding;

import java.util.ArrayList;

public class OwnedMedicationListFragment extends Fragment {

    private FragmentMedicationListOwnedBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMedicationListOwnedBinding.inflate(inflater, container, false);
        DataHandler.getInstance(getContext()).getMedications(this::fill);

        NavController navController = NavHostFragment.findNavController(this);
        binding.fab.setOnClickListener(view -> navController.navigate(R.id.MedicationsListFragment));
        return binding.getRoot();
    }

    private void fill(ArrayList<Medication> medications) {
        RecyclerView medicationsList = binding.medicationsList;
        medicationsList.setHasFixedSize(true);
//        medicationsList.addItemDecoration(new DividerItemDecoration(
//                medicationsList.getContext(), DividerItemDecoration.VERTICAL));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}