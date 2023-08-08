package edu.northeastern.coinnect.activities.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;

import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.welcome.WelcomeActivity;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import edu.northeastern.coinnect.repositories.UsersRepository;

public class UsernameSignInActivity extends AppCompatActivity {

    private Button logInButton;
    private UsersRepository usersRepository = UsersRepository.getInstance();
    private FirebaseDBHandler firebaseDBHandler = usersRepository.getFirebaseDbHandler();
    private String attemptUsername;
    private String attemptPassword;
    private EditText userEntry;
    private EditText passwordEntry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username_sign_in);
    }

    private static String encryptPass(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        StringBuffer hexStr = new StringBuffer();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes("UTF-8"));

        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexStr.append('0');
                hexStr.append(hex);
            }
        }
        return hexStr.toString();
    }

    private static boolean comparePasswords(String attemptedPassword, String realPassword) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (encryptPass(attemptedPassword).equals(realPassword)) {
            return true;
        } else return false;

    }
}