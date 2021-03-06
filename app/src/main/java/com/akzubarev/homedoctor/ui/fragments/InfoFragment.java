package com.akzubarev.homedoctor.ui.fragments;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.BuildConfig;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.databinding.FragmentInfoBinding;

import java.util.Objects;

public class InfoFragment extends Fragment {

    private static final String TAG = "InfoFragment";
    private FragmentInfoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            Activity activity = getActivity();
            PackageManager pcm = Objects.requireNonNull(activity).getPackageManager();
            String versionName = pcm.getPackageInfo(activity.getPackageName(), 0).versionName;
            TextView versionTV = binding.version;
            String versionTag = "";
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                versionTag = " (beta)";
            String versionText = String.format("Версия: %s%s", versionName, versionTag).trim();
            versionTV.setText(versionText);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_info).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        return super.onOptionsItemSelected(menuItem);
    }
}
