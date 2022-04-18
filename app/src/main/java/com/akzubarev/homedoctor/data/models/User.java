package com.akzubarev.homedoctor.data.models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class User {
    private String name = "";
    private String email = "";
    private ArrayList<Prescription> prescriptions;
    private ArrayList<Medication> medications;

    public User(String name, String email, ArrayList<Prescription> prescriptions) {
        this.name = name;
        this.email = email;
        this.prescriptions = prescriptions;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(ArrayList<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }



    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        ArrayList<Map<String, Object>> prescriptionMap = new ArrayList<>();
        ArrayList<Map<String, Object>> medicationMap = new ArrayList<>();

        for (Prescription prescription : prescriptions) {
            prescriptionMap.add(prescription.toMap());
        }
        for (Medication medication : medications) {
            medicationMap.add(medication.toMap());
        }

        result.put("name", name);
        result.put("email", email);
        result.put("prescriptions", prescriptionMap);
        result.put("medications", medicationMap);

        return result;
    }
}