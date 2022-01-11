package com.akzubarev.homedoctor.ui.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
    public void onBindViewHolder(@NonNull MedicationViewHolder MedicationViewHolder, int MedicationNumber) {
        Medication Medication = medications.get(MedicationNumber);
        String info = Medication.getName();
        String datetime = Medication.nextConsumption().toString();

        TextView medicationNextTime = MedicationViewHolder.medicationNextTime;
        medicationNextTime.setId(MedicationNumber + 1);
        medicationNextTime.setText(datetime);
        medicationNextTime.setTag(MedicationNumber);

        TextView medicationName = MedicationViewHolder.medicationName;
        medicationName.setId(MedicationNumber);
        medicationName.setText(info);
        medicationName.setTag(MedicationNumber);

        Button arrow = MedicationViewHolder.arrow;
        arrow.setTag(MedicationNumber);
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    public static class MedicationViewHolder extends RecyclerView.ViewHolder {

        TextView medicationNextTime;
        TextView medicationName;
        Button arrow;

        public MedicationViewHolder(@NonNull View itemView, final OnUserClickListener listener) {
            super(itemView);
            medicationNextTime = itemView.findViewById(R.id.next_consumption);
            medicationName = itemView.findViewById(R.id.medication_name);
            arrow = itemView.findViewById(R.id.arrow);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onUserClick(position);
                    }
                }
            });
        }
    }
}

