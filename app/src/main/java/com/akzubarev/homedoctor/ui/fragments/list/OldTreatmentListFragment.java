package com.akzubarev.homedoctor.ui.fragments.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Treatment;
import com.akzubarev.homedoctor.databinding.FragmentOldTreatmentsListBinding;
import com.akzubarev.homedoctor.ui.adapters.OldTreatmentsAdapter;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class OldTreatmentListFragment extends Fragment {
    private final String TAG = "FragmentOldTreatmentsList";
    private FragmentOldTreatmentsListBinding binding;
    private boolean working = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        working = true;
        binding = FragmentOldTreatmentsListBinding.inflate(inflater, container, false);
        DataHandler.getInstance(getContext()).getOldTreatments(this::fill);
        return binding.getRoot();
    }

    private void fill(ArrayList<Treatment> treatments) {
        if (working) {
            treatments = (ArrayList<Treatment>) treatments.stream().sorted(
                    (x, y) -> -(x.getDay() + " " + x.getTime()).compareTo(y.getDay() + " " + y.getTime())
            ).collect(Collectors.toList());
            RecyclerView medicationsList = binding.oldTreatmentsList;
            medicationsList.setHasFixedSize(true);
            LinearLayoutManager lm = new LinearLayoutManager(getContext());

            OldTreatmentsAdapter treatmentsAdapter = new OldTreatmentsAdapter(treatments, getActivity());
            medicationsList.setLayoutManager(lm);
            medicationsList.setAdapter(treatmentsAdapter);
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