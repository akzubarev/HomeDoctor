package com.akzubarev.homedoctor.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class RemindTimeAdapter
        extends RecyclerView.Adapter<RemindTimeAdapter.RemindTimeViewHolder> {

    private final ArrayList<String> dates;
    private Context context;
    private final ArrayList<RemindTimeViewHolder> viewholders = new ArrayList<>();

    public Context getContext() {
        return context;
    }

    public boolean contains(String time) {
        return dates.contains(time);
    }

    public void addTime(String time) {
        dates.add(time);
        notifyItemInserted(dates.size() - 1);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public RemindTimeAdapter(ArrayList<String> dates, Context context) {
        this.dates = dates;
        this.context = context;
    }

    public ArrayList<String> gatherTreatments() {
        ArrayList<String> result = new ArrayList<>();
        for (RemindTimeViewHolder vh : viewholders) {
            result.add(vh.remindTime.getText().toString());
        }
        return result;
    }

    @NonNull
    @Override
    public RemindTimeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.block_remind_time, viewGroup, false);
        return new RemindTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RemindTimeViewHolder dateViewHolder, int dateNumber) {
        String date = dates.get(dateNumber);
        TextView datetime = dateViewHolder.remindTime;
        datetime.setText(date);
        dateViewHolder.deletionCallback = this::deleteItem;
        viewholders.add(dateViewHolder);
    }

    private void deleteItem(int position) {
        dates.remove(position);
        viewholders.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dates.size());
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public static class RemindTimeViewHolder extends RecyclerView.ViewHolder {
        handleDeletion deletionCallback;
        TextView remindTime;

        public RemindTimeViewHolder(@NonNull View itemView) {
            super(itemView);
            remindTime = itemView.findViewById(R.id.remind_time);
            remindTime.setOnClickListener(this::reminderDropDown);
        }

        public void reminderDropDown(View v) {
            TimePicker timePicker = (TimePicker) TimePicker.inflate(v.getContext(),
                    R.layout.selector_time, null);
//        timePicker.setIs24HourView(StringFormat.is24HourFormat(context));
            timePicker.setIs24HourView(true);

            new AlertDialog.Builder(v.getContext())
                    .setView(timePicker)
                    .setPositiveButton("????", (dialog1, which) ->
                            {
                                String text = timeFromPicker(timePicker.getHour(), timePicker.getMinute());
                                remindTime.setText(text);
                            }
                    ).setNeutralButton("????????????", (dialog1, which) -> {
                    }).setNegativeButton("?????????????? ??????????????????????", (dialog1, which) -> deletionCallback.deleteItemFromRV(getAdapterPosition()))
                    .show();
        }

        private String timeFromPicker(int hour, int minute) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", new Locale("ru", "ru"));
            return sdf.format(calendar.getTime());
        }

        interface handleDeletion {
            void deleteItemFromRV(int position);
        }

    }


}

