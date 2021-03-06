package com.akzubarev.homedoctor.data.handlers;

import android.content.Context;

import com.akzubarev.homedoctor.data.models.BaseModel;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.MedicationStats;
import com.akzubarev.homedoctor.data.models.Prescription;
import com.akzubarev.homedoctor.data.models.Profile;
import com.akzubarev.homedoctor.data.models.Treatment;
import com.akzubarev.homedoctor.data.models.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

public interface DataHandler {
    static DataHandler getInstance(Context context) {
        FireBaseHandler fireBaseHandler = new FireBaseHandler();
        return fireBaseHandler;
//        if (fireBaseHandler.initialized())
//            return fireBaseHandler;
//        else
//            return new LocalHandler();
    }

    //region getAll
    void getProfiles(ProfilesCallback callback);

    void getPrescriptions(String profileID, PrescriptionsCallback callback);

    void getPrescriptions(PrescriptionsCallback callback);

    void getMedications(MedicationsCallback callback);

    void getMedications(String profileID, MedicationsCallback callback);

    void getTreatments(String prescriptionID, TreatmentsCallback callback);

    void getTreatmentsForProfile(String profileID, TreatmentsCallback callback);

    void getTreatments(TreatmentsCallback callback);

    void getMedicationStats(MedicationStatsCallback callback);

    //endregion

    //region get
    void getProfile(String name, ProfileCallback callback);

    void getTreatment(String id, TreatmentCallback callback);

    void getPrescription(String profileID, String id, PrescriptionCallback callback);

    void getMedication(String medicationName, MedicationCallback callback);

    void getMedicationStat(String id, MedicationStatCallback callback);

    //endregion

    //region save
    void saveProfile(Profile profile);

    void saveMedicationStats(MedicationStats medication, EmptyCallback callback);

    void savePrescription(Prescription prescription, String profileID);

    //endregion

    //region delete
    void deleteTreatments(ArrayList<Treatment> treatments);

    void deletePrescription(Prescription prescription);

    void deleteProfile(Profile profile);

    void deletePrescriptions(ArrayList<Prescription> prescriptions);

    void deleteMedication(Medication medication);

    void deleteObject(DatabaseReference dbr, BaseModel obj);

    void deleteObjects(DatabaseReference dbr, ArrayList<? extends BaseModel> objs);
    //endregion

    //region notifications

    void getCurrentReminder(TreatmentsCallback callback);

    void findNextReminders(TreatmentsCallback callback);

    void findNextReminderForProfile(String profileID, TreatmentCallback callback, EmptyCallback failCallback);

    void findNextReminder(String medicationID, TreatmentCallback callback, EmptyCallback failCallback);

    void findNextReminderForPrescription(String prescriptionID, TreatmentCallback callback, EmptyCallback failCallback);

    void saveNextReminders(ArrayList<Treatment> treatments);

    void getExpiryData(StringCallback callback);

    void getShortageData(StringCallback callback);

    void saveOldTreatment(Treatment treatmentStr);

    void getNextMorningTime(CalendarCallback callback);

    void getNextReminderTime(CalendarCallback callback);

    void saveSettings(String morningTime, Boolean control, String expireTimeFrame,
                      int expiryValue, String shortageMethod, int shortageValue,
                      EmptyCallback callback);

    void getShortageSettings(ShortageSettingsCallback callback);

    void getExpirySettings(ExpirySettingsCallback callback);

    void getMorningSettings(StringCallback callback);

    void saveMedication(Medication buildMedication, EmptyCallback callback);

    void getControlSettings(BoolCallback callback);

    void checkEndedPrescriptions(StringCallback callback);

    void createUser(FirebaseUser fbUser, User user);

    void getOldTreatments(TreatmentsCallback callback);

    void saveTreatments(ArrayList<Treatment> treatments, EmptyCallback callback);

    //endregion

    //region callbacksget

    interface ExpirySettingsCallback {
        void onCallback(String timeframe, int value);
    }

    interface ShortageSettingsCallback {
        void onCallback(String method, int value);
    }

    interface BoolCallback {
        void onCallback(Boolean result);
    }

    interface StringCallback {
        void onCallback(String message);
    }

    interface CalendarCallback {
        void onCallback(Calendar calendar);
    }

    interface MedicationCallback {
        void onCallback(Medication medication);
    }

    interface MedicationsCallback {
        void onCallback(ArrayList<Medication> medications);
    }

    interface MedicationStatsCallback {
        void onCallback(ArrayList<MedicationStats> medicationStats);
    }

    interface MedicationStatCallback {
        void onCallback(MedicationStats medicationStat);
    }

    interface PrescriptionCallback {
        void onCallback(Prescription prescription);
    }

    interface TreatmentsCallback {
        void onCallback(ArrayList<Treatment> treatments);
    }

    interface TreatmentCallback {
        void onCallback(Treatment treatment);
    }

    interface PrescriptionsCallback {
        void onCallback(ArrayList<Prescription> prescriptions);
    }

    interface ProfileCallback {
        void onCallback(Profile profile);
    }

    interface ProfilesCallback {
        void onCallback(ArrayList<Profile> profiles);
    }

    interface EmptyCallback {
        void onCallback();
    }
    //endregion

    default ArrayList<Medication> filter(ArrayList<Medication> query, ArrayList<String> targets) {
        return (ArrayList<Medication>) query.stream().filter(x -> targets.contains(x.getDbID())).collect(Collectors.toList());
    }

}
