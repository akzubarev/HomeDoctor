package com.akzubarev.homedoctor.data.models;

import com.google.firebase.database.Exclude;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Treatment extends BaseModel {
    private String medicationId;
    private String prescriptionId;
    private String profileID;
    private String time;
    private int amount;

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

    public Treatment(String medicationId, String prescriptionId, String profileID, String time, int amount) {
        this.medicationId = medicationId;
        this.prescriptionId = prescriptionId;
        this.profileID = profileID;
        this.time = time;
        this.amount = amount;
    }

    public Treatment() {
    }

    @Override
    @Exclude
    public String getDBID() {
        return String.format("%s | %s | %s | %s", medicationId, profileID, time, prescriptionId);
    }

    public String getProfileID() {
        return profileID;
    }

    public void setProfileID(String profileID) {
        this.profileID = profileID;
    }
}
