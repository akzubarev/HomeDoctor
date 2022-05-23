package com.akzubarev.homedoctor.data.models;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Treatment extends BaseModel {
    private String medicationId;
    private String prescriptionId;
    private String profileID;
    private String time;
    private String day;
    private int amount;
    @Exclude
    private final static String[] days = new String[]{"Понедельник", "Вторник", "Среда",
            "Четверг", "Пятница", "Суббота", "Воскресенье"};

    public String getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(String medicationId) {
        this.medicationId = medicationId;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Treatment(String medicationId, String prescriptionId, String profileID, String day, String time, int amount) {
        this.medicationId = medicationId;
        this.prescriptionId = prescriptionId;
        this.profileID = profileID;
        this.day = day;
        this.time = time;
        this.amount = amount;
    }

    public Treatment() {
    }

    @Override
    @Exclude
    public String getDbID() {
        return String.format("%s | %s | %s | %s | %s", medicationId, profileID, day, time, prescriptionId);
    }

    public String getProfileID() {
        return profileID;
    }

    public void setProfileID(String profileID) {
        this.profileID = profileID;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Exclude
    public String getDateTime() {
        Calendar calendar = getAbsoluteTime();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM HH:mm", new Locale("ru"));
        return format.format(calendar.getTime());
    }

    @Exclude
    public Calendar getAbsoluteTime() {
        String[] time = getTime().split(":");
        String day = getDay();
        int hours = Integer.parseInt(time[0]);
        int minutes = Integer.parseInt(time[1]);

        Calendar calendar = Calendar.getInstance(new Locale("ru"));
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeekConversion(day));
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 7);
        return calendar;
    }

    private int dayOfWeekConversion(String day) {
        int num = Arrays.asList(days).indexOf(day) + 1;
        num++; //fist day of the week is Sunday
        if (num == 8)
            num = 1;
        return num;
    }

    @Exclude
    public String getNotification() {
        return getDbID();
    }
}
