package com.akzubarev.homedoctor.ui.adapters;

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
import com.akzubarev.homedoctor.data.models.Profile;
import com.akzubarev.homedoctor.data.models.Treatment;

import java.util.ArrayList;

public class OldTreatmentsAdapter
        extends RecyclerView.Adapter<OldTreatmentsAdapter.OldTreatmentViewHolder> {

    private final String TAG = "OldTreatmentsAdapter";
    private final ArrayList<Treatment> treatments;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public OldTreatmentsAdapter(ArrayList<Treatment> treatments, Context context) {
        this.treatments = treatments;
        this.context = context;
    }

    @NonNull
    @Override
    public OldTreatmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.block_old_treatment, viewGroup, false);
        return new OldTreatmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OldTreatmentViewHolder viewHolder, int idx) {
        Treatment treat = treatments.get(idx);

        viewHolder.name.setText(treat.getMedicationId());
        viewHolder.datetime.setText(treat.getTime() + " " + treat.getDay().replaceAll("-", "."));
        viewHolder.prescription.setText(treat.getPrescriptionId());
        viewHolder.profile.setText(treat.getProfileID());
    }

    @Override
    public int getItemCount() {
        return treatments.size();
    }

    public static class OldTreatmentViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView datetime;
        TextView prescription;
        TextView profile;


        public OldTreatmentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.med_name);
            datetime = itemView.findViewById(R.id.treat_datetime);
            prescription = itemView.findViewById(R.id.prescription_name);
            profile = itemView.findViewById(R.id.profile_name);
        }

    }
}

