package com.akzubarev.homedoctor.ui.fragments.list;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.MedicationStats;
import com.akzubarev.homedoctor.data.models.Treatment;
import com.akzubarev.homedoctor.databinding.FragmentMedicationListBinding;
import com.akzubarev.homedoctor.databinding.FragmentOldTreatmentsListBinding;
import com.akzubarev.homedoctor.ui.adapters.MedicationStatsAdapter;
import com.akzubarev.homedoctor.ui.adapters.OldTreatmentsAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class OldTreatmentListFragment extends Fragment {
    private final String TAG = "FragmentOldTreatmentsList";
    private FragmentOldTreatmentsListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOldTreatmentsListBinding.inflate(inflater, container, false);
        DataHandler.getInstance(getContext()).getOldTreatments(this::fill);
        return binding.getRoot();
    }

    private void fill(ArrayList<Treatment> treatments) {
        treatments = (ArrayList<Treatment>) treatments.stream().sorted((Comparator.comparing(t -> (t.getTime() + " " + t.getDay())))).collect(Collectors.toList());
        RecyclerView medicationsList = binding.oldTreatmentsList;
        medicationsList.setHasFixedSize(true);
//        medicationsList.addItemDecoration(new DividerItemDecoration(
//                medicationsList.getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager lm = new LinearLayoutManager(getContext());

        OldTreatmentsAdapter treatmentsAdapter = new OldTreatmentsAdapter(treatments, getActivity());
        medicationsList.setLayoutManager(lm);
        medicationsList.setAdapter(treatmentsAdapter);
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
        super.onDestroyView();
        binding = null;
    }
}