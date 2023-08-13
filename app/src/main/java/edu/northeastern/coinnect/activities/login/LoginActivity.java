package edu.northeastern.coinnect.activities.login;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.welcome.WelcomeActivity;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import edu.northeastern.coinnect.models.userModels.User.UserModel;
import edu.northeastern.coinnect.repositories.UsersRepository;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

  // setting up OAUTH2 authentication for 1-tap sign in.
  SignInClient oneTapClient;

  private Handler handler = new Handler();
  private UsersRepository usersRepository = UsersRepository.getInstance();
  private FirebaseDBHandler firebaseDBHandler = usersRepository.getFirebaseDbHandler();
  BeginSignInRequest signUpRequest;
  Button signInButton;
  Button registerButton;
  Button usernameSignInButton;
  private int defaultMonthlyBudget = 2000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);


    registerButton = findViewById(R.id.registerButton);
    usernameSignInButton = findViewById(R.id.signInUsernameButton);

    registerButton.setOnClickListener(
        v -> {
          Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
          startActivity(intent);
        });

    usernameSignInButton.setOnClickListener(
        v -> {
          Intent intent = new Intent(getApplicationContext(), UsernameSignInActivity.class);
          startActivity(intent);
        });

  }

  public void registerUser(
      Handler handler, Context activityContext, UserModel user, String username) {
    firebaseDBHandler
        .getDbInstance()
        .getReference()
        .child(FirebaseDBHandler.USERS_BUCKET_NAME)
        .get()
        .addOnCompleteListener(
            task -> {
              Object resultValue = task.getResult().getValue();

              if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
              } else {
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
                      () ->
                          Toast.makeText(
                                  activityContext, "User already exists! ", Toast.LENGTH_SHORT)
                              .show());
                }
              }
            });
  }
}
