package edu.northeastern.coinnect.activities.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText firstNameEditText, passwordEditText, confirmPasswordEditText;
    private TextInputLayout passwordInputLayout, confirmPasswordInputLayout;
    private Button submitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Views
        firstNameEditText = findViewById(R.id.firstNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        submitButton = findViewById(R.id.registerButton);


        // Verifying the password fields match up - if so, register to DB and redirect to WelcomeActivity
        submitButton.setOnClickListener(v -> {
            String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
            String confirmPassword = Objects.requireNonNull(confirmPasswordEditText.getText()).toString().trim();
            String firstName = firstNameEditText.getText().toString().trim();
            if (!password.equals(confirmPassword)) {
                passwordInputLayout.setError("Passwords do not match!");
                confirmPasswordInputLayout.setError("Passwords do not match!");
            } else {
            // TODO: Continue with the registration process
                passwordInputLayout.setError(null);
                confirmPasswordInputLayout.setError(null);
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                intent.putExtra("USER_NAME", firstName);
                startActivity(intent);
                finish();
            }


        });
    }
}