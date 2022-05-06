package com.akzubarev.homedoctor.ui.adapters;

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
import com.akzubarev.homedoctor.data.models.Prescription;
import com.akzubarev.homedoctor.data.models.Profile;

import java.util.ArrayList;

public class PrescriptionAdapter
        extends RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder> {

    private ArrayList<Prescription> prescriptions;
    private Profile profile;
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
        return new PrescriptionViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionViewHolder viewHolder, int PrescriptionNumber) {
        Prescription prescription = prescriptions.get(PrescriptionNumber);

        TextView prescriptionName = viewHolder.prescriptionName;
        prescriptionName.setText(prescription.getName());

        TextView prescriptionNextTime = viewHolder.prescriptionNextTime;
//        PrescriptionNextTime.setText(Prescription.nextConsumption().toString());
        if (listener == null) {
            viewHolder.itemView.setOnClickListener(v -> {
                        NavController navController = Navigation.findNavController(viewHolder.itemView);
                        Bundle bundle = new Bundle();
                        bundle.putString("Profile", profile.getDBID());
                        bundle.putString("Prescription", prescription.getDBID());
                        navController.navigate(R.id.PrescriptionFragment, bundle);
                    }
            );
        }
    }

    @Override
    public int getItemCount() {
        return prescriptions.size();
    }

    public static class PrescriptionViewHolder extends RecyclerView.ViewHolder {

        TextView prescriptionNextTime;
        TextView prescriptionName;

        public PrescriptionViewHolder(@NonNull View itemView, final OnUserClickListener listener) {
            super(itemView);
            prescriptionNextTime = itemView.findViewById(R.id.next_consumption);
            prescriptionName = itemView.findViewById(R.id.prescription_name);
        }
    }
}

