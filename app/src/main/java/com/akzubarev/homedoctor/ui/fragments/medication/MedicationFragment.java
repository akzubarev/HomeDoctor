package com.akzubarev.homedoctor.ui.fragments.medication;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.databinding.FragmentMedicationBinding;

public class MedicationFragment extends Fragment {

    private MedicationViewModel medicationViewModel;
    private FragmentMedicationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        medicationViewModel =
                new ViewModelProvider(this).get(MedicationViewModel.class);

        binding = FragmentMedicationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        boolean create_mode = savedInstanceState.getBoolean("create", true);
        if (create_mode) {
//            Button b = findViewById(R.id.id);
        } else {
            EditText nameEditText = binding.nameEditText;
            EditText durationEditText = binding.durationEditText;
            EditText frequencyEditText = binding.frequencyEditText;
            Spinner durationSpinner = binding.durationSpinner;

        }
        configureSpinner(binding.durationSpinner, R.array.duration_dropdown, (int choice) -> {
            String[] options = getResources().getStringArray(R.array.duration_dropdown);
            String duration = options[choice];
//            DataReader.SaveInt(goal_minutes, "", getContext());
        }, 0); // DataReader.GetInt(DataReader.GOAL, getContext()) / 5 - 1);

        return view;
    }


    public interface SpinnerCallback {
        void onCallback(int choice);
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
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected,
                                       int selectedItemPosition,
                                       long selectedId) {
                callback.onCallback(selectedItemPosition);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
//        int offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -80, getResources().getDisplayMetrics());
//        spinner.setDropDownHorizontalOffset(offset);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}