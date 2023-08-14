package edu.northeastern.coinnect.activities.login;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.welcome.WelcomeActivity;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import edu.northeastern.coinnect.models.userModels.User.UserModel;
import edu.northeastern.coinnect.repositories.UsersRepository;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText firstNameEditText,
            lastNameEditText,
            usernameEditText,
            passwordEditText,
            confirmPasswordEditText,
            budgetEditText;
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

        submitButton.setOnClickListener(
                v -> {
                    firstName = Objects.requireNonNull(firstNameEditText.getText()).toString().trim();
                    lastName = Objects.requireNonNull(lastNameEditText.getText()).toString().trim();
                    username = Objects.requireNonNull(usernameEditText.getText()).toString().trim();
                    password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
                    confirmPassword =
                            Objects.requireNonNull(confirmPasswordEditText.getText()).toString().trim();
                    monthlyBudget = Objects.requireNonNull(budgetEditText.getText()).toString().trim();

                    // Verifying the password fields match up - if so, register to DB and redirect to
                    // WelcomeActivity
                    if (!password.equals(confirmPassword)) {
                        passwordInputLayout.setError("Passwords do not match!");
                        confirmPasswordInputLayout.setError("Passwords do not match!");
                    } else {
                        passwordInputLayout.setError(null);
                        confirmPasswordInputLayout.setError(null);
                        try {
                            datePass = getIntent().getStringExtra("DATE");
                            newUser = new UserModel(username,
                                    firstName,
                                    lastName,
                                    encryptPass(password),
                                    Integer.parseInt(monthlyBudget));

                            usersRepository.registerUser(handler, getApplicationContext(), newUser);


                        } catch (Exception e) {
                            System.out.printf("Exception %s occurred!%n", e);
                        }
                    }
                });
    }

    private static String encryptPass(String password)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {

        StringBuffer hexStr = new StringBuffer();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes("UTF-8"));

        // goes through the byte array and hashes each character.
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexStr.append('0');
            }
            hexStr.append(hex);
        }
        return hexStr.toString();
    }
}
