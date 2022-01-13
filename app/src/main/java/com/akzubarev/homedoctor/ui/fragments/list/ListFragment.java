package com.akzubarev.homedoctor.ui.fragments.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.databinding.FragmentListBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ListFragment extends Fragment {

    private ListViewModel listViewModel;
    private FragmentListBinding binding;
    private ArrayList<Medication> medications = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listViewModel =
                new ViewModelProvider(this).get(ListViewModel.class);

        binding = FragmentListBinding.inflate(inflater, container, false);
        fill();
        return binding.getRoot();
    }

    private void fill() {
        RecyclerView medicationsList = binding.medicationsList;
        medicationsList.setHasFixedSize(true);
        medicationsList.addItemDecoration(new DividerItemDecoration(
                medicationsList.getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager medicationsLayoutManager = new LinearLayoutManager(getContext());
        MedicationAdapter medicationsAdapter = new MedicationAdapter(medications, getContext());

        medicationsList.setLayoutManager(medicationsLayoutManager);
        medicationsList.setAdapter(medicationsAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}