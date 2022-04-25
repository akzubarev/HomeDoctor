package com.akzubarev.homedoctor.data.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.Exclude;

import java.io.IOException;

public class MedicationStats extends BaseModel {
    private String name = "";
    private String courseLength = "1 месяц";
    private int dailyFrequency = 1;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseLength() {
        return courseLength;
    }

    public void setCourseLength(String courseLength) {
        this.courseLength = courseLength;
    }

    public int getDailyFrequency() {
        return dailyFrequency;
    }

    public void setDailyFrequency(int dailyFrequency) {
        this.dailyFrequency = dailyFrequency;
    }

    public MedicationStats() {
    }

    public MedicationStats(String name, String courceLength, int dailyFrequency) {
        this.name = name;
        this.courseLength = courceLength;
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

    public static MedicationStats deserialize(String json) {
        if (!json.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(json, MedicationStats.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}