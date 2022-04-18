package com.akzubarev.homedoctor.data.adapters;

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

    public MedicationAdapter(ArrayList<Medication> medications, Context context) {
        this.medications = medications;
        this.context = context;
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
        Medication Medication = medications.get(medicationNumber);

        TextView medicationName = medicationViewHolder.medicationName;
        medicationName.setText(Medication.getName());

        TextView medicationNextTime = medicationViewHolder.medicationNextTime;
//        medicationNextTime.setText(Medication.nextConsumption().toString());

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

            int position = getAdapterPosition();
            if (listener == null) {
                itemView.setOnClickListener(v -> {
                            NavController navController = Navigation.findNavController(itemView);
                            Bundle bundle = new Bundle();
                            bundle.putInt("User", position);
                            navController.navigate(R.id.MedicationFragment, bundle);
                        }
                );
            }
        }
    }
}

