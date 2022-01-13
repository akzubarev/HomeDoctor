package com.akzubarev.homedoctor.data.handlers;

import android.content.Context;

import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.User;

import java.util.ArrayList;

public class FireBaseHandler implements DataHandler {


    FireBaseHandler() {
        //TODO:implement Firebase

    }

    public void save(Object value, String name, Context context) {

    }

    public Object get(String name, Context context) {
        return 1;
    }

    @Override
    public ArrayList<User> getUsers() {
        return null;
    }

    @Override
    public ArrayList<Medication> getAllMedications() {
        return null;
    }

    @Override
    public ArrayList<Medication> getMedicationsForUser(String userId) {
        return null;
    }

    @Override
    public void addUser(String userId) {

    }

    @Override
    public void addMedication(String userId) {

    }

    @Override
    public void addMedicationForUser(String userId) {

    }

    public boolean initialized() {
        return false;
    }
}
