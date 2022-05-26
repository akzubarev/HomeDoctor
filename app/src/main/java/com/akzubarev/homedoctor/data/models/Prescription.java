package com.akzubarev.homedoctor.data.models;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Prescription extends BaseModel {
    private String name = "";
    private String endDate = "02.05.2022";
    private String diagnosis = "";
    private boolean autoDisable = true;

    public Prescription() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public boolean ended() {
        String[] date = getEndDate().split("\\.");
        int day = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]) - 1; //starts with 0
        int year = Integer.parseInt(date[2]);

        Calendar calendar = Calendar.getInstance(new Locale("ru"));
        calendar.set(Calendar.DAY_OF_MONTH, day);

        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
//        Log.d("Name ", name);
//        Log.d("Prescription end ", String.valueOf(calendar.getTime()));
        Boolean result = calendar.getTime().compareTo(new Date()) < 0;
        Log.d(String.format("Prescription %s is ended", name), String.valueOf(result));
        return result;
    }

    public boolean getAutoDisable() {
        return autoDisable;
    }

    public void setAutoDisable(boolean autoDisable) {
        this.autoDisable = autoDisable;
    }

    @Override
    public boolean validate() {
        return !name.equals("") && !endDate.equals("")
                && !diagnosis.equals("");
    }

    @Exclude
    public String getEndedMessage() {
        return String.format("Схема лечения %s закончилась\n", name);
    }
}
