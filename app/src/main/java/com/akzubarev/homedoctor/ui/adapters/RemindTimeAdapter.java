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

public class RemindTimeAdapter
        extends RecyclerView.Adapter<RemindTimeAdapter.RemindTimeViewHolder> {

    private ArrayList<String> dates = new ArrayList<>();
    private OnUserClickListener listener;
    private Context context;
    private ArrayList<RemindTimeViewHolder> viewholders = new ArrayList<>();

    public Context getContext() {
        return context;
    }

    public void addTime(String time) {
        dates.add(time);
        notifyItemInserted(dates.size() - 1);
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
        return new RemindTimeViewHolder(view, listener);
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

        public RemindTimeViewHolder(@NonNull View itemView, final OnUserClickListener listener) {
            super(itemView);
            remindTime = itemView.findViewById(R.id.remind_time);
            remindTime.setOnClickListener(this::reminderDropDown);
        }

        public void reminderDropDown(View v) {
            TimePicker timePicker = (TimePicker) TimePicker.inflate(v.getContext(),
                    R.layout.time_selector, null);
//        timePicker.setIs24HourView(StringFormat.is24HourFormat(context));
            timePicker.setIs24HourView(true);

            AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                    .setTitle("Введите время напоминания").setView(timePicker)
                    .setPositiveButton("Ок", (dialog1, which) ->
                            {
                                String text = timeFromPicker(timePicker.getHour(), timePicker.getMinute());
                                remindTime.setText(text);
                            }
                    ).setNeutralButton("Отмена", (dialog1, which) -> {
                    }).setNegativeButton("Удалить напоминание", (dialog1, which) -> {
                        deletionCallback.deleteItemFromRV(getAdapterPosition());
                    }).show();
        }

        private String timeFromPicker(int hour, int minute) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(calendar.getTime());
        }

        interface handleDeletion {
            void deleteItemFromRV(int position);
        }

    }


}

