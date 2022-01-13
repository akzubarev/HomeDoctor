package com.akzubarev.homedoctor.ui.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.akzubarev.homedoctor.R;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

//    private FirebaseAuth auth;

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;
    private TextView toggleLoginSignUpTextView;
    private Button loginSignUpButton;

    private boolean signUpModeActive;

//    private FirebaseDatabase database;
//    private DatabaseReference usersDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

//        auth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//        usersDatabaseReference = database.getReference().child("users");

//        if (auth.getCurrentUser() != null) {
//            startActivity(new Intent(this, MainActivity.class));
//        } else {
//            emailEditText = findViewById(R.id.emailEditText);
//            passwordEditText = findViewById(R.id.passwordEditText);
//            repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);
//            toggleLoginSignUpTextView = findViewById(R.id.toggleLoginSignUpTextView);
//            loginSignUpButton = findViewById(R.id.loginSignUpButton);
//
//            toggleLoginSignUpTextView.setOnClickListener(this::toggleLoginMode);
//
//            signUpModeActive = false;
//            setModeUI();
//        }
    }

    public void signUp(View v) {
        loginSignUpUser(emailEditText.getText().toString().trim(),
                passwordEditText.getText().toString().trim());
    }


    private boolean checkInput() {
        if (passwordEditText.getText().toString().trim().length() < 7) {
            Toast.makeText(this,
                    "Password must be at least 7 characters",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (emailEditText.getText().toString().trim().equals("")) {
            Toast.makeText(this,
                    "Please input your email",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (signUpModeActive && !passwordEditText.getText().toString().trim().equals(
                repeatPasswordEditText.getText().toString().trim())) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void loginSignUpUser(String email, String password) {
//        if (checkInput()) {
//            if (signUpModeActive)
//                auth.createUserWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(this, task -> {
//                            if (task.isSuccessful()) {
//                                FirebaseUser user = auth.getCurrentUser();
//                                if (user != null) {
//                                    createUser(user);
//                                }
//                                Intent intent = new Intent(this, MainActivity.class);
//                                startActivity(intent);
//                            } else
//                                Toast.makeText(this, "Authentication failed.",
//                                        Toast.LENGTH_SHORT).show();
//                        });
//            else
//                auth.signInWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(this, task -> {
//                            if (task.isSuccessful()) {
//                                FirebaseUser user = auth.getCurrentUser();
//                                Intent intent = new Intent(this, MainActivity.class);
//                                startActivity(intent);
//                            } else
//                                Toast.makeText(this, "Authentication failed.",
//                                        Toast.LENGTH_SHORT).show();
//                        });
//
//        }
    }

//    private void createUser(FirebaseUser firebaseUser) {
//        User user = new User();
//        user.setId(firebaseUser.getUid());
//        user.setEmail(firebaseUser.getEmail());
//
//        usersDatabaseReference.child(firebaseUser.getUid()).setValue(user);
//    }

    public void toggleLoginMode(View view) {
        signUpModeActive = !signUpModeActive;
        setModeUI();
    }

    private void setModeUI() {
        if (signUpModeActive) {
            loginSignUpButton.setText("Создать аккаунт");
            toggleLoginSignUpTextView.setText("Нажмите чтобы войти");
            repeatPasswordEditText.setVisibility(View.VISIBLE);

        } else {
            loginSignUpButton.setText("Войти");
            toggleLoginSignUpTextView.setText("Нажмите чтобы создать аккаунт");
            repeatPasswordEditText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            finishAndRemoveTask();
        else
            finishAffinity();
    }

}
