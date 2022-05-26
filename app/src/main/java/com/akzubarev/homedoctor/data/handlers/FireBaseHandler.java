package com.akzubarev.homedoctor.data.handlers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.akzubarev.homedoctor.data.models.BaseModel;
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.MedicationStats;
import com.akzubarev.homedoctor.data.models.Prescription;
import com.akzubarev.homedoctor.data.models.Profile;
import com.akzubarev.homedoctor.data.models.Treatment;
import com.akzubarev.homedoctor.data.models.User;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FireBaseHandler implements DataHandler {

    private static final String TAG = "FIREBASEHANDLER";
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
    private static final String OLDTREATMENTS = "oldTreatments";
    private static final String CONTROL = "control";
    private final DatabaseReference mDatabase;
    private final FirebaseAuth mAuth;

    public FireBaseHandler() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        init();
    }

    private void init() {

    }

    //region basic
    private void writeObject(DatabaseReference dbr, BaseModel obj) {
        Map<String, Object> childUpdates = new HashMap<>();
        String dbID = obj.getDbID();
        if (dbID == null || dbID.isEmpty()) {
            dbID = dbr.push().getKey();
            obj.setDbID(dbID);
        }
        childUpdates.put(dbID, obj);
        dbr.updateChildren(childUpdates);
    }

    private void writeObjects(DatabaseReference dbr, ArrayList<? extends BaseModel> objs) {
//        dbr.setValue(null);
        Map<String, Object> childUpdates = new HashMap<>();
        for (BaseModel obj : objs) {
            String dbID = obj.getDbID();
            if (dbID == null || dbID.isEmpty()) {
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
        return mDatabase.child(USERS).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
    }
//endregion

    //region save

    @Override
    public void createUser(FirebaseUser fbUser, User user) {
        mDatabase.child("users").child(fbUser.getUid()).setValue(user);
    }

    @Override
    public void saveMedicationStats(MedicationStats medication, EmptyCallback callback) {
        DatabaseReference dbr = mDatabase.child(MEDICATIONS);
        dbr.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        writeObject(dbr, medication);
                        callback.onCallback();
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
                        callback.onCallback();
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
    public void saveTreatments(ArrayList<Treatment> treatments, EmptyCallback callback) {
        DatabaseReference dbr = getUserDBR().child(TREATMENTS);
        dbr.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        writeObjects(dbr, treatments);
                        callback.onCallback();
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

    //endregion

    //region getAll
    @Override
    public void getOldTreatments(TreatmentsCallback callback) {
        DatabaseReference dbr = getUserDBR().child(OLDTREATMENTS);
        dbr.get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            ArrayList<Treatment> treats = new ArrayList<>();
            for (DataSnapshot child : result.getChildren()) {
                Treatment treat = child.getValue(Treatment.class);
                treats.add(treat);
            }
            callback.onCallback(treats);
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
    public void getPrescriptions(PrescriptionsCallback callback) {
        getUserDBR().child(PRESCRIPTIONS).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            ArrayList<Prescription> prescriptions = new ArrayList<>();
            for (DataSnapshot profile : result.getChildren()) {
                for (DataSnapshot prescriptionRef : profile.getChildren())
                    prescriptions.add(prescriptionRef.getValue(Prescription.class));
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
                Map<String, Boolean> allowed = Objects.requireNonNull(med).getAllowedProfiles();
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
                if (Objects.requireNonNull(tr).getPrescriptionId().equals(prescriptionID))
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
                if (Objects.requireNonNull(tr).getProfileID().equals(profileID))
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

    @Override
    public void getProfile(String id, ProfileCallback callback) {
        getUserDBR().child(PROFILES).child(id).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            Profile profile = result.getValue(Profile.class);
            callback.onCallback(profile);
        });
    }

    @Override
    public void getTreatment(String id, TreatmentCallback callback) {
        getUserDBR().child(TREATMENTS).child(id).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            Treatment treatment = result.getValue(Treatment.class);
            callback.onCallback(treatment);
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
    public void saveOldTreatment(Treatment treatment) {
        DatabaseReference dbr = getUserDBR().child(OLDTREATMENTS);
        dbr.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        writeObject(dbr, treatment);
                    }

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "addProfile:onCancelled", databaseError.toException());
                    }
                });
    }

    @Override
    public void getNextMorningTime(CalendarCallback callback) {
        DatabaseReference dbr = getUserDBR().child(SETTINGS).child(MORNINGTIME);
        dbr.get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            String[] treatmentTime = Objects.requireNonNull(result.getValue(String.class)).split(":");
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
    public void saveSettings(String morningTime, Boolean control, String expireTimeFrame,
                             int expiryValue, String shortageMethod, int shortageValue,
                             EmptyCallback callback) {

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(MORNINGTIME, morningTime);
        childUpdates.put(CONTROL, control);
        childUpdates.put(EXPIRATION + "/expiryTimeframe", expireTimeFrame);
        childUpdates.put(EXPIRATION + "/expiryValue", expiryValue);
        childUpdates.put(SHORTAGE + "/shortageMethod", shortageMethod);
        childUpdates.put(SHORTAGE + "/shortageValue", shortageValue);
        DatabaseReference dbr = getUserDBR().child(SETTINGS);
        dbr.updateChildren(childUpdates);
        callback.onCallback();
    }

    @Override
    public void findNextReminders(TreatmentsCallback callback) {
        getTreatments(treatments -> {
            List<Treatment> sorted = treatments.stream().sorted(Comparator.comparing(Treatment::getAbsoluteTime)).collect(Collectors.toList());
            if (sorted.size() > 0) {
                ArrayList<Treatment> treats = new ArrayList<>();
                Treatment next = sorted.get(0);
                treats.add(next);
                Log.d(TAG, String.valueOf(next.getTime()));

                sorted.remove(0);
                for (Treatment t : sorted)
                    if (t.getTime().equals(next.getTime()) && t.getDay().equals(next.getDay()))
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
                        dbr.setValue(null);
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
                    StringBuilder text = new StringBuilder();
                    for (Medication med : medications) {
                        if (med.getReminders() && med.doesExpire(timeframe, value))
                            text.append(med.getExpiryMessage()).append("\n");
                    }
                    String message = text.toString();
                    if (!message.isEmpty())
                        callback.onCallback(message);
                })
        );
    }

    @Override
    public void getShortageData(StringCallback callback) {
        getShortageSettings((method, value) ->
                getTreatments(treatments -> getMedications(medications -> {
                    StringBuilder text = new StringBuilder();
                    for (Medication med : medications) {
                        Stream<Treatment> sorted = treatments.stream().filter((treatment) -> treatment.getMedicationId().equals(med.getDbID()));
                        ArrayList<Treatment> filteredTreatments = (ArrayList<Treatment>) sorted.collect(Collectors.toList());
                        if (med.getReminders() && med.isShortage(method, value, filteredTreatments))
                            text.append(med.getShortageMessage());
                    }

                    String message = text.toString();
                    if (!message.isEmpty())
                        callback.onCallback(message);
                }))
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

    @Override
    public void getControlSettings(BoolCallback callback) {
        getUserDBR().child(SETTINGS).get().addOnCompleteListener(task -> {
            DataSnapshot result = task.getResult();
            Boolean control = result.child(CONTROL).getValue(Boolean.class);
            callback.onCallback(control);
        });
    }
    //endregion

    @Override
    public void checkEndedPrescriptions(StringCallback callback) {
        DatabaseReference dbr = getUserDBR().child(TREATMENTS);
        ArrayList<Prescription> endedPrescriptions = new ArrayList<>();
        getTreatments(treatments -> getPrescriptions((prescriptions) -> {
                    for (Prescription prescription : prescriptions) {
                        if (prescription.getAutoDisable() && prescription.ended()) {
                            Stream<Treatment> sorted = treatments.stream().filter(treatment -> treatment.getPrescriptionId().equals(prescription.getDbID()));
                            ArrayList<Treatment> prescriptionTreatments = (ArrayList<Treatment>) sorted.collect(Collectors.toList());
                            if (prescriptionTreatments.size() > 0) {
                                deleteObjects(dbr, prescriptionTreatments);
                                endedPrescriptions.add(prescription);
                            }

                        }
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Prescription endedPrescription : endedPrescriptions)
                        stringBuilder.append(endedPrescription.getEndedMessage());

                    String message = stringBuilder.toString();
                    if (!message.isEmpty())
                        callback.onCallback(message);

                })
        );
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