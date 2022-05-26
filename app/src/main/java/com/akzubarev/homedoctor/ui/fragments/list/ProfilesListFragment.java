package com.akzubarev.homedoctor.ui.fragments.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Profile;
import com.akzubarev.homedoctor.databinding.FragmentProfilesListBinding;
import com.akzubarev.homedoctor.ui.adapters.ProfilesAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Objects;

public class ProfilesListFragment extends Fragment {
    private FragmentProfilesListBinding binding;
    ArrayList<Profile> profiles = new ArrayList<>();
    private boolean working = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        working = true;
        binding = FragmentProfilesListBinding.inflate(inflater, container, false);
        DataHandler dataHandler = DataHandler.getInstance(getContext());
        dataHandler.getProfiles(this::fill);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        binding.email.setText(Objects.requireNonNull(user).getEmail());
        binding.id.setText(user.getUid());
        NavController navController = NavHostFragment.findNavController(this);
        binding.fab.setOnClickListener(view -> navController.navigate(R.id.ProfileFragment));

        return binding.getRoot();
    }


    private void fill(ArrayList<Profile> profilesData) {
        if (working) {
            profiles = profilesData;
            RecyclerView userList = binding.profilesList;
            userList.setHasFixedSize(true);
            LinearLayoutManager userLayoutManager = new LinearLayoutManager(getContext());

            ProfilesAdapter profilesAdapter = new ProfilesAdapter(profiles, getContext());
            userList.setLayoutManager(userLayoutManager);
            userList.setAdapter(profilesAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        NavController navController = NavHostFragment.findNavController(this);
        if (menuItem.getItemId() == R.id.action_settings) {
            navController.navigate(R.id.SettingsFragment);
            return true;
        } else
            return super.onOptionsItemSelected(menuItem);
    }


    @Override
    public void onDestroyView() {
        working = false;
        super.onDestroyView();
        binding = null;
    }
}