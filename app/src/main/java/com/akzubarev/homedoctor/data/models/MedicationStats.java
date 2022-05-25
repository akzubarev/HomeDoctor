package com.akzubarev.homedoctor.data.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.Exclude;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MedicationStats extends BaseModel {
    @Exclude
    public static String ANALOG = "аналог";
    @Exclude
    public static String CONTRADICTION = "противопоказания";

    private String name = "";
    private String group = "Нет данных";
    private String form = "Нет данных";
    private Map<String, String> relationships = new HashMap<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public MedicationStats() {
    }

    public MedicationStats(String name, String group, String form) {
        this.name = name;
        this.group = group;
        this.form = form;
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

    @Override
    public boolean validate() {
        return !name.equals("") && !form.equals("")
                && !group.equals("");
    }

    public Map<String, String> getRelationships() {
        return relationships;
    }

    public void setRelationships(Map<String, String> relationships) {
        this.relationships = relationships;
    }
}