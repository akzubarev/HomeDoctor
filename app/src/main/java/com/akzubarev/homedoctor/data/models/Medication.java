package com.akzubarev.homedoctor.data.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Medication extends BaseModel {
    private String name = "";
    private String courceLength = "1 месяц";
    private int dailyFrequency = 1;


    public void setFrequency(ArrayList<Date> consDates) {
        dailyFrequency = consDates.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourceLength() {
        return courceLength;
    }

    public void setCourceLength(String courceLength) {
        this.courceLength = courceLength;
    }

    public int getDailyFrequency() {
        return dailyFrequency;
    }

    public void setDailyFrequency(int dailyFrequency) {
        this.dailyFrequency = dailyFrequency;
    }

    public Medication() {
    }

    public Medication(String name, String courceLength,
                      int dailyFrequency) {
        this.name = name;
        this.courceLength = courceLength;
        this.dailyFrequency = dailyFrequency;
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

        result.put("name", name);
        result.put("courceLength", courceLength);
        result.put("dailyFrequency", dailyFrequency);

        return result;
    }
}