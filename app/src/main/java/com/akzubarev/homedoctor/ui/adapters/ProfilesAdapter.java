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

public class ProfilesAdapter
        extends RecyclerView.Adapter<ProfilesAdapter.ProfileViewHolder> {

    private ArrayList<Profile> profiles;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ProfilesAdapter(ArrayList<Profile> profiles, Context context) {
        this.profiles = profiles;
        this.context = context;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.block_profile, viewGroup, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder userViewHolder, int UserNumber) {
        Profile profile = profiles.get(UserNumber);

        TextView userName = userViewHolder.profileName;
        userName.setText(profile.getName());

        userViewHolder.itemView.setOnClickListener(v -> {
                    NavController navController = Navigation.findNavController(userViewHolder.itemView);
                    Bundle bundle = new Bundle();
                    bundle.putString("Profile", profile.getDbID());
                    Log.d("PROFILE", profile.getDbID());
                    navController.navigate(R.id.ProfileFragment, bundle);
                }
        );

        userViewHolder.setNextTime(profile);
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {

        TextView profileName;
        TextView nextTreatmentTime;
        TextView nextTreatmentMed;
        ImageView alarm;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            nextTreatmentTime = itemView.findViewById(R.id.next_consumption);
            nextTreatmentMed = itemView.findViewById(R.id.medication_name);
            profileName = itemView.findViewById(R.id.profile_name);
            alarm = itemView.findViewById(R.id.alarm);
        }

        public void setNextTime(Profile profile) {
            DataHandler dataHandler = DataHandler.getInstance(itemView.getContext());
            dataHandler.findNextReminderForProfile(profile.getDbID(),
                    (Treatment treatment) -> {
                        nextTreatmentTime.setText(treatment.getDateTime());
                        dataHandler.getMedication(treatment.getMedicationId(), (Medication med) -> nextTreatmentMed.setText(med.getName()));

                    },
                    () -> {
                        nextTreatmentTime.setText("");
                        nextTreatmentMed.setText("");
                        alarm.setImageResource(R.drawable.ic_alarm_off);
                    });
        }
    }
}

