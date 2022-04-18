package com.akzubarev.homedoctor.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.models.User;
import com.akzubarev.homedoctor.databinding.FragmentSignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

public class SignInFragment extends Fragment implements View.OnClickListener {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static final String TAG = "SignInFragment";

    private boolean signUpModeActive;
    private FragmentSignInBinding binding;

//    private FirebaseDatabase database;
//    private DatabaseReference usersDatabaseReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        binding.loginSignUpButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        } else {
            binding.toggleLoginSignUpTextView.setOnClickListener(this::toggleLoginMode);
            signUpModeActive = false;
            setModeUI();
        }
    }

    private boolean validateInput() {
        EditText emailEditText = binding.emailEditText;
        String email = emailEditText.getText().toString();
        if (email.trim().equals("") || !email.contains("@")) {
            emailEditText.setError("Email should be valid");
            return false;
        } else {
            emailEditText.setError(null);
        }


        EditText passwordEditText = binding.passwordEditText;
        if (passwordEditText.getText().toString().trim().length() < 7) {
            passwordEditText.setError("Password must be at least 7 characters");
            return false;
        } else {
            passwordEditText.setError(null);
        }

        EditText repeatPasswordEditText = binding.passwordEditText;
        if (signUpModeActive && !passwordEditText.getText().toString().trim().equals(
                repeatPasswordEditText.getText().toString().trim())) {
            repeatPasswordEditText.setError("Passwords don't match");
            return false;
        } else {
            repeatPasswordEditText.setError(null);
        }
        return true;
    }

    private void loginSignUpUser() {
//        if (!validateInput()) {
//            Toast.makeText(getContext(), "Not valid input", Toast.LENGTH_SHORT).show();
//            return;
//        }

        String email = "alexkzubarev@gmail.com"; //binding.emailEditText.getText().toString().trim();
        String password = "enotoman1a!adsdasd"; //binding.passwordEditText.getText().toString().trim();

        if (signUpModeActive)
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), task -> {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(getContext(), "Sign Up Failed " + email,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        else
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), task -> {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });


    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        writeNewUser(user.getUid(), username, user.getEmail());
        NavHostFragment.findNavController(this).navigate(R.id.action_SignInFragment_to_nav_home);
    }


    public void toggleLoginMode(View view) {
        signUpModeActive = !signUpModeActive;
        setModeUI();
    }

    private void setModeUI() {
        if (signUpModeActive) {
            binding.loginSignUpButton.setText("Создать аккаунт");
            binding.toggleLoginSignUpTextView.setText("Нажмите чтобы войти");
            binding.repeatPasswordEditText.setVisibility(View.VISIBLE);

        } else {
            binding.loginSignUpButton.setText("Войти");
            binding.toggleLoginSignUpTextView.setText("Нажмите чтобы создать аккаунт");
            binding.repeatPasswordEditText.setVisibility(View.GONE);
        }
    }

    public void onClick(View v) {
        loginSignUpUser();
    }

}
