package edu.northeastern.coinnect.activities.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.welcome.WelcomeActivity;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import edu.northeastern.coinnect.repositories.UsersRepository;

public class UsernameSignInActivity extends AppCompatActivity {

    private Button logInButton;
    private static UsersRepository usersRepository = UsersRepository.getInstance();
    private FirebaseDBHandler firebaseDBHandler = usersRepository.getFirebaseDbHandler();
    private static String attemptUsername;
    private static String attemptPassword;
    private EditText userEntry;
    private EditText passwordEntry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username_sign_in);
        logInButton = findViewById(R.id.logInButton);
        userEntry = findViewById(R.id.userLogIn);
        passwordEntry = findViewById(R.id.passwordLogIn);

        logInButton.setOnClickListener(v -> {
            logInFlow(String.valueOf(userEntry.getText()), String.valueOf(passwordEntry.getText()));

        });

    }



    private static String encryptPass(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {

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

    private static boolean comparePasswords(String attemptedPassword, String realPassword) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (encryptPass(attemptedPassword).equals(realPassword)) {
            return true;
        } else return false;

    }

    private void logInFlow(String username, String password) {

        firebaseDBHandler
                .getDbInstance()
                .getReference()
                .child("users/" + username)
                .get()
                .addOnCompleteListener( task -> {
                    Object resultValue = task.getResult().getValue();

                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        HashMap value = (HashMap) task.getResult().getValue();
                        boolean flag = true;

                        for (Object key : value.keySet()) {
                            if (key.toString().equals("password")) {
                                flag = false;
                                String usersHashedPassword = String.valueOf(value.get("password"));
                                try {
                                    if (comparePasswords(password, usersHashedPassword)) {
                                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                                        intent.putExtra("USER_NAME", value.get("firstName").toString());
                                        intent.putExtra("BUDGET", value.get("monthlyBudget").toString());
                                        startActivity(intent);
                                        finish();
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }

                            }
                        }


                    }

                });
    }
}