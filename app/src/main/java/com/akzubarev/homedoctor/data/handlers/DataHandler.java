package com.akzubarev.homedoctor.data.handlers;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.akzubarev.homedoctor.data.models.BaseModel;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.MedicationStats;
import com.akzubarev.homedoctor.data.models.Prescription;
import com.akzubarev.homedoctor.data.models.Profile;
import com.akzubarev.homedoctor.data.models.Treatment;
import com.akzubarev.homedoctor.data.models.User;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

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


    void saveMedicationStats(MedicationStats medication);

    void saveMedication(Medication medication);

    void savePrescription(Prescription prescription, String profileID);

    void saveTreatments(ArrayList<Treatment> treatments);

    void saveProfile(Profile profile);

    void getProfiles(ProfilesCallback callback);


    void getPrescriptions(PrescriptionsCallback callback, String profileID);

    void getMedications(MedicationsCallback callback);

    void getMedications(MedicationsCallback callback, String profileID);

    void getTreatments(TreatmentsCallback callback, String prescriptionID);

    void getMedicationStats(MedicationStatsCallback callback);

    void getProfile(String name, ProfileCallback callback);


    void getPrescription(PrescriptionCallback callback, String profileID, String id);

    void getMedication(String medicationName, MedicationCallback callback);

    void getMedicationStat(String id, MedicationStatCallback callback);

    Treatment findNextReminder();

    void saveNextReminder(Treatment treatment);

    Treatment getCurrentReminder();

    String getExpiryData();

    String getShortageData();

    //region delete
    void deleteTreatments(ArrayList<Treatment> treatments);

    void deleteObjects(DatabaseReference dbr, ArrayList<? extends BaseModel> objs);

    Calendar getNextMorningTime();

    Calendar getNextReminderTime();

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

    interface PrescriptionsCallback {
        void onCallback(ArrayList<Prescription> prescriptions);
    }

    interface ProfileCallback {
        void onCallback(Profile profile);
    }

    interface ProfilesCallback {
        void onCallback(ArrayList<Profile> profiles);
    }

    interface UserCallback {
        void onCallback(User user);
    }
    interface SuccesfulSave {
        void onCallback();
    }


    default ArrayList<Medication> filter(ArrayList<Medication> query, ArrayList<String> targets) {
        ArrayList<Medication> intersect = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            intersect = (ArrayList<Medication>) query.stream().filter(x -> targets.contains(x.getDBID())).collect(Collectors.toList());
        else
            for (Medication model : query)
                if (targets.contains(model.getDBID()))
                    intersect.add(model);

        return intersect;
    }

}
