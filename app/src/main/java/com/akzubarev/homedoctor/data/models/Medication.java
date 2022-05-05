package com.akzubarev.homedoctor.data.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Medication extends BaseModel {

    private String name;
    private String medicationStatsID;
    private int amount = 1;
    private String expiryDate = "";
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

    @Override
    @Exclude
    public String getDBID() {
        return getName();
    }

}