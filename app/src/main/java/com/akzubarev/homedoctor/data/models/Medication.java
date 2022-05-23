package com.akzubarev.homedoctor.data.models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Medication extends BaseModel {

    private String name;
    private String medicationStatsID;
    private int amount = 1;
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
        medicationStatsID = name;
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

    //endregion
    public void addAllowedProfile(String allowed_profile) {
        this.allowedProfiles.put(allowed_profile, true);
    }

    @Exclude
    public String getExpiryMessage() {
        return String.format("%s expires at %s", name, expiryDate);
    }

    @Exclude
    public boolean doesExpire(int daysToExpire) {
        String[] dates = expiryDate.split("\\.");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(dates[2]));
        calendar.set(Calendar.MONTH, Integer.parseInt(dates[1]));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dates[0]));

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.add(Calendar.DAY_OF_YEAR, daysToExpire);
        return calendar.before(expirationDate);
    }

    @Exclude
    public boolean isShortage(String method, int value, ArrayList<Treatment> treatments) {
        boolean result = false;
        switch (method) {
            case "Количество":
                result = amount <= value;
                break;
            case "По неделям":
                int sum = 0;
                for (Treatment treatment : treatments)
                    sum += treatment.getAmount();
                result = amount <= sum * value;
                break;
        }
        return result;
    }

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
}