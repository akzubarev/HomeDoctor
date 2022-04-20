package com.akzubarev.homedoctor.data.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.models.Profile;
import com.akzubarev.homedoctor.data.models.User;

import java.util.ArrayList;

public class ProfilesAdapter
        extends RecyclerView.Adapter<ProfilesAdapter.ProfileViewHolder> {

    private ArrayList<Profile> profiles;
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

    public ProfilesAdapter(ArrayList<Profile> profiles, Context context) {
        this.profiles = profiles;
        this.context = context;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.block_user, viewGroup, false);
        return new ProfileViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder UserViewHolder, int UserNumber) {
        Profile profile = profiles.get(UserNumber);
        String info = profile.getName();
//        Medication nextConsumption = User.nextConsumption();
//        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm dd.MM");
//        String nextConsumptionText = String.format("%s %s",
//                nextConsumption.getName(), sdf.format(nextConsumption.nextConsumption()));

        TextView userNextTime = UserViewHolder.profileNextTime;
//        userNextTime.setText(nextConsumptionText);

        TextView userName = UserViewHolder.profileName;
        userName.setText(info);

        Button arrow = UserViewHolder.arrow;

        if (listener == null) {
            UserViewHolder.itemView.setOnClickListener(v -> {
                        NavController navController = Navigation.findNavController(UserViewHolder.itemView);
                        Bundle bundle = new Bundle();
                        bundle.putString("Profile", profile.getDBID());
                        navController.navigate(R.id.ProfileFragment, bundle);
                    }
            );
        }
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {

        TextView profileNextTime;
        TextView profileName;
        Button arrow;

        public ProfileViewHolder(@NonNull View itemView, final OnUserClickListener listener) {
            super(itemView);
            profileNextTime = itemView.findViewById(R.id.next_consumption);
            profileName = itemView.findViewById(R.id.profile_name);
            arrow = itemView.findViewById(R.id.arrow);
        }
    }
}

