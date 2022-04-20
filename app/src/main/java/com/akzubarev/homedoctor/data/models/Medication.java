package com.akzubarev.homedoctor.data.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.Exclude;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Medication extends BaseModel {
    public Medication(String name, String medicationStatsID, int dailyFrequency) {
        this.name = name;
        this.medicationStatsID = medicationStatsID;
        this.dailyFrequency = dailyFrequency;
    }

    private String name;
    private String medicationStatsID;
    private int dailyFrequency = 1;
    private int tablets = 1;
    private Date expiry_date = new Date();
    private Map<String, Boolean> allowed_profiles = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        medicationStatsID = name;
    }

    public String getMedicationStatsID() {
        return medicationStatsID;
    }

    public int getDailyFrequency() {
        return dailyFrequency;
    }

    public Medication() {
    }

    public Medication(int dailyFrequency) {
        this.dailyFrequency = dailyFrequency;
    }

    @Override
    @Exclude
    public String getDBID() {
        return getName();
    }

    public String serialize() {
        String json = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static Medication deserialize(String json) {
        if (!json.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(json, Medication.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

//        result.put("name", name);
//        result.put("courceLength", courceLength);
//        result.put("dailyFrequency", dailyFrequency);

        return result;
    }

    public Map<String, Boolean> getAllowed_profiles() {
        return allowed_profiles;
    }

    public void addAllowed_profile(String allowed_profile) {
        this.allowed_profiles.put(allowed_profile, true);
    }
}