package com.akzubarev.homedoctor.data.handlers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.akzubarev.homedoctor.data.models.BaseModel;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.MedicationStats;
import com.akzubarev.homedoctor.data.models.Prescription;
import com.akzubarev.homedoctor.data.models.Profile;
import com.akzubarev.homedoctor.data.models.Treatment;
import com.akzubarev.homedoctor.data.models.User;
import com.akzubarev.homedoctor.ui.notifications.NotificationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FireBaseHandler implements DataHandler {

    private static final String TAG = "NewPostFragment";
    private static final String MEDICATIONS = "medications";
    private static final String USERS = "users";
    private static final String PROFILES = "profiles";
    private static final String PRESCRIPTIONS = "prescriptions";
    private static final String TREATMENTS = "treatments";
    private static final String ACCESS = "access";
    private static final String SETTINGS = "settings";
    private static final String NEXTREMINDER = "nextreminder";
    private static final String MORNINGTIME = "morningTime";
    private static final String SHORTAGE = "ShortageSettings";
    private static final String EXPIRATION = "ExpirySettings";
    private final DatabaseReference mDatabase;
    private final FirebaseAuth mAuth;

    public FireBaseHandler(Context context) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        init();
    }

    private void init() {
        MedicationStats ms = new MedicationStats("Парацетамол", "Анальгетик", "Таблетки");
//        saveMedicationStats(ms);

//        saveProfile(new Profile("Мама", "Женщина", "01.01.1968"));
//        saveProfile(new Profile("Дочь", "Женщина", "01.01.2000"));

        Medication paracetamol = new Medication("Парацетамол", "Парацетамол");
        paracetamol.addAllowedProfile("Дочь");
//        saveMedication(paracetamol);


        Prescription angina = new Prescription("Ангина Весна 2022", "2 месяца");
//        savePrescription(angina, "Дочь");
//

        ArrayList<Treatment> treatmetsPara = new ArrayList<>();
        treatmetsPara.add(new Treatment(paracetamol.getDbID(), angina.getName(), "Дочь", "Понедельник", "14:00", 1));
        treatmetsPara.add(new Treatment(paracetamol.getDbID(), angina.getName(), "Дочь", "Понедельник", "11:00", 1));
        treatmetsPara.add(new Treatment(paracetamol.getDbID(), angina.getName(), "Дочь", "Вторник", "17:00", 2));
//        saveTreatments(treatmetsPara);

    }

    public void save(Object value, String name, Context context) {

    }

    public Object get(String name, Context context) {
        return 1;
    }

    public boolean initialized() {
        return false;
    }

    //region basic
    private void writeObject(DatabaseReference dbr, BaseModel obj) {
        Map<String, Object> childUpdates = new HashMap<>();
        String dbID = obj.getDbID();
        if (dbID.isEmpty()) {
            dbID = dbr.push().getKey();
            obj.setDbID(dbID);
        }
        childUpdates.put(dbID, obj);
        dbr.updateChildren(childUpdates);
    }

    private void writeObjects(DatabaseReference dbr, ArrayList<? extends BaseModel> objs) {
        dbr.setValue(null);
        Map<String, Object> childUpdates = new HashMap<>();
        for (BaseModel obj : objs) {
            String dbID = obj.getDbID();
            if (dbID.isEmpty()) {
                dbID = dbr.push().getKey();
                obj.setDbID(dbID);
            }
            childUpdates.put(dbID, obj);
        }
        dbr.updateChildren(childUpdates);
    }

    private void writePrimitives(DatabaseReference dbr, ArrayList<?> objs) {
        dbr.setValue(null);
        Map<String, Object> childUpdates = new HashMap<>();
        for (int i = 0; i < objs.size(); i++)
            childUpdates.put(Integer.toString(i + 1), objs.get(i));
        dbr.updateChildren(childUpdates);
    }

    public DatabaseReference getUserDBR() {
        return mDatabase.child(USERS).child(mAuth.getCurrentUser().getUid());
    }
//endregion

    //region save

    @Override
    public void createUser(FirebaseUser fbUser, User user) {
        mDatabase.child("users").child(fbUser.getUid()).setValue(user);
    }

    @Override
    public void saveAllowed(String medicationID, ArrayList<String> profileIDs) {
        DatabaseReference dbr = getUserDBR().child(MEDICATIONS).child(medicationID).child(ACCESS);
        dbr.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        writePrimitives(dbr, profileIDs);
                    }

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    @Override
    public void saveMedicationStats(MedicationStats medication) {
        DatabaseReference dbr = mDatabase.child(MEDICATIONS);
        dbr.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        writeObject(dbr, medication);
                    }

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    @Override
    public void saveMedication(Medication medication) {
        DatabaseReference dbr = getUserDBR().child(MEDICATIONS);
        dbr.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        writeObject(dbr, medication);
                    }

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void saveMedication(Medication medication, EmptyCallback callback) {
        DatabaseReference dbr = getUserDBR().child(MEDICATIONS);
        dbr.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        writeObject(dbr, medication);
                    }

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    @Override
    public void savePrescription(Prescription prescription, String profileID) {
        DatabaseReference dbr = getUserDBR().child(PRESCRIPTIONS).child(profileID);
        dbr.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        writeObject(dbr, prescription);
                    }

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    @Override
    public void saveTreatments(ArrayList<Treatment> treatments) {
        DatabaseReference dbr = getUserDBR().child(TREATMENTS);
        dbr.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        writeObjects(dbr, treatments);
                    }

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "saveTreatments:onCancelled", databaseError.toException());
                    }
                });
    }

    @Override
    public void saveProfile(Profile profile) {
        DatabaseReference userDBR = getUserDBR();
        userDBR.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        writeObject(userDBR.child(PROFILES), profile);
                    }

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "addProfile:onCancelled", databaseError.toException());
                    }
                });
    }


    public void saveProfiles(ArrayList<Profile> profiles) {
        for (Profile profile : profiles)
            saveProfile(profile);
    }

    //endregion

    //region getAll

    @Override
    public void getAllowedProfiles(String medicationID, AllowedProfilesCallback callback) {
        getUserDBR().child(MEDICATIONS).child(medicationID).child(ACCESS).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            ArrayList<String> profiles = new ArrayList<>();
            for (DataSnapshot child : result.getChildren()) {
                String profileID = child.getValue(String.class);
                profiles.add(profileID);
            }
            callback.onCallback(profiles);
        });

    }


    @Override
    public void getProfiles(ProfilesCallback callback) {
        getUserDBR().child(PROFILES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Profile> profiles = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    profiles.add(child.getValue(Profile.class));
                }
                callback.onCallback(profiles);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getPrescriptions(String profileID, PrescriptionsCallback callback) {
        getUserDBR().child(PRESCRIPTIONS).child(profileID).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            ArrayList<Prescription> prescriptions = new ArrayList<>();
            for (DataSnapshot child : result.getChildren()) {
                prescriptions.add(child.getValue(Prescription.class));
            }
            callback.onCallback(prescriptions);
        });
    }


    @Override
    public void getMedications(MedicationsCallback callback) {
        getUserDBR().child(MEDICATIONS).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            ArrayList<Medication> meds = new ArrayList<>();
            for (DataSnapshot child : result.getChildren()) {
                Medication med = child.getValue(Medication.class);
                meds.add(med);
            }
            callback.onCallback(meds);
        });

    }

    @Override
    public void getMedications(String profileID, MedicationsCallback callback) {
        getUserDBR().child(MEDICATIONS).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            ArrayList<Medication> meds = new ArrayList<>();
            for (DataSnapshot child : result.getChildren()) {
                Medication med = child.getValue(Medication.class);
                Map<String, Boolean> allowed = med.getAllowedProfiles();
                if (allowed.getOrDefault(profileID, false))
                    meds.add(med);
            }
            callback.onCallback(meds);
        });
    }

    @Override
    public void getTreatments(String prescriptionID, TreatmentsCallback callback) {
        getUserDBR().child(TREATMENTS).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            ArrayList<Treatment> treatments = new ArrayList<>();
            for (DataSnapshot child : result.getChildren()) {
                Treatment tr = child.getValue(Treatment.class);
                if (tr.getPrescriptionId().equals(prescriptionID))
                    treatments.add(tr);
            }
            callback.onCallback(treatments);
        });
    }

    @Override
    public void getTreatmentsForProfile(String profileID, TreatmentsCallback callback) {
        getUserDBR().child(TREATMENTS).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            ArrayList<Treatment> treatments = new ArrayList<>();
            for (DataSnapshot child : result.getChildren()) {
                Treatment tr = child.getValue(Treatment.class);
                if (tr.getProfileID().equals(profileID))
                    treatments.add(tr);
            }
            callback.onCallback(treatments);
        });
    }

    @Override
    public void getTreatments(TreatmentsCallback callback) {
        getUserDBR().child(TREATMENTS).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            ArrayList<Treatment> treatments = new ArrayList<>();
            for (DataSnapshot child : result.getChildren()) {
                Treatment tr = child.getValue(Treatment.class);
                treatments.add(tr);
            }
            callback.onCallback(treatments);
        });
    }

    @Override
    public void getMedicationStats(MedicationStatsCallback callback) {
        mDatabase.child(MEDICATIONS).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            ArrayList<MedicationStats> meds = new ArrayList<>();
            for (DataSnapshot child : result.getChildren()) {
                meds.add(child.getValue(MedicationStats.class));
            }
            callback.onCallback(meds);
        });
    }
    //endregion

    //region get

    public void getUser(UserCallback callback) {
        getUserDBR().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Profile> profiles = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    profiles.add(child.getValue(Profile.class));
                }
                callback.onCallback(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getProfile(String id, ProfileCallback callback) {
        getUserDBR().child(PROFILES).child(id).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            Profile profile = result.getValue(Profile.class);
            callback.onCallback(profile);
        });
    }

    @Override
    public void getPrescription(String profileID, String id, PrescriptionCallback callback) {
        getUserDBR().child(PRESCRIPTIONS).child(profileID).child(id).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            Prescription prescription = result.getValue(Prescription.class);
            callback.onCallback(prescription);
        });
    }

    @Override
    public void getMedication(String id, MedicationCallback callback) {
        getUserDBR().child(MEDICATIONS).child(id).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            Medication med = result.getValue(Medication.class);
//            med.setAllowedProfiles(result.getValue());
            callback.onCallback(med);
        });
    }

    @Override
    public void getMedicationStat(String id, MedicationStatCallback callback) {
        mDatabase.child(MEDICATIONS).child(id).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            MedicationStats stats = result.getValue(MedicationStats.class);
            callback.onCallback(stats);
        });
    }
    //endregion

    //region create
    private void createUserStructure() {
        DatabaseReference userDBR = getUserDBR();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(PROFILES, null);
        childUpdates.put(MEDICATIONS, null);

        userDBR.updateChildren(childUpdates);
    }

    private void createProfileStructure(Profile profile) {
        DatabaseReference userDBR = getUserDBR();
        DatabaseReference profileDBR = userDBR.child(profile.getDbID());
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(PRESCRIPTIONS, null);
        childUpdates.put(MEDICATIONS, null);

        profileDBR.updateChildren(childUpdates);
    }
    //endregion

    //region delete
    @Override
    public void deleteTreatments(ArrayList<Treatment> treatments) {
        DatabaseReference dbr = getUserDBR().child(TREATMENTS);
        dbr.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        deleteObjects(dbr, treatments);
                    }

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "saveTreatments:onCancelled", databaseError.toException());
                    }
                });
    }


    @Override
    public void deletePrescription(Prescription prescription) {
        DatabaseReference dbr = getUserDBR().child(PRESCRIPTIONS).child(prescription.getDbID());
        deleteObject(dbr, prescription);
        getTreatments(prescription.getDbID(), this::deleteTreatments);
    }

    @Override
    public void deleteProfile(Profile profile) {
        DatabaseReference dbr = getUserDBR().child(PROFILES).child(profile.getDbID());
        deleteObject(dbr, profile);
        getPrescriptions(profile.getDbID(), this::deletePrescriptions);
    }

    @Override
    public void deletePrescriptions(ArrayList<Prescription> prescriptions) {
        DatabaseReference dbr = getUserDBR().child(PRESCRIPTIONS);
        deleteObjects(dbr, prescriptions);
        for (Prescription prescription : prescriptions)
            getTreatments(prescription.getDbID(), this::deleteTreatments);
    }

    @Override
    public void deleteMedication(Medication medication) {
        DatabaseReference dbr = getUserDBR().child(MEDICATIONS);
        deleteObject(dbr, medication);
        getTreatments(treatments -> {
            for (Treatment treatment : treatments)
                if (treatment.getMedicationId().equals(medication.getDbID()))
                    deleteObject(getUserDBR().child(TREATMENTS), treatment);
        });
    }

    @Override
    public void deleteObject(DatabaseReference dbr, BaseModel obj) {
        dbr.child(obj.getDbID()).setValue(null);
    }

    @Override
    public void deleteObjects(DatabaseReference dbr, ArrayList<? extends BaseModel> objs) {
        Map<String, Object> childUpdates = new HashMap<>();
        for (BaseModel obj : objs)
            childUpdates.put(obj.getDbID(), null);
        dbr.updateChildren(childUpdates);
    }
//endregion

    //region notifications
    @Override
    public void getNextMorningTime(CalendarCallback callback) {
        DatabaseReference dbr = getUserDBR().child(SETTINGS).child(MORNINGTIME);
        dbr.get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            String[] treatmentTime = result.getValue(String.class).split(":");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(treatmentTime[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(treatmentTime[1]));
            calendar.set(Calendar.SECOND, 0);

            if (calendar.before(Calendar.getInstance()))
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            callback.onCallback(calendar);
        });
    }

    @Override
    public void getCurrentReminder(TreatmentsCallback callback) {
        DatabaseReference dbr = getUserDBR().child(NEXTREMINDER);
        dbr.get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            ArrayList<Treatment> treatments = new ArrayList<>();
            for (DataSnapshot child : result.getChildren()) {
                treatments.add(child.getValue(Treatment.class));
            }
            callback.onCallback(treatments);
        });

    }

    @Override
    public void getNextReminderTime(CalendarCallback callback) {
        findNextReminders((nextReminders) -> {
            saveNextReminders(nextReminders);
            if (nextReminders.size() > 0)
                callback.onCallback(nextReminders.get(0).getAbsoluteTime());
        });
    }

    @Override
    public void saveSettings(String morningTime, String expireTimeFrame,
                             int expiryValue, String shortageMethod, int shortageValue) {

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("morningTime", morningTime);
        childUpdates.put(EXPIRATION + "/expireTimeFrame", expireTimeFrame);
        childUpdates.put(EXPIRATION + "/expiryValue", expiryValue);
        childUpdates.put(SHORTAGE + "/shortageMethod", shortageMethod);
        childUpdates.put(SHORTAGE + "/shortageValue", shortageValue);
        DatabaseReference dbr = getUserDBR().child(SETTINGS);
        dbr.updateChildren(childUpdates);
    }

//    @Override
//    public void getSettings() {
//        Map<String, Object> f = new HashMap<>();
//        childUpdates.put("morningTime", morningTime);
//        childUpdates.put("expireTimeFrame", expireTimeFrame);
//        childUpdates.put("expiryValue", expiryValue);
//        childUpdates.put("shortageMethod", shortageMethod);
//        childUpdates.put("shortageValue", shortageValue);
//        DatabaseReference dbr = getUserDBR().child(SETTINGS);
//        dbr.updateChildren(childUpdates);
//    }

    @Override
    public void findNextReminders(TreatmentsCallback callback) {
        getTreatments(treatments -> {
            List<Treatment> sorted = treatments.stream().sorted(Comparator.comparing(Treatment::getAbsoluteTime)).collect(Collectors.toList());
            if (sorted.size() > 0) {
                ArrayList<Treatment> treats = new ArrayList<>();
                Treatment next = sorted.get(0);
                for (Treatment t : sorted)
                    if (t.getAbsoluteTime().equals(next.getAbsoluteTime()))
                        treats.add(t);
                callback.onCallback(treats);
            }
        });
    }

    @Override
    public void findNextReminder(String medicationID, TreatmentCallback callback, EmptyCallback failCallback) {
        getTreatments(treatments -> {
            Stream<Treatment> sorted = treatments.stream().filter(x ->
                    x.getMedicationId().equals(medicationID)).sorted(
                    Comparator.comparing(Treatment::getAbsoluteTime));
            Optional<Treatment> next = sorted.findFirst();

            if (next.isPresent())
                callback.onCallback(next.get());
            else
                failCallback.onCallback();
        });
    }

    @Override
    public void findNextReminderForProfile(String profileID, TreatmentCallback callback, EmptyCallback failCallback) {
        getTreatmentsForProfile(profileID, treatments -> {
            Stream<Treatment> sorted = treatments.stream().sorted(
                    Comparator.comparing(Treatment::getAbsoluteTime));
            Optional<Treatment> next = sorted.findFirst();

            if (next.isPresent())
                callback.onCallback(next.get());
            else
                failCallback.onCallback();
        });
    }

    @Override
    public void findNextReminderForPrescription(String prescriptionID, TreatmentCallback callback, EmptyCallback failCallback) {
        getTreatments(prescriptionID, treatments -> {
            Stream<Treatment> sorted = treatments.stream().sorted(
                    Comparator.comparing(Treatment::getAbsoluteTime));
            Optional<Treatment> next = sorted.findFirst();
            if (next.isPresent())
                callback.onCallback(next.get());
            else
                failCallback.onCallback();
        });
    }

    @Override
    public void saveNextReminders(ArrayList<Treatment> treatments) {
        DatabaseReference dbr = getUserDBR().child(NEXTREMINDER);
        dbr.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        writeObjects(dbr, treatments);
                    }

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "saveNextReminder:onCancelled", databaseError.toException());
                    }
                });
    }

    @Override
    public void getExpiryData(StringCallback callback) {
        getExpirySettings((timeframe, value) ->
                getMedications(medications -> {
                    int daysToExpire = 0;
                    switch (timeframe) {
                        case "day":
                            daysToExpire = value;
                            break;
                        case "week":
                            daysToExpire = value * 7;
                            break;
                        case "month":
                            daysToExpire = value * 30;
                            break;
                    }
                    StringBuilder text = new StringBuilder();
                    for (Medication med : medications) {
                        if (med.doesExpire(daysToExpire))
                            text.append(med.getExpiryMessage()).append("\n");
                    }
                    callback.onCallback(text.toString());
                })
        );
    }

    @Override
    public void getShortageData(StringCallback callback) {
        getShortageSettings((method, value) ->
                getMedications(medications -> {
                    StringBuilder text = new StringBuilder();
                    for (Medication med : medications) {
                        getTreatments(treatments -> {
                            treatments = (ArrayList<Treatment>) treatments.stream().filter(
                                            (treatment) -> treatment.getMedicationId().equals(med.getDbID()))
                                    .collect(Collectors.toList());
                            if (med.isShortage(method, value, treatments))
                                text.append(med.getExpiryMessage()).append("\n");
                        });

                    }
                    callback.onCallback(text.toString());
                })
        );
    }

    @Override
    public void getShortageSettings(ShortageSettingsCallback callback) {
        getUserDBR().child(SETTINGS).child(SHORTAGE).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            String method = result.child("shortageMethod").getValue(String.class);
            int value = result.child("shortageValue").getValue(Integer.class);

            callback.onCallback(method, value);
        });
    }

    @Override
    public void getExpirySettings(ExpirySettingsCallback callback) {
        getUserDBR().child(SETTINGS).child(EXPIRATION).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            String timeframe = result.child("expiryTimeframe").getValue(String.class);
            int value = result.child("expiryValue").getValue(Integer.class);

            callback.onCallback(timeframe, value);
        });
    }

    @Override
    public void getMorningSettings(StringCallback callback) {
        getUserDBR().child(SETTINGS).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            String morning = result.child(MORNINGTIME).getValue(String.class);
            callback.onCallback(morning);
        });
    }
    //endregion

    @Override
    public void checkEndedPrescriptions(StringCallback callback) {
        DatabaseReference dbr = getUserDBR().child(TREATMENTS);
        ArrayList<Prescription> endedPrescriptions = new ArrayList<>();
        getProfiles(profiles -> {
            for (Profile profile : profiles)
                getPrescriptions(profile.getDbID(), prescriptions -> {
                    for (Prescription prescription : prescriptions) {
                        if (prescription.getAutoDisable() && prescription.ended())
                            getTreatments(prescription.getDbID(),
                                    treatments -> {
                                        if (treatments.size() > 0) {
                                            deleteObjects(dbr, treatments);
                                            endedPrescriptions.add(prescription);
                                        }
                                    });
                    }
                });
        });
        StringBuilder stringBuilder = new StringBuilder();
        for (Prescription prescription : endedPrescriptions)
            stringBuilder.append(prescription.getName()).append(" схема лечения подошла к концу\n");
        callback.onCallback(stringBuilder.toString());
    }
}


//    public void getObject(Class cls, String dbID, BaseModelCallback callback) {
//        if (BaseModel.class.isAssignableFrom(cls))
//            try {
//                Field pathField = cls.getField("dbpath");
//                String path = (String) pathField.get(null);
//                DatabaseReference userDBR = getUserDBR();
//                DatabaseReference dataDBR = userDBR.child(path);
//                dataDBR.get().addOnCompleteListener(task -> {
//                    DataSnapshot result = task.getResult();
//                    ArrayList<BaseModel> values = new ArrayList<>();
//                    if (result != null && result.hasChildren())
//                        for (DataSnapshot snapshot : result.getChildren()) {
//                            cls med = snapshot.getValue(cls);
//                            values.add(med);
//                        }
//                    callback.onCallback(values);
//                });
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                e.printStackTrace();
//            }