package com.akzubarev.homedoctor.data.handlers;

import android.content.Context;
import android.content.SharedPreferences;

import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.Prescription;
import com.akzubarev.homedoctor.data.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class LocalHandler implements DataHandler {
    public static final String DATA_SETTINGS = "Settings";

    public static final String THEME = "Theme";

    private final HashMap<String, Integer> defaultInts = new HashMap<>();
    private final HashMap<String, String> defaultStrings = new HashMap<>();
    private final HashMap<String, Boolean> defaultBooleans = new HashMap<>();


    public LocalHandler() {
        //ints
//        defaultInts.put(ROUND_TIME, 1);

        //enums
        defaultInts.put(THEME, -1); // dark

        //booleans
//        defaultBooleans.put(REMINDER, false);

        //strings
//        defaultStrings.put(REMINDER_TIME, "18:00");
    }

    public void save(Object value, String name, Context context) {

        SharedPreferences.Editor editor = context.getSharedPreferences(DATA_SETTINGS, Context.MODE_PRIVATE).edit();

        if (defaultInts.containsKey(name))
            editor.putInt(name, (int) value);
        else if (defaultStrings.containsKey(name))
            editor.putString(name, (String) value);
        else if (defaultBooleans.containsKey(name))
            editor.putBoolean(name, (boolean) value);
        else
            throw new IllegalArgumentException();
        editor.apply();
    }

    public Object get(String name, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(DATA_SETTINGS, Context.MODE_PRIVATE);

        if (defaultInts.containsKey(name))
            return prefs.getInt(name, defaultInts.get(name));
        else if (defaultStrings.containsKey(name))
            return prefs.getString(name, defaultStrings.get(name));
        else if (defaultBooleans.containsKey(name))
            return prefs.getBoolean(name, defaultBooleans.get(name));
        else
            throw new IllegalArgumentException();
    }

    @Override
    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
//        users.add(new User("Бабушка", getMedicationsForUser("stub")));
        users.add(new User("Мама", new ArrayList<>()));
        users.add(new User("Дочь", new ArrayList<>()));
        return users;
    }

    @Override
    public ArrayList<Medication> getAllMedications() {
        ArrayList<Medication> medications = new ArrayList<>();
        medications.add(new Medication("Парацетамол", "2 недели", 2));
        medications.add(new Medication("Ибупрофен", "1 неделя", 3));
        medications.add(new Medication("Феназепам", "1 месяц", 1));
        return medications;
    }

    @Override
    public ArrayList<Prescription> getPrescriptionsForUser(String userId) {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Medication> getMedicationsForUser(String userId) {
        ArrayList<Date> dates = new ArrayList<>();
        dates.add(Calendar.getInstance().getTime());
        ArrayList<Medication> medications = new ArrayList<>();
        medications.add(new Medication("Парацетамол",
                "2 недели", 2, dates));
        medications.add(new Medication("Ибупрофен",
                "1 неделя", 3, dates));
        medications.add(new Medication("Феназепам",
                "1 месяц", 1, dates));
        return medications;
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


    public void saveJson(String name, String json, Context p_context) {
        SharedPreferences.Editor editor = p_context.getSharedPreferences(DATA_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putString(name, json);
        editor.apply();
    }

    static String getJson(String name, Context p_context) {
        SharedPreferences prefs = p_context.getSharedPreferences(DATA_SETTINGS, Context.MODE_PRIVATE);
        return prefs.getString(name, "");
    }
}
