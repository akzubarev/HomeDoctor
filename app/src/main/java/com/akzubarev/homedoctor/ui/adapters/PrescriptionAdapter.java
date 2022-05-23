package com.akzubarev.homedoctor.ui.adapters;

import android.content.Context;
import android.os.Bundle;
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
import com.akzubarev.homedoctor.data.models.Prescription;
import com.akzubarev.homedoctor.data.models.Profile;
import com.akzubarev.homedoctor.data.models.Treatment;

import java.util.ArrayList;

public class PrescriptionAdapter
        extends RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder> {

    private ArrayList<Prescription> prescriptions;
    private Profile profile;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public PrescriptionAdapter(ArrayList<Prescription> prescriptions, Profile profile, Context context) {
        this.prescriptions = prescriptions;
        this.context = context;
        this.profile = profile;
    }

    @NonNull
    @Override
    public PrescriptionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.block_prescription, viewGroup, false);
        return new PrescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionViewHolder viewHolder, int PrescriptionNumber) {
        Prescription prescription = prescriptions.get(PrescriptionNumber);

        TextView prescriptionName = viewHolder.prescriptionName;
        prescriptionName.setText(prescription.getName());

        viewHolder.itemView.setOnClickListener(v -> {
                    NavController navController = Navigation.findNavController(viewHolder.itemView);
                    Bundle bundle = new Bundle();
                    bundle.putString("Profile", profile.getDbID());
                    bundle.putString("Prescription", prescription.getDbID());
                    navController.navigate(R.id.PrescriptionFragment, bundle);
                }
        );
        viewHolder.setNextTime(prescription);
    }

    @Override
    public int getItemCount() {
        return prescriptions.size();
    }

    public static class PrescriptionViewHolder extends RecyclerView.ViewHolder {

        TextView prescriptionNextTime;
        TextView prescriptionNextName;
        TextView prescriptionName;
        ImageView alarm;

        public PrescriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            prescriptionNextTime = itemView.findViewById(R.id.next_consumption);
            prescriptionNextName = itemView.findViewById(R.id.medication_name);
            alarm = itemView.findViewById(R.id.alarm);
            prescriptionName = itemView.findViewById(R.id.prescription_name);
        }

        public void setNextTime(Prescription prescription) {
            DataHandler dataHandler = DataHandler.getInstance(itemView.getContext());
            dataHandler.findNextReminderForPrescription(prescription.getDbID(),
                    (Treatment treatment) -> {
                        prescriptionNextTime.setText(treatment.getDateTime());
                        dataHandler.getMedication(treatment.getMedicationId(), (Medication med) -> prescriptionNextName.setText(med.getName()));

                    },
                    () -> {
                        prescriptionNextTime.setText("");
                        prescriptionNextName.setText("");
                        alarm.setImageResource(R.drawable.ic_alarm_off);
                    });
        }
    }
}

