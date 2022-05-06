package com.akzubarev.homedoctor.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.MedicationStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class MedicationStatsAdapter
        extends RecyclerView.Adapter<MedicationStatsAdapter.MedicationStatsViewHolder> {
    private static final String TAG = "MedicationStatsAdapter";
    private ArrayList<MedicationStats> medicationStats;
    HashMap<String, String> actualMeds = new HashMap<>();
    NavController navController;
    private OnUserClickListener listener;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public interface OnUserClickListener {
        void onUserClick(int position);
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }

    public MedicationStatsAdapter(ArrayList<MedicationStats> medicationStats, Activity activity) {
        this.medicationStats = medicationStats;
        this.context = activity;
        this.navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        DataHandler.getInstance(context).getMedications(this::setMedicationStats);
    }

    public void setMedicationStats(ArrayList<Medication> medications) {
        for (MedicationStats medicationStat : medicationStats)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Optional<Medication> result = medications.stream()
                        .filter(med -> med.getMedicationStatsID().equals(medicationStat.getDBID())).findFirst();
                if (result.isPresent())
                    actualMeds.put(medicationStat.getDBID(), result.get().getDBID());
                else
                    actualMeds.put(medicationStat.getDBID(), null);

            } else {
                String result = null;
                for (Medication med : medications)
                    if (med.getMedicationStatsID().equals(medicationStat.getDBID())) {
                        result = med.getDBID();
                        break;
                    }
                actualMeds.put(medicationStat.getDBID(), result);
            }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicationStatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.block_medication_stat, viewGroup, false);
        return new MedicationStatsViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationStatsViewHolder medicationViewHolder, int medicationNumber) {
        MedicationStats medicationStat = this.medicationStats.get(medicationNumber);
        TextView medicationName = medicationViewHolder.medicationName;
        medicationName.setText(medicationStat.getName());
        String medID = actualMeds.get(medicationStat.getDBID());

        medicationViewHolder.itemView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("Add", false);
                    bundle.putString("Medication", medID);
                    bundle.putString("MedicationStat", medicationStat.getDBID());
                    navController.navigate(R.id.MedicationFragment, bundle);
                }
        );

        medicationViewHolder.add.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("Add", true);
                    bundle.putString("Medication", medID);
                    bundle.putString("MedicationStat", medicationStat.getDBID());
                    navController.navigate(R.id.MedicationFragment, bundle);
                }
        );

        if (medID != null) {
            medicationViewHolder.checkBox.setVisibility(View.VISIBLE);
            medicationViewHolder.add.setVisibility(View.GONE);
        } else {
            medicationViewHolder.checkBox.setVisibility(View.GONE);
            medicationViewHolder.add.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return medicationStats.size();
    }

    public static class MedicationStatsViewHolder extends RecyclerView.ViewHolder {

        TextView medicationName;
        ImageButton add;
        CheckBox checkBox;

        public MedicationStatsViewHolder(@NonNull View itemView, final OnUserClickListener listener) {
            super(itemView);
            medicationName = itemView.findViewById(R.id.medication_name);
            add = itemView.findViewById(R.id.button_add);
            checkBox = itemView.findViewById(R.id.checkbox);

        }
    }
}

