package com.akzubarev.homedoctor.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.models.Medication;

import java.util.ArrayList;

public class MedicationAdapter
        extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private ArrayList<Medication> medications;
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

    public MedicationAdapter(ArrayList<Medication> medications, Activity activity) {
        this.medications = medications;
        this.context = activity;
        this.navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.block_medication, viewGroup, false);
        return new MedicationViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder medicationViewHolder, int medicationNumber) {
        Medication medication = medications.get(medicationNumber);

        TextView medicationName = medicationViewHolder.medicationName;
        medicationName.setText(medication.getName());

        TextView medicationNextTime = medicationViewHolder.medicationNextTime;
//        medicationNextTime.setText(Medication.nextConsumption().toString());
        medicationViewHolder.itemView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("Medication", medication.getDBID());
                    bundle.putString("MedicationStat", medication.getMedicationStatsID());
                    navController.navigate(R.id.MedicationFragment, bundle);
                }
        );
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    public static class MedicationViewHolder extends RecyclerView.ViewHolder {

        TextView medicationNextTime;
        TextView medicationName;

        public MedicationViewHolder(@NonNull View itemView, final OnUserClickListener listener) {
            super(itemView);
            medicationNextTime = itemView.findViewById(R.id.next_consumption);
            medicationName = itemView.findViewById(R.id.medication_name);

        }
    }
}

