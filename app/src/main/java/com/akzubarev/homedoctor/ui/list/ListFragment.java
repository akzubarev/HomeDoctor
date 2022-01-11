package com.akzubarev.homedoctor.ui.list;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.databinding.FragmentListBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ListFragment extends Fragment {

    private ListViewModel listViewModel;
    private FragmentListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listViewModel =
                new ViewModelProvider(this).get(ListViewModel.class);

        binding = FragmentListBinding.inflate(inflater, container, false);
        fill();
        return binding.getRoot();
    }

    private void fill() {
        ArrayList<Date> dates = new ArrayList<>();
        dates.add(Calendar.getInstance().getTime());
        ArrayList<Medication> medications = new ArrayList<>();
        medications.add(new Medication("Парацетамол",
                "2 недели", 2, dates));
        medications.add(new Medication("Ибупрофен",
                "1 неделя", 3, dates));
        medications.add(new Medication("Феназепам",
                "1 месяц", 1, dates));

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