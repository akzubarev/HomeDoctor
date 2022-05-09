package com.akzubarev.homedoctor.data.models;

import com.google.firebase.database.Exclude;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

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
    public String getDBID() {
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
    public Calendar getAbsoluteTime() {
        String[] time = getTime().split(":");
        String day = getDay();
        int hours = Integer.parseInt(time[0]);
        int minutes = Integer.parseInt(time[1]);
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.DAY_OF_WEEK, DayOfWeek.MONDAY);
//        calendar.set(Calendar.HOUR_OF_DAY, );
//        calendar.set(Calendar.MINUTE, );
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, Arrays.asList(days).indexOf(day));
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 7);
        return calendar;
    }
    @Exclude
    public String getNotification() {
        return getDBID();
    }
}
