package edu.northeastern.coinnect.activities.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
    private String firstName, lastName, username, password, confirmPassword;
    private int monthlyBudget;

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

        // Verifying the password fields match up - if so, register to DB and redirect to WelcomeActivity
        submitButton.setOnClickListener(v -> {
            firstName = Objects.requireNonNull(firstNameEditText.getText()).toString().trim();
            lastName = Objects.requireNonNull(lastNameEditText.getText()).toString().trim();
            username = Objects.requireNonNull(usernameEditText.getText()).toString().trim();
            password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
            confirmPassword = Objects.requireNonNull(confirmPasswordEditText.getText()).toString().trim();
            monthlyBudget = Integer.parseInt(Objects.requireNonNull(budgetEditText.getText()).toString().trim());

            if (!password.equals(confirmPassword)) {
                passwordInputLayout.setError("Passwords do not match!");
                confirmPasswordInputLayout.setError("Passwords do not match!");
            } else {
            // TODO: Continue with the registration process
                passwordInputLayout.setError(null);
                confirmPasswordInputLayout.setError(null);
                try {
                    newUser = new UserModel(firstName, lastName, username, password, monthlyBudget);
                    usersRepository.registerUser(handler, getApplicationContext(), newUser);
                    Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                    intent.putExtra("USER_NAME", firstName);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    System.out.printf("Exception %s occurred!%n", e);
                }
            }


        });
    }
}