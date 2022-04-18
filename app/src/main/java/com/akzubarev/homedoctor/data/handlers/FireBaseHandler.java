package com.akzubarev.homedoctor.data.handlers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.Prescription;
import com.akzubarev.homedoctor.data.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FireBaseHandler implements DataHandler {

    private static final String TAG = "NewPostFragment";
    private static final String MEDICATIONS = "medications";
    private DatabaseReference mDatabase;
    private Context context;

    FireBaseHandler(Context context) {
        //TODO:implement Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.context = context;
    }

    public void save(Object value, String name, Context context) {

    }

    public Object get(String name, Context context) {
        return 1;
    }

    @Override
    public ArrayList<User> getUsers() {
//        DatabaseReference usersRef = mDatabase.child("users");
//        usersRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot child: dataSnapshot.getChildren()) {
//                    String key = child.getKey();
//                    String value = child.getValue().toString();
//                    // do what you want with key and value
//                    return;
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        ArrayList<User> users = new ArrayList<>();
//        users.add(new User("Бабушка", getMedicationsForUser("stub")));
        users.add(new User("Мама", "mama@gmail.com", new ArrayList<>()));
        users.add(new User("Дочь", "mama@gmail.com", new ArrayList<>()));
        return users;
    }

    @Override
    public ArrayList<Medication> getAllMedications() {
        return null;
    }

    @Override
    public ArrayList<Prescription> getPrescriptionsForUser(String userId) {
        return null;
    }

    @Override
    public ArrayList<Medication> getMedicationsForUser(String userId) {
        return null;
    }


    @Override
    public void addMedication(Medication medication) {
        mDatabase.child(MEDICATIONS).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        writeMedication(mDatabase.child("medications"), medication);
                    }

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }


    private void writeMedication(DatabaseReference databaseref, Medication medication) {
//        String key = mDatabase.child("medications").push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(medication.getName(), medication.toMap());

        databaseref.updateChildren(childUpdates);
    }

    @Override
    public void addMedicationForUser(User user, Medication medication) {
        DatabaseReference usersRef = mDatabase.child("users").child(user.getEmail());
        usersRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + user.getEmail() + " is unexpectedly null");
                            Toast.makeText(context,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            writeMedication(usersRef.child("medications"), medication);
                        }

//                        setEditingEnabled(true);
//                        NavHostFragment.findNavController(NewPostFragment.this)
//                                .navigate(R.id.action_NewPostFragment_to_MainFragment);
                    }

                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
//                        setEditingEnabled(true);
                    }
                });
    }


    public boolean initialized() {
        return false;
    }
}


