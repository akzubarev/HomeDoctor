package com.akzubarev.homedoctor.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.databinding.FragmentSettingsBinding;
import com.akzubarev.homedoctor.ui.notifications.NotificationHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";
    private FragmentSettingsBinding binding;
    boolean working = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        working = true;
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        DataHandler dataHandler = DataHandler.getInstance(getContext());

        dataHandler.getShortageSettings((method, value) -> {
            if (working) {
                ArrayList<String> options = new ArrayList<>(Arrays.asList("Количество", "По неделям"));
                binding.shortageMethod.setSelection(options.indexOf(method));
                binding.shortageMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                        if (selectedItemPosition == 0)
                            binding.shortageText.setText("Оставшиеся приемы");
                        else
                            binding.shortageText.setText("Недель до конца приемов");
                        saveSettings();
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                binding.shortageValue.setText(Integer.toString(value));
            }
        });

        dataHandler.getExpirySettings((timeframe, value) -> {
            ArrayList<String> options = new ArrayList<>(Arrays.asList("дни", "недели", "месяцы"));
            binding.expiryTimeframe.setSelection(options.indexOf(timeframe));
            binding.expiryValue.setText(Integer.toString(value));
        });

        dataHandler.getMorningSettings(morning -> binding.morning.setText(morning));
        dataHandler.getControlSettings(control -> binding.control.setChecked(control));


        binding.morning.setOnClickListener((View v) -> morningSelector());
        binding.exit.setOnClickListener((View v) -> logOut());
        return binding.getRoot();
    }

    private void morningSelector() {
        TimePicker timePicker = (TimePicker) TimePicker.inflate(getContext(), R.layout.selector_time, null);
        timePicker.setIs24HourView(true);

        new AlertDialog.Builder(getContext()).setView(timePicker)
                .setPositiveButton("Ок", (dialog1, which) ->
                        {
                            String text = timeFromPicker(timePicker.getHour(), timePicker.getMinute());
                            binding.morning.setText(text);
                        }
                ).setNeutralButton("Отмена", (dialog1, which) -> dialog1.dismiss())
                .show();
    }

    private String timeFromPicker(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", new Locale("ru", "ru"));
        return sdf.format(calendar.getTime());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    void saveSettings() {
        DataHandler dataHandler = DataHandler.getInstance(getContext());
        String morningTime = binding.morning.getText().toString();
        String expireTimeFrame = binding.expiryTimeframe.getSelectedItem().toString();
        int expiryValue = Integer.parseInt(binding.expiryValue.getText().toString());

        String shortageMethod = binding.shortageMethod.getSelectedItem().toString();
        int shortageValue = Integer.parseInt(binding.shortageValue.getText().toString());
        Boolean control = binding.control.isChecked();
        dataHandler.saveSettings(morningTime, control,
                expireTimeFrame, expiryValue,
                shortageMethod, shortageValue,
                () -> {
                    NotificationHelper notificationHelper = new NotificationHelper(getContext());
                    notificationHelper.updateMorning();
                }
        );

    }

    private void logOut() {
        NavController navController = NavHostFragment.findNavController(this);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        working = false;
        navController.navigate(R.id.SignInFragment);
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
        if (working)
            saveSettings();
        working = false;
        super.onStop();
    }
}
