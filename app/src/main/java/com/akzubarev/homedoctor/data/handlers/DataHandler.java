package com.akzubarev.homedoctor.data.handlers;

import android.content.Context;

import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.Prescription;
import com.akzubarev.homedoctor.data.models.User;

import java.util.ArrayList;

public interface DataHandler {
    static DataHandler getInstance() {
        FireBaseHandler fireBaseHandler = new FireBaseHandler();
        if (fireBaseHandler.initialized())
            return new FireBaseHandler();
        else
            return new LocalHandler();
    }

    void save(Object value, String name, Context context);

    Object get(String name, Context context);


    ArrayList<User> getUsers();

    ArrayList<Medication> getAllMedications();


    ArrayList<Prescription> getPrescriptionsForUser(String userId);

    ArrayList<Medication> getMedicationsForUser(String userId);

    void addUser(String userId);

    void addMedication(String userId);

    void addMedicationForUser(String userId);

}
