package com.akzubarev.homedoctor.ui.fragments.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.adapters.ProfilesAdapter;
import com.akzubarev.homedoctor.data.handlers.FireBaseHandler;
import com.akzubarev.homedoctor.data.models.Profile;
import com.akzubarev.homedoctor.databinding.FragmentHomeBinding;
import com.akzubarev.homedoctor.databinding.FragmentProfilesListBinding;

import java.util.ArrayList;

public class ProfilesListFragment extends Fragment {
    private FireBaseHandler dataHandler;
    private FragmentProfilesListBinding binding;
    ArrayList<Profile> profiles = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfilesListBinding.inflate(inflater, container, false);
        dataHandler = new FireBaseHandler(getContext());//DataHandler.getInstance(getContext());
        dataHandler.getProfiles(this::fill);

        NavController navController = NavHostFragment.findNavController(this);
        binding.fab.setOnClickListener(view -> navController.navigate(R.id.ProfileFragment));
        return binding.getRoot();
    }


    private void fill(ArrayList<Profile> profilesData) {
        profiles = profilesData;
        RecyclerView userList = binding.profilesList;
        userList.setHasFixedSize(true);
        userList.addItemDecoration(new DividerItemDecoration(
                userList.getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager userLayoutManager = new LinearLayoutManager(getContext());

        ProfilesAdapter profilesAdapter = new ProfilesAdapter(profiles, getContext());
        userList.setLayoutManager(userLayoutManager);
        userList.setAdapter(profilesAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        dataHandler.saveProfiles(profiles);
    }
}