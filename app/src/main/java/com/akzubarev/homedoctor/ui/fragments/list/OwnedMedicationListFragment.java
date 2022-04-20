package com.akzubarev.homedoctor.ui.fragments.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.data.adapters.MedicationAdapter;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.databinding.FragmentMedicationListBinding;

import java.util.ArrayList;

public class OwnedMedicationListFragment extends Fragment {

    private FragmentMedicationListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMedicationListBinding.inflate(inflater, container, false);
//        DataHandler.getInstance(getContext()).getMedications(this::fill);
        return binding.getRoot();
    }

    private void fill(ArrayList<Medication> medications) {
        RecyclerView medicationsList = binding.medicationsList;
        medicationsList.setHasFixedSize(true);
        medicationsList.addItemDecoration(new DividerItemDecoration(
                medicationsList.getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager medicationsLayoutManager = new LinearLayoutManager(getContext());

        MedicationAdapter medicationsAdapter = new MedicationAdapter(medications, getActivity());
        medicationsList.setLayoutManager(medicationsLayoutManager);
        medicationsList.setAdapter(medicationsAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}