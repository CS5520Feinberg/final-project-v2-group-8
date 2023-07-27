package edu.northeastern.coinnect.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import edu.northeastern.coinnect.R;

public class RegisterScreen extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        email = findViewById(R.id.email_entry);
        password = findViewById(R.id.password_entry);
        confirmPassword = findViewById(R.id.confirm_password_entry);
        signUpButton = findViewById(R.id.signUpButton);

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Boolean isEqual = password.getText().toString()
                        .equals(confirmPassword.getText().toString());
                signUpButton.setEnabled(isEqual);
            }
        };

        password.addTextChangedListener(tw);
        confirmPassword.addTextChangedListener(tw);
    }
}