package com.akzubarev.homedoctor.data.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.ui.notifications.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RemindTimeAdapter
        extends RecyclerView.Adapter<RemindTimeAdapter.DateViewHolder> {

    private ArrayList<Date> dates;
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

    public RemindTimeAdapter(ArrayList<Date> dates, Context context) {
        this.dates = dates;
        this.context = context;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.block_remind_time, viewGroup, false);
        return new DateViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder dateViewHolder, int dateNumber) {
        Date date = dates.get(dateNumber);
        String text = String.format("Напоминание №%d", dateNumber);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.US);
        String datetime = sdf.format(date);

        TextView remindText = dateViewHolder.remindText;
        remindText.setText(text);

        TextView dateName = dateViewHolder.remindTime;
//        dateName.setText(datetime);
    }


    @Override
    public int getItemCount() {
        return dates.size();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {

        TextView remindText;
        TextView remindTime;

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void reminderDropDown(View v) {
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

        @RequiresApi(api = Build.VERSION_CODES.M)
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

        public DateViewHolder(@NonNull View itemView, final OnUserClickListener listener) {
            super(itemView);
            remindText = itemView.findViewById(R.id.remind_text);
            remindTime = itemView.findViewById(R.id.remind_time);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                remindTime.setOnClickListener(this::reminderDropDown);
            }
        }

    }


}

