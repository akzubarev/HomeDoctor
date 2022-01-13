package com.akzubarev.homedoctor.ui.fragments.medication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akzubarev.homedoctor.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        dateName.setText(datetime);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {

        TextView remindText;
        TextView remindTime;

        public DateViewHolder(@NonNull View itemView, final OnUserClickListener listener) {
            super(itemView);
            remindText = itemView.findViewById(R.id.remind_text);
            remindTime = itemView.findViewById(R.id.remind_time);

            remindTime.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onUserClick(position);
                    }
                }
            });
        }
    }
}

