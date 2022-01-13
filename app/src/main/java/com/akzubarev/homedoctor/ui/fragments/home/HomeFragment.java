package com.akzubarev.homedoctor.ui.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.User;
import com.akzubarev.homedoctor.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        fill();
        return binding.getRoot();
    }


    private void fill() {
        RecyclerView userList = binding.userList;
        userList.setHasFixedSize(true);
        userList.addItemDecoration(new DividerItemDecoration(
                userList.getContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager userLayoutManager = new LinearLayoutManager(getContext());

        DataHandler handler = DataHandler.getInstance();
        UserAdapter userAdapter = new UserAdapter(handler.getUsers(), getContext());
        userList.setLayoutManager(userLayoutManager);
        userList.setAdapter(userAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}