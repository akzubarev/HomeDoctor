package com.akzubarev.homedoctor.data.models;

import com.google.firebase.database.Exclude;

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