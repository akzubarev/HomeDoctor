package com.akzubarev.homedoctor.data.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class User {
    private String name = "";
    private ArrayList<Prescription> prescriptions;

    public User(String name, ArrayList<Prescription> prescriptions) {
        this.name = name;
        this.prescriptions = prescriptions;
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


    public Medication nextConsumption() {
        ArrayList<Date> dates = new ArrayList<>();
        dates.add(Calendar.getInstance().getTime());
        return new Medication("Парацетамол", "1 месяц", 1, dates);
    }
}