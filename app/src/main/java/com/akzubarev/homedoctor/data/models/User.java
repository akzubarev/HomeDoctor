package com.akzubarev.homedoctor.data.models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;

public class User {
    private String name = "";
    private String email = "";
    private ArrayList<Prescription> prescriptions;
    private ArrayList<Profile> profiles;
    private ArrayList<Medication> medications;

    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getDBID() {
        return getName();
    }
}