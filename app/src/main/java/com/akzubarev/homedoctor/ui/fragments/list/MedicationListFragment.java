package com.akzubarev.homedoctor.ui.fragments.list;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.ui.adapters.MedicationStatsAdapter;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.MedicationStats;
import com.akzubarev.homedoctor.databinding.FragmentMedicationListBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class MedicationListFragment extends Fragment {
    private final String TAG = "MedicationListFragment";
    private FragmentMedicationListBinding binding;
    private boolean working = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        working = true;
        binding = FragmentMedicationListBinding.inflate(inflater, container, false);
        DataHandler.getInstance(getContext()).getMedicationStats(this::fill);
        NavController navController = NavHostFragment.findNavController(this);
        binding.fab.setOnClickListener(view -> navController.navigate(R.id.MedicationFragment));
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals("/users/dgsYX8q5uig3E8YbGIgQVQfeKRr2"))
            binding.fab.setVisibility(View.GONE);

        return binding.getRoot();
    }

    private void fill(ArrayList<MedicationStats> medications) {
        if (working) {
            medications = (ArrayList<MedicationStats>) medications.stream().sorted(Comparator.comparing(MedicationStats::getName)).collect(Collectors.toList());
            RecyclerView medicationsList = binding.medicationsList;
            medicationsList.setHasFixedSize(true);
//        medicationsList.addItemDecoration(new DividerItemDecoration(
//                medicationsList.getContext(), DividerItemDecoration.VERTICAL));
            LinearLayoutManager medicationsLayoutManager = new LinearLayoutManager(getContext());

            MedicationStatsAdapter medicationsAdapter = new MedicationStatsAdapter(medications, getActivity());
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
                    Log.d(TAG, query);
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