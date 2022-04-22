package com.akzubarev.homedoctor.data.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.Treatment;
import com.akzubarev.homedoctor.ui.notifications.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class TreatmentTimeAdapter
        extends RecyclerView.Adapter<TreatmentTimeAdapter.DateViewHolder> {

    private ArrayList<Treatment> treatments;
    private ArrayList<Medication> medications;
    private OnUserClickListener listener;
    private Context context;
    NavController navController;

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

    public TreatmentTimeAdapter(ArrayList<Treatment> treatments, ArrayList<Medication> medications, Activity activity) {
        this.treatments = treatments;
        this.medications = medications;
        this.context = activity;
        this.navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.block_medication_prescription, viewGroup, false);
        return new DateViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder viewHolder, int medicationNumber) {
        Medication Medication = medications.get(medicationNumber);

        TextView medicationName = viewHolder.medicationName;
        medicationName.setText(Medication.getName());

        TextView medicationNextTime = viewHolder.medicationNextTime;
//        medicationNextTime.setText(Medication.nextConsumption().toString());
//        viewHolder.itemView.setOnClickListener(v -> {
//                    int position = viewHolder.getAdapterPosition();
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("User", position);
//                    bundle.putString("Medication", medications.get(position).getName());
//                    navController.navigate(R.id.MedicationFragment, bundle);
//                }
//        );

    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {

        TextView remindDay;
        TextView remindTime;
        TextView medicationNextTime;
        TextView medicationName;
        LinearLayout treatmentLayout;
        ImageButton addButton;
        HashMap<String, RecyclerView> rvs = new HashMap<>();
        HashMap<String, RemindTimeAdapter> adapters = new HashMap<>();
        ArrayList<Integer> selectedItems = new ArrayList<>();
        String[] items;
        String[] days_abbr = new String[]{"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
        HashMap<String, String> decoder = new HashMap<>();

        public DateViewHolder(@NonNull View itemView, final OnUserClickListener listener) {
            super(itemView);
            remindTime = itemView.findViewById(R.id.remind_time);
            remindDay = itemView.findViewById(R.id.remind_day);
            addButton = itemView.findViewById(R.id.remind_add_button);
            treatmentLayout = itemView.findViewById(R.id.treatment_layout);

            items = itemView.getContext().getResources().getStringArray(R.array.reminder_day_options);
            medicationNextTime = itemView.findViewById(R.id.next_consumption);
            medicationName = itemView.findViewById(R.id.medication_name);

            remindTime.setOnClickListener(this::reminderTimeDropDown);
            remindDay.setOnClickListener(this::reminderDayDropDown);
            addButton.setOnClickListener(this::addTreatments);

            rvs.put("Понедельник", itemView.findViewById(R.id.remind_list));
            selectedItems.add(0);
            remindDay.setText("Понедельник");

            for (int i = 1; i < items.length; i++) {
                LinearLayout lyt = (LinearLayout) View.inflate(itemView.getContext(), R.layout.block_treatment, treatmentLayout);
                RecyclerView rv = lyt.findViewById(R.id.remind_list); // TODO: that somehow is always the same layout instead of copies
                Button bt = lyt.findViewById(R.id.day_button);
                bt.setText(days_abbr[i]);
                rvs.put(items[i], rv);
                decoder.put(days_abbr[i], items[i]);
            }

            DataHandler.getInstance(medicationName.getContext()).getTreatments(this::fillTreatments, "prescriptionID");
        }

        private void configureRecyclerView(RecyclerView rv) {
            rv.setHasFixedSize(false);
            rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
            LinearLayoutManager lm = new LinearLayoutManager(rv.getContext());
            rv.setLayoutManager(lm);
        }

        private void fillTreatments(ArrayList<Treatment> treatments) {
            for (String key : rvs.keySet()) {
                ArrayList<String> times = new ArrayList<>();
                times.add("13:00");
                RemindTimeAdapter adapter = new RemindTimeAdapter(times, itemView.getContext());
                adapters.put(key, adapter);

                RecyclerView day = rvs.get(key);
                configureRecyclerView(day);
                day.setAdapter(adapter);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void reminderDayDropDown(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            boolean[] checked = new boolean[]{false, false, false, false, false, false, false,};
            for (int selectedItem : selectedItems)
                checked[selectedItem] = true;

            ArrayList<Integer> selectedCopy = new ArrayList<>(selectedItems);
            AlertDialog reminderDayDialog = builder.setMultiChoiceItems(items, checked
                    , (dialog, indexSelected, isChecked) -> {
                        if (isChecked) {
                            selectedItems.add(indexSelected);
                        } else if (selectedItems.contains(indexSelected)) {
                            selectedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }).setPositiveButton("OK", (dialog, id) -> {
                StringBuilder text = new StringBuilder();
                if (selectedItems.size() == 1)
                    text.append("Каждый день");
                else if (selectedItems.size() == 7)
                    text.append(days_abbr[selectedItems.get(0)]);
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
                String day = items[selection];
                RemindTimeAdapter adapter = adapters.get(day);
                adapter.addTime(time);
            }
        }

        public void reminderTimeDropDown(View v) {
            TimePicker timePicker = (TimePicker) TimePicker.inflate(v.getContext(),
                    R.layout.time_selector, null);
//        timePicker.setIs24HourView(DateFormat.is24HourFormat(context));
            timePicker.setIs24HourView(true);

            AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                    .setTitle("Введите время напоминания").setView(timePicker)
                    .setPositiveButton("Ок", (dialog1, which) ->
                            setAlarm(timePicker.getHour(), timePicker.getMinute())
                    ).setNegativeButton("Отмена", (dialog1, which) -> {
                    })
                    .show();
        }

        private void setAlarm(int hour, int minute) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String timetext = sdf.format(calendar.getTime());
            remindTime.setText(timetext);
//        DataReader.SaveString(timetext, DataReader.REMINDER_TIME, context);

            new NotificationHelper(remindTime.getContext()).setReminder(hour, minute, NotificationHelper.MAKE);
        }


    }


}

