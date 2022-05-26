package com.akzubarev.homedoctor.data.models;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Medication extends BaseModel {

    private String name;
    private String medicationStatsID;
    private int amount = 0;
    private String expiryDate = "";
    private boolean reminders = true;
    private Map<String, Boolean> allowedProfiles = new HashMap<>();


    //region constructors

    public Medication(String name, String medicationStatsID, int amount, String expiryDate, Map<String, Boolean> allowedProfiles) {
        this.name = name;
        this.medicationStatsID = medicationStatsID;
        this.amount = amount;
        this.expiryDate = expiryDate;
        this.allowedProfiles = allowedProfiles;
    }

    public Medication(String name, String medicationStatsID) {
        this.name = name;
        this.medicationStatsID = medicationStatsID;
    }

    public Medication() {
    }


    //endregion

    //region setters
    public void setMedicationStatsID(String medicationStatsID) {
        this.medicationStatsID = medicationStatsID;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setAllowedProfiles(Map<String, Boolean> allowedProfiles) {
        this.allowedProfiles = allowedProfiles;
    }


    public void setName(String name) {
        this.name = name;
    }

    //endregion

    //region getters
    public int getAmount() {
        return amount;
    }


    public String getName() {
        return name;
    }

    public String getMedicationStatsID() {
        return medicationStatsID;
    }

    public Map<String, Boolean> getAllowedProfiles() {
        return allowedProfiles;
    }

    @Exclude
    public String getExpiryMessage() {
        return String.format("У %s истекает срок годности (%s)", name, expiryDate);
    }

    @Exclude
    public boolean doesExpire(String timeframe, int value) {
        int daysToExpire;
        switch (timeframe) {
            case "дни":
            default:
                daysToExpire = value;
                break;
            case "недели":
                daysToExpire = value * 7;
                break;
            case "месяцы":
                daysToExpire = value * 30;
                break;
        }
        String[] dates = expiryDate.split("\\.");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(dates[2]));
        calendar.set(Calendar.MONTH, Integer.parseInt(dates[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dates[0]));


        Calendar expirationDate = Calendar.getInstance();
        expirationDate.add(Calendar.DAY_OF_YEAR, daysToExpire);
        Log.d("EXPIRY " + name, String.valueOf(calendar.getTime()));
        Log.d("EXPIRY " + name, String.valueOf(expirationDate.getTime()));
        Log.d("EXPIRY " + name, String.valueOf(calendar.before(expirationDate)));
        return calendar.before(expirationDate);
    }

    @Exclude
    public boolean isShortage(String method, int value, ArrayList<Treatment> treatments) {
        int threshold = 0;
        switch (method) {
            case "Количество":
                threshold = value;
                break;
            case "По неделям":
                int sum = 0;
                for (Treatment treatment : treatments)
                    sum += treatment.getAmount();
                threshold = sum * value;
                break;
        }
        Log.d("SHORTAGE threshold", String.valueOf(threshold));
        return amount <= threshold;
    }

    @Override
    public boolean validate() {
        return !name.equals("") && !medicationStatsID.equals("")
                && !expiryDate.equals("");
    }

    public boolean getReminders() {
        return reminders;
    }

    public void setReminders(boolean reminders) {
        this.reminders = reminders;
    }

    public void take() {
        amount = Math.max(--amount, 0);
    }

    @Exclude
    public String getShortageMessage() {
        return String.format(new Locale("ru", "ru"), "Заканчивается %s, осталось на %d приемов\n", name, amount);
    }
}