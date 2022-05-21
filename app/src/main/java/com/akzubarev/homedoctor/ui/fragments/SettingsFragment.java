package com.akzubarev.homedoctor.ui.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.databinding.FragmentSettingsBinding;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";
    private FragmentSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        DataHandler dataHandler = DataHandler.getInstance(getContext());
        dataHandler.getShortageSettings((method, value) -> {
            binding.shortageMethod.setText(method);
            binding.shortageValue.setText(Integer.toString(value));
        });

        dataHandler.getExpirySettings((timeframe, value) -> {
            ArrayList<String> options = new ArrayList<>(Arrays.asList("системная", "темная", "светлая"));//(Arrays.asList(getResources().getStringArray(R.array.theme_dropdown)));
            binding.expiryTimeframe.setSelection(options.indexOf(timeframe));
            binding.expiryValue.setText(Integer.toString(value));
        });

        dataHandler.getMorningSettings(morning -> binding.morning.setText(morning));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        configureSpinner(binding.themeSpinner, R.array.theme_dropdown, (int choice) -> {
//            String[] options = getResources().getStringArray(R.array.theme_dropdown);
//            String theme;
////            saveSettings();
//            switch (choice) {
//                case 0:
//                    theme = "system";
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//                    break;
//                case 1:
//                    theme = "dark";
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                    break;
//                case 2:
//                    theme = "light";
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                    break;
//
//            }
//        }, 0);

    }

    void saveSettings() {
        DataHandler dataHandler = DataHandler.getInstance(getContext());
        String morningTime = binding.morning.getText().toString();
        String expireTimeFrame = binding.expiryTimeframe.getSelectedItem().toString();
        int expiryValue = Integer.parseInt(binding.expiryValue.getText().toString());

        String shortageMethod = binding.shortageMethod.getText().toString();
        int shortageValue = Integer.parseInt(binding.shortageValue.getText().toString());
        dataHandler.saveSettings(morningTime,
                expireTimeFrame, expiryValue,
                shortageMethod, shortageValue
        );

    }


    private void configureSpinner(Spinner spinner, int arrayID, SpinnerCallback callback, int choice_index) {
        String[] state = getResources().getStringArray(arrayID);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_row, R.id.spinner_row_text, state);
        spinner.setGravity(Gravity.END);
        adapter.setDropDownViewResource(R.layout.spinner_row_unfolded);

        spinner.setAdapter(adapter);
        spinner.setSelection(choice_index);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                callback.onCallback(selectedItemPosition);
                saveSettings();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
//        int offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -80, getResources().getDisplayMetrics());
//        spinner.setDropDownHorizontalOffset(offset);

    }

    public interface SpinnerCallback {
        void onCallback(int choice);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_info).setVisible(true);
        menu.findItem(R.id.action_settings).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_info) {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.InfoFragment);
            return true;
        } else
            return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onStop() {
        saveSettings();
        super.onStop();
    }
}
