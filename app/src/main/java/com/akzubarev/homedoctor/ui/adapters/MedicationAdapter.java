package com.akzubarev.homedoctor.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.Treatment;

import java.util.ArrayList;

public class MedicationAdapter
        extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private static final String TAG = "MedicationAdapter";
    private final ArrayList<Medication> medications;
    private final ArrayList<Medication> medicationsAll;
    NavController navController;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public MedicationAdapter(ArrayList<Medication> medications, Activity activity) {
        this.medications = medications;
        this.medicationsAll = new ArrayList<>(medications);
        this.context = activity;
        this.navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.block_medication, viewGroup, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder medicationViewHolder, int medicationNumber) {
        Medication medication = medications.get(medicationNumber);

        TextView medicationName = medicationViewHolder.medicationName;
        medicationName.setText(medication.getName());

        medicationViewHolder.setNextTime(medication);
        medicationViewHolder.itemView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    Log.d(TAG, medication.getMedicationStatsID());
                    bundle.putString("Medication", medication.getDbID());
                    bundle.putString("MedicationStat", medication.getMedicationStatsID());
                    navController.navigate(R.id.MedicationFragment, bundle);
                }
        );
    }

    public void filter(String text) {
        medications.clear();
        if (text.isEmpty()) {
            medications.addAll(medicationsAll);
        } else {
            text = text.toLowerCase();
            for (Medication med : medicationsAll) {
                if (med.getName().toLowerCase().contains(text)) {
                    medications.add(med);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    public static class MedicationViewHolder extends RecyclerView.ViewHolder {

        TextView medicationNextTime;
        TextView medicationName;
        ImageView alarm;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            medicationNextTime = itemView.findViewById(R.id.reminder);
            medicationName = itemView.findViewById(R.id.medication_name);
            alarm = itemView.findViewById(R.id.alarm);
        }

        public void setNextTime(Medication medication) {
            DataHandler dataHandler = DataHandler.getInstance(itemView.getContext());
            dataHandler.findNextReminder(medication.getDbID(), (Treatment treatment) -> medicationNextTime.setText(treatment.getDateTime()),
                    () ->
                    {
                        medicationNextTime.setText("");
                        alarm.setImageResource(R.drawable.ic_alarm_off);
                    });
        }
    }
}

