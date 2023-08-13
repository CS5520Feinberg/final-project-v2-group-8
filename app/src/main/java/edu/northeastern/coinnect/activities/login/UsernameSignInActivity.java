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

    logInButton.setOnClickListener(
        v -> {
          logInFlow(
              String.valueOf(userEntry.getText()),
              String.valueOf(passwordEntry.getText()),
              getApplicationContext());
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

  private static boolean comparePasswords(String attemptedPassword, String realPassword)
      throws UnsupportedEncodingException, NoSuchAlgorithmException {
    if (encryptPass(attemptedPassword).equals(realPassword)) {
      return true;
    } else return false;
  }

  private void logInFlow(String username, String password, Context activityContext) {
    Task<DataSnapshot> firebaseCall =
        firebaseDBHandler
            .getDbInstance()
            .getReference()
            .child(FirebaseDBHandler.USERS_BUCKET_NAME)
            .child(username)
            .get();

    if (firebaseCall == null) {
      handler.post(
          () ->
              Toast.makeText(activityContext, "Incorrect login information!", Toast.LENGTH_SHORT)
                  .show());
    } else {

      firebaseCall.addOnCompleteListener(
          task -> {
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
                      firebaseDBHandler.setCurrentUserName(username);
                      usersRepository.setMonthlyBudget(value.get("monthlyBudget").toString());
                      usersRepository.setUserFirstName(value.get("firstName").toString());
                      usersRepository.setUserLastName(value.get("lastName").toString());

                      Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                      startActivity(intent);
                      finish();
                    } else {
                      handler.post(
                          () ->
                              Toast.makeText(
                                      activityContext,
                                      "Incorrect login information!",
                                      Toast.LENGTH_SHORT)
                                  .show());
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
}
