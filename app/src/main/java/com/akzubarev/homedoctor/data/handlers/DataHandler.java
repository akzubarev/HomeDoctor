package com.akzubarev.homedoctor.data.handlers;

import android.content.Context;

import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.Prescription;
import com.akzubarev.homedoctor.data.models.User;

import java.util.ArrayList;

public interface DataHandler {
    static DataHandler getInstance(Context context) {
        FireBaseHandler fireBaseHandler = new FireBaseHandler(context);
        return fireBaseHandler;
//        if (fireBaseHandler.initialized())
//            return fireBaseHandler;
//        else
//            return new LocalHandler();
    }

    void save(Object value, String name, Context context);

    Object get(String name, Context context);


    ArrayList<User> getUsers();

    ArrayList<Medication> getAllMedications();


    ArrayList<Prescription> getPrescriptionsForUser(String userId);

    ArrayList<Medication> getMedicationsForUser(String userId);

    void addMedication(Medication medication) ;

    void addMedicationForUser(User user, Medication medication);

}
