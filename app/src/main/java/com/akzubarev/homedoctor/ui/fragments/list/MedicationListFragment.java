package com.akzubarev.homedoctor.ui.fragments.list;

import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.akzubarev.homedoctor.data.adapters.MedicationAdapter;
import com.akzubarev.homedoctor.data.adapters.MedicationStatsAdapter;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.MedicationStats;
import com.akzubarev.homedoctor.data.models.Profile;
import com.akzubarev.homedoctor.databinding.FragmentMedicationListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MedicationListFragment extends Fragment {

    private FragmentMedicationListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMedicationListBinding.inflate(inflater, container, false);
        DataHandler.getInstance(getContext()).getMedicationStats(this::fill);
        NavController navController = NavHostFragment.findNavController(this);
        binding.fab.setOnClickListener(view -> navController.navigate(R.id.MedicationFragment));
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals("/users/dgsYX8q5uig3E8YbGIgQVQfeKRr2"))
            binding.fab.setVisibility(View.GONE);
        return binding.getRoot();
    }

    private void fill(ArrayList<MedicationStats> medications) {
        RecyclerView medicationsList = binding.medicationsList;
        medicationsList.setHasFixedSize(true);
        medicationsList.addItemDecoration(new DividerItemDecoration(
                medicationsList.getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager medicationsLayoutManager = new LinearLayoutManager(getContext());

        MedicationStatsAdapter medicationsAdapter = new MedicationStatsAdapter(medications, getActivity());
        medicationsList.setLayoutManager(medicationsLayoutManager);
        medicationsList.setAdapter(medicationsAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}