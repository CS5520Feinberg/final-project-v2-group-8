package edu.northeastern.coinnect.activities.login;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.home.HomeActivity;
import edu.northeastern.coinnect.activities.welcome.WelcomeActivity;
import edu.northeastern.coinnect.models.AbstractUserModel;
import edu.northeastern.coinnect.models.UserModel;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import edu.northeastern.coinnect.repositories.UsersRepository;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText firstNameEditText, lastNameEditText, usernameEditText,
            passwordEditText, confirmPasswordEditText, budgetEditText;
    private TextInputLayout passwordInputLayout, confirmPasswordInputLayout;
    private Button submitButton;


    private UserModel newUser;
    private UsersRepository usersRepository = UsersRepository.getInstance();
    private FirebaseDBHandler firebaseDBHandler = usersRepository.getFirebaseDbHandler();
    private String firstName, lastName, username, password, confirmPassword, monthlyBudget, datePass;

    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // <editor-fold desc="Views">
        // Initialize Views
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        budgetEditText = findViewById(R.id.budgetEditText);
        submitButton = findViewById(R.id.registerButton);
        // </editor-fold>

        submitButton.setOnClickListener(v -> {
            firstName = Objects.requireNonNull(firstNameEditText.getText()).toString().trim();
            lastName = Objects.requireNonNull(lastNameEditText.getText()).toString().trim();
            username = Objects.requireNonNull(usernameEditText.getText()).toString().trim();
            password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
            confirmPassword = Objects.requireNonNull(confirmPasswordEditText.getText()).toString().trim();
            monthlyBudget = Objects.requireNonNull(budgetEditText.getText()).toString().trim();

        // Verifying the password fields match up - if so, register to DB and redirect to WelcomeActivity
            if (!password.equals(confirmPassword)) {
                passwordInputLayout.setError("Passwords do not match!");
                confirmPasswordInputLayout.setError("Passwords do not match!");
            } else {
                // TODO: Continue with the registration process
                passwordInputLayout.setError(null);
                confirmPasswordInputLayout.setError(null);
                try {
                    // hashing password
                    password = encryptPass(password);

                    datePass = getIntent().getStringExtra("DATE");

                    newUser = new UserModel(username,
                            firstName,
                            lastName,
                            password,
                            Integer.parseInt(monthlyBudget));
                    registerUser(handler, getApplicationContext(), newUser, newUser.getUsername());

                    Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                    intent.putExtra("USER_NAME", newUser.getFirstName());
                    intent.putExtra("DATE", datePass);
                    intent.putExtra("BUDGET", monthlyBudget);

                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    System.out.printf("Exception %s occurred!%n", e);
                }
            }
        });
    }

            public void registerUser(Handler handler, Context activityContext, UserModel user, String username) {
                firebaseDBHandler.getDbInstance().getReference().child("users").get()
                        .addOnCompleteListener(task -> {
                            Object resultValue = task.getResult().getValue();

                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            } else {
                                Log.i(TAG, "Data retrieved from Firebase: " + task.getResult().getValue().toString());
                                HashMap value = (HashMap) task.getResult().getValue();
                                boolean flag = true;


                                for (Object key : value.keySet()) {
                                    if (key.toString().equals(username)) {
                                        flag = false;
                                    }
                                }

                                if (flag) {
                                    Log.i(TAG, String.format("User %s being added to database", username));
                                    firebaseDBHandler.addUser(user);

                                    Log.i(TAG, String.format("User %s being logged in", username));

                                    firebaseDBHandler.setCurrentUserName(username);

                                } else {
                                    Log.i(TAG, String.format("User %s already exists", username));
                                    handler.post(
                                            () -> Toast.makeText(activityContext, "User already exists! ",
                                                    Toast.LENGTH_SHORT).show());
                                }
                            }
                        });
            }

    private static String encryptPass(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        StringBuffer hexStr = new StringBuffer();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes("UTF-8"));

        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexStr.append('0');
            }
                hexStr.append(hex);
        }
        return hexStr.toString();
    }
}
