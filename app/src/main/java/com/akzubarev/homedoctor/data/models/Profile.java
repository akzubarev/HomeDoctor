package com.akzubarev.homedoctor.data.models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Profile extends BaseModel {
    private String name = "";
    private String gender;
    private String birthday;

    public Profile(String name, String gender, String birthday, ArrayList<Prescription> prescriptions) {
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
    }

    public Profile(String name, String gender, String birthday) {
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
    }

    public Profile() {
    }

    @Override
    @Exclude
    public String getDBID() {
        return getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
//        ArrayList<Map<String, Object>> prescriptionMap = new ArrayList<>();
//        ArrayList<Map<String, Object>> medicationMap = new ArrayList<>();
//
//        for (Prescription prescription : prescriptions) {
//            prescriptionMap.add(prescription.toMap());
//        }
//        for (Medication medication : medications) {
//            medicationMap.add(medication.toMap());
//        }
//
//        result.put("name", name);
//        result.put("prescriptions", prescriptionMap);
//        result.put("medications", medicationMap);

        return result;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}