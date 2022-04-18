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
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class UserAdapter
        extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<User> users;
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

    public UserAdapter(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.block_user, viewGroup, false);
        return new UserViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder UserViewHolder, int UserNumber) {
        User User = users.get(UserNumber);
        String info = User.getName();
//        Medication nextConsumption = User.nextConsumption();
//        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm dd.MM");
//        String nextConsumptionText = String.format("%s %s",
//                nextConsumption.getName(), sdf.format(nextConsumption.nextConsumption()));

        TextView userNextTime = UserViewHolder.userNextTime;
//        userNextTime.setText(nextConsumptionText);

        TextView userName = UserViewHolder.userName;
        userName.setText(info);

        Button arrow = UserViewHolder.arrow;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView userNextTime;
        TextView userName;
        Button arrow;
        private Context context;

        public UserViewHolder(@NonNull View itemView, final OnUserClickListener listener) {
            super(itemView);
            userNextTime = itemView.findViewById(R.id.next_consumption);
            userName = itemView.findViewById(R.id.user_name);
            arrow = itemView.findViewById(R.id.arrow);
            context = itemView.getContext();


            int position = getAdapterPosition();
            if (listener == null) {
                itemView.setOnClickListener(v -> {
                            NavController navController = Navigation.findNavController(itemView);
                            Bundle bundle = new Bundle();
                            bundle.putInt("User", position);
                            navController.navigate(R.id.MedicationsListFragment, bundle);
                        }
                );
            }
        }
    }
}

