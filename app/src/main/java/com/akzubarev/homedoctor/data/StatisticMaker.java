package com.akzubarev.homedoctor.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.akzubarev.homedoctor.data.models.Medication;


public class StatisticMaker {

    public static final String STATISTICS = "Statistics";
    public static final String MEDICATIONS = "medications";


    public static void saveMedication(Medication medication, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);

        String medicationCountStr = prefs.getString(MEDICATIONS, "0");
        int medicationCount = Integer.parseInt(medicationCountStr);
        saveMedication_by_number(medication, context, medicationCount);
        editor.putString(MEDICATIONS, Integer.toString(medicationCount + 1));
        editor.apply();
    }

    public static Medication loadMedication(Context context, int medicationNumber) {
        SharedPreferences prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        String currentMedication = prefs.getString(MEDICATIONS + "_" + medicationNumber, "");
        return Medication.deserialize(currentMedication);
    }


    public static int getMedicationCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        return Integer.parseInt(prefs.getString(MEDICATIONS, "0"));
    }

    public static String getMedicationInfoOLD(Context context, int medicationNumber) {
        SharedPreferences prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        return prefs.getString(MEDICATIONS + "_" + medicationNumber + "_" + "0", "");
    }

    public static void removeStatistics(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
//        FireBaseUtils.deleteMedications();
    }

    public static void removeMedication(Context context, int medicationNumber) {
        SharedPreferences.Editor editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        editor.remove(MEDICATIONS + "_" + medicationNumber);
        int medicationsCount = Integer.parseInt(prefs.getString(MEDICATIONS, "0"));
        editor.putString(MEDICATIONS, Integer.toString(medicationsCount - 1));
        editor.apply();
        for (int i = medicationNumber + 1; i < medicationsCount; ++i) {
            Medication medication = loadMedication(context, i);
            saveMedication_by_number(medication, context, i - 1);
        }
    }

    public static void removeMedicationOld(Context context, int medicationNumber) {
        SharedPreferences.Editor editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        int taskCount = Integer.parseInt(prefs.getString(MEDICATIONS + "_" + medicationNumber, "0"));
        for (int taskNumber = 0; taskNumber < taskCount + 2; ++taskNumber) {
            editor.remove(MEDICATIONS + "_" + medicationNumber + "_" + taskNumber);
        }
        editor.remove(MEDICATIONS + "_" + medicationNumber);
        int medicationsCount = Integer.parseInt(prefs.getString(MEDICATIONS, "0"));
        editor.putString(MEDICATIONS, Integer.toString(medicationsCount - 1));
        editor.apply();
        for (int i = medicationNumber + 1; i < medicationsCount; ++i) {
            Medication medication = loadMedication(context, i);
            saveMedication_by_number(medication, context, i - 1);
        }
    }

    public static void saveMedication_by_number(Medication medication, Context context, int medicationNumber) {
        SharedPreferences.Editor editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit();
        String medicationNumberString = Integer.toString(medicationNumber);
        String serializedMedication = medication.serialize();
        editor.putString(MEDICATIONS + "_" + medicationNumberString, serializedMedication);
        editor.apply();

//        FireBaseUtils.uploadMedication(medication);
    }
}
