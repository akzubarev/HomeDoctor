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

public abstract class BaseModel {
    protected String dbID = "";

    public BaseModel() {
    }

    public String getDbID() {
        return dbID;
    }

    public void setDbID(String dbID) {
        this.dbID = dbID;
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

    public static BaseModel deserialize(String json) {
        if (!json.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(json, BaseModel.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}