package com.akzubarev.homedoctor.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.User;
import com.akzubarev.homedoctor.databinding.FragmentSignInBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignInFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private static final String TAG = "SignInFragment";

    private boolean signUpModeActive;
    private FragmentSignInBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        binding.loginSignUpButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            alreadySigned();
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
        if (!validateInput()) {
            Toast.makeText(getContext(), "Not valid input", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (signUpModeActive)
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), task -> {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            String username = usernameFromEmail(Objects.requireNonNull(Objects.requireNonNull(user).getEmail()));
                            writeNewUser(user, username, user.getEmail());
                            sign();
                        } else {
                            Toast.makeText(getContext(), "Sign Up Failed " + email,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        else
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), task -> {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            alreadySigned();
                        } else {
                            Log.w(TAG, "signInWithEmailAndPassword:failure", task.getException());
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

    private void writeNewUser(FirebaseUser fbUser, String name, String email) {
        User user = new User(name, email);
        DataHandler dataHandler = DataHandler.getInstance(getContext());
        dataHandler.createUser(fbUser, user);

        dataHandler.saveSettings("10:00", false, "????????????", 3, "????????????????????", 1, () -> {
        });
    }

    private void alreadySigned() {
        NavHostFragment.findNavController(this).navigate(R.id.ProfilesListFragment);
    }


    private void sign() {
        NavHostFragment.findNavController(this).navigate(R.id.ProfileFragment);
    }

    public void toggleLoginMode(View view) {
        signUpModeActive = !signUpModeActive;
        setModeUI();
    }

    private void setModeUI() {
        if (signUpModeActive) {
            binding.loginSignUpButton.setText("?????????????? ??????????????");
            binding.toggleLoginSignUpTextView.setText("?????????????? ?????????? ??????????");
            binding.repeatPasswordEditText.setVisibility(View.VISIBLE);

        } else {
            binding.loginSignUpButton.setText("??????????");
            binding.toggleLoginSignUpTextView.setText("?????????????? ?????????? ?????????????? ??????????????");
            binding.repeatPasswordEditText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_info).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        return super.onOptionsItemSelected(menuItem);
    }

    public void onClick(View v) {
        loginSignUpUser();
    }

}
