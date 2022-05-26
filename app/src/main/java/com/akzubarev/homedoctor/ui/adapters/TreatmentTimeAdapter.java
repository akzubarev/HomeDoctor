package com.akzubarev.homedoctor.ui.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.Treatment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class TreatmentTimeAdapter
        extends RecyclerView.Adapter<TreatmentTimeAdapter.TreatmentViewHolder> {

    private final HashMap<String, ArrayList<Treatment>> treatments;
    private final HashMap<String, Medication> medications;
    private final HashMap<String, TreatmentViewHolder> viewholders = new HashMap<>();
    private final ArrayList<String> fixedKeyList = new ArrayList<>();
    private Context context;
    NavController navController;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void add(Medication medication) {
        if (medications.containsKey(medication.getDbID()))
            return;
        fixedKeyList.add(medication.getDbID());
        medications.put(medication.getDbID(), medication);
        treatments.put(medication.getDbID(), new ArrayList<>());
        notifyDataSetChanged();
    }

    public TreatmentTimeAdapter(HashMap<String, ArrayList<Treatment>> treatments, HashMap<String, Medication> medications, Activity activity) {
        this.treatments = treatments;
        this.medications = medications;
        fixedKeyList.addAll(medications.keySet());
        this.context = activity;
        this.navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
    }

    @NonNull
    @Override
    public TreatmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.block_medication_prescription, viewGroup, false);
        return new TreatmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TreatmentViewHolder viewHolder, int position) {
        String key = fixedKeyList.get(position);
        Medication medication = medications.get(key);
        TextView medicationName = viewHolder.medicationName;
        medicationName.setText(Objects.requireNonNull(medication).getName());

        viewHolder.fillTreatments(treatments.get(medication.getDbID()));
        viewHolder.medication_layout.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("Medication", medication.getDbID());
                    bundle.putString("MedicationStat", medication.getMedicationStatsID());
                    navController.navigate(R.id.MedicationFragment, bundle);
                }
        );
        viewholders.put(medication.getDbID(), viewHolder);
    }

    public HashMap<String, ArrayList<Pair<String, String>>> gatherTreatments() {
        HashMap<String, ArrayList<Pair<String, String>>> result = new HashMap<>();
        for (int i = 0; i < getItemCount(); i++) {
            String medKey = fixedKeyList.get(i);
            TreatmentViewHolder vh = viewholders.get(medKey);
            for (Pair<String, String> dayTime : Objects.requireNonNull(vh).gatherTreatments()) {
                if (!result.containsKey(medKey))
                    result.put(medKey, new ArrayList<>());
                Objects.requireNonNull(result.get(medKey)).add(dayTime);
            }
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    public static class TreatmentViewHolder extends RecyclerView.ViewHolder {

        private final TextView remindDay;
        private final TextView remindTime;
        private final TextView medicationName;
        private final LinearLayout medication_layout;
        private final HashMap<String, RecyclerView> rvs = new HashMap<>();
        private final HashMap<String, RemindTimeAdapter> adapters = new HashMap<>();
        private ArrayList<Integer> selectedItems = new ArrayList<>();
        private final String[] daysNames;
        private final String[] days_abbr = new String[]{"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};

        public TreatmentViewHolder(@NonNull View itemView) {
            super(itemView);
            remindTime = itemView.findViewById(R.id.remind_time);
            remindDay = itemView.findViewById(R.id.remind_day);
            ImageButton addButton = itemView.findViewById(R.id.remind_add_button);
            LinearLayout treatmentLayout = itemView.findViewById(R.id.treatment_layout);
            medication_layout = itemView.findViewById(R.id.medication_layout);
            medicationName = itemView.findViewById(R.id.medication_name);

            daysNames = itemView.getContext().getResources().getStringArray(R.array.reminder_day_options);

            remindTime.setOnClickListener(this::reminderTimeDropDown);
            remindDay.setOnClickListener(this::reminderDayDropDown);
            addButton.setOnClickListener(this::addTreatments);

//            rvs.put(daysNames[0], itemView.findViewById(R.id.remind_list));
            selectedItems.add(0);
            remindDay.setText(daysNames[0]);

            for (int i = 0; i < daysNames.length; i++) {
                LinearLayout lyt = (LinearLayout) View.inflate(itemView.getContext(), R.layout.block_treatment, null);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                );

                lyt.setLayoutParams(param);
                RecyclerView rv = lyt.findViewById(R.id.remind_list);
                ToggleButton bt = lyt.findViewById(R.id.day_button);
                bt.setText(days_abbr[i]);
                bt.setTextOn(days_abbr[i]);
                bt.setTextOff(days_abbr[i]);
//                bt.setOnClickListener();
                treatmentLayout.addView(lyt);
                rvs.put(daysNames[i], rv);
            }
        }

        private void configureRecyclerView(RecyclerView rv) {
            rv.setHasFixedSize(false);
            rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
            LinearLayoutManager lm = new LinearLayoutManager(rv.getContext());
            rv.setLayoutManager(lm);
        }

        private void fillTreatments(ArrayList<Treatment> treatments) {
            HashMap<String, ArrayList<String>> treatmentsMap = filterTreatments(treatments);
            for (String dayKey : rvs.keySet()) {
                RemindTimeAdapter adapter = new RemindTimeAdapter(treatmentsMap.get(dayKey), itemView.getContext());
                adapters.put(dayKey, adapter);

                RecyclerView dayRecyclerView = rvs.get(dayKey);
                configureRecyclerView(Objects.requireNonNull(dayRecyclerView));
                dayRecyclerView.setAdapter(adapter);
            }
        }

        private HashMap<String, ArrayList<String>> filterTreatments(ArrayList<Treatment> treatments) {
            HashMap<String, ArrayList<String>> result = new HashMap<>();
            for (String dayKey : daysNames)
                result.put(dayKey, new ArrayList<>());

            for (Treatment t : treatments)
                if (result.containsKey(t.getDay()))
                    Objects.requireNonNull(result.get(t.getDay())).add(t.getTime());

            return result;
        }

        public void reminderDayDropDown(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            boolean[] checked = new boolean[]{false, false, false, false, false, false, false,};
            for (int selectedItem : selectedItems)
                checked[selectedItem] = true;

            ArrayList<Integer> selectedCopy = new ArrayList<>(selectedItems);
            builder.setMultiChoiceItems(daysNames, checked, (dialog, indexSelected, isChecked) -> {
                if (isChecked) {
                    selectedItems.add(indexSelected);
                } else if (selectedItems.contains(indexSelected)) {
                    selectedItems.remove(Integer.valueOf(indexSelected));
                }
            }).setPositiveButton("OK", (dialog, id) -> {
                StringBuilder text = new StringBuilder();
                if (selectedItems.size() == 0)
                    text.append("Никогда");
                else if (selectedItems.size() == 1)
                    text.append(days_abbr[selectedItems.get(0)]);
                else if (selectedItems.size() == 7)
                    text.append("Каждый день");
                else
                    for (int sel : selectedItems) {
                        if (text.length() > 0)
                            text.append(", ");
                        text.append(days_abbr[sel]);
                    }
                remindDay.setText(text);
            }).setNegativeButton("Отмена", (dialog, id) -> {
                selectedItems = selectedCopy;
                dialog.dismiss();
            }).show();
        }

        private void addTreatments(View v) {
            String time = remindTime.getText().toString();
            for (int selection : selectedItems) {
                String day = daysNames[selection];
                RemindTimeAdapter adapter = adapters.get(day);
                if (!Objects.requireNonNull(adapter).contains(time))
                    adapter.addTime(time);
            }
        }

        public void reminderTimeDropDown(View v) {
            TimePicker timePicker = (TimePicker) TimePicker.inflate(v.getContext(),
                    R.layout.selector_time, null);
//        timePicker.setIs24HourView(DateFormat.is24HourFormat(context));
            timePicker.setIs24HourView(true);

            new AlertDialog.Builder(v.getContext())
                    .setView(timePicker)
                    .setPositiveButton("Ок", (dialog1, which) ->
                    {
                        String text = timeFromPicker(timePicker.getHour(), timePicker.getMinute());
                        remindTime.setText(text);
                    }).setNegativeButton("Отмена", (dialog1, which) -> {
                    }).show();
        }

        private String timeFromPicker(int hour, int minute) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", new Locale("ru", "ru"));
            return sdf.format(calendar.getTime());
        }

        public ArrayList<Pair<String, String>> gatherTreatments() {
            ArrayList<Pair<String, String>> result = new ArrayList<>();
            for (String dayKey : rvs.keySet()) {
                RemindTimeAdapter adapter = adapters.get(dayKey);
                for (String time : Objects.requireNonNull(adapter).gatherTreatments())
                    result.add(new Pair<>(dayKey, time));
            }
            return result;
        }
    }

}

