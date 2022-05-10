package com.akzubarev.homedoctor.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
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
import java.util.function.Consumer;

public class MedicationStatsAdapter
        extends RecyclerView.Adapter<MedicationStatsAdapter.MedicationStatsViewHolder> {
    private static final String TAG = "MedicationStatsAdapter";
    private ArrayList<MedicationStats> medicationStats;
    private ArrayList<MedicationStats> medicationStatsAll;
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
        this.medicationStatsAll = new ArrayList<>(medicationStats);
        this.context = activity;
        this.navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        DataHandler.getInstance(context).getMedications(this::setMedicationStats);
    }

    public void setMedicationStats(ArrayList<Medication> medications) {
        for (MedicationStats medicationStat : medicationStats) {
            Optional<Medication> result = medications.stream()
                    .filter(med -> med.getMedicationStatsID().equals(medicationStat.getDBID())).findFirst();
            if (result.isPresent())
                actualMeds.put(medicationStat.getDBID(), result.get().getDBID());
            else
                actualMeds.put(medicationStat.getDBID(), null);

        }
        notifyDataSetChanged();
    }

    public void filter(String text) {
        medicationStats.clear();
        if (text.isEmpty()) {
            medicationStats.addAll(medicationStatsAll);
        } else {
            text = text.toLowerCase();
            for (MedicationStats med : medicationStatsAll) {
                Log.d(TAG, med.getName());
                if (med.getName().toLowerCase().contains(text)) {
                    medicationStats.add(med);
                }
            }
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

        View.OnClickListener normalOpener = v -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("Add", false);
            bundle.putString("Medication", medID);
            bundle.putString("MedicationStat", medicationStat.getDBID());
            navController.navigate(R.id.MedicationFragment, bundle);
        };
        View.OnClickListener adder = v -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("Add", true);
            bundle.putString("Medication", medID);
            bundle.putString("MedicationStat", medicationStat.getDBID());
            navController.navigate(R.id.MedicationFragment, bundle);
        };

        medicationViewHolder.itemView.setOnClickListener(normalOpener);

        ImageButton img = medicationViewHolder.add;
        if (medID != null) {
            img.setImageResource(R.drawable.ic_done);
            img.setOnClickListener(normalOpener);
        } else {
            img.setImageResource(R.drawable.ic_add);
            img.setOnClickListener(adder);
        }
    }

    @Override
    public int getItemCount() {
        return medicationStats.size();
    }

    public static class MedicationStatsViewHolder extends RecyclerView.ViewHolder {

        TextView medicationName;
        ImageButton add;

        public MedicationStatsViewHolder(@NonNull View itemView, final OnUserClickListener listener) {
            super(itemView);
            medicationName = itemView.findViewById(R.id.medication_name);
            add = itemView.findViewById(R.id.button_add);

        }
    }
}

