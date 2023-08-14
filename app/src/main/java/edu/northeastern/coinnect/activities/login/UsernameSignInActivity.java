package edu.northeastern.coinnect.activities.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.welcome.WelcomeActivity;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import edu.northeastern.coinnect.repositories.UsersRepository;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class UsernameSignInActivity extends AppCompatActivity {

  private Button logInButton;
  private Handler handler = new Handler();
  private static UsersRepository usersRepository = UsersRepository.getInstance();
  private EditText userEntry;
  private EditText passwordEntry;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_username_sign_in);
    logInButton = findViewById(R.id.logInButton);
    userEntry = findViewById(R.id.userLogIn);
    passwordEntry = findViewById(R.id.passwordLogIn);

    logInButton.setOnClickListener(
        v -> {
          usersRepository.loginUser(
                  handler,
                  UsernameSignInActivity.this,
                  userEntry.getText().toString(),
                  passwordEntry.getText().toString());
        });
  }
}
