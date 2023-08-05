package edu.northeastern.coinnect.activities.login;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import edu.northeastern.coinnect.activities.home.HomeActivity;

public class LoginActivity extends AppCompatActivity {

  // setting up OAUTH2 authentication for 1-tap sign in.
  SignInClient oneTapClient;
  BeginSignInRequest signUpRequest;
  Button signInButton;
  Button registerButton;

  Button freepassBtn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    /*
     * grabbing the signUpButton from the layout and attaching the SignInRequest logic.
     * -- note --
     *  This was largely implemented by following the following documentation: https://developer.android.com/training/id-auth/authenticate
     *
     */
    signInButton = findViewById(R.id.signInButton);
    registerButton = findViewById(R.id.registerButton);
    oneTapClient = Identity.getSignInClient(this);

    //test
    freepassBtn = findViewById(R.id.freePassBtn);

    freepassBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
      }
    });

    signUpRequest =
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build();

    /*
     * Launching the Google sign in flow. If the user returns a token (meaning we find their google
     * acct), we can launch a loading animation and redirect to the home page.
     */
    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher =
        registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            result -> {
              if (result.getResultCode() == Activity.RESULT_OK) {
                try {
                  SignInCredential credential =
                      oneTapClient.getSignInCredentialFromIntent(result.getData());
                  String idToken = credential.getGoogleIdToken();
                  if (idToken != null) {
                    String name = credential.getDisplayName();

                    Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                    intent.putExtra("USER_NAME", name);

                    startActivity(intent);
                    finish();
                  }
                } catch (ApiException e) {
                  Toast.makeText(LoginActivity.this, "Exception encountered!", Toast.LENGTH_SHORT)
                      .show();
                  e.printStackTrace();
                }
              }
            });

    signInButton.setOnClickListener(
        v ->
            oneTapClient
                .beginSignIn(signUpRequest)
                .addOnSuccessListener(
                    LoginActivity.this,
                    result -> {
                      IntentSenderRequest intentSenderRequest =
                          new IntentSenderRequest.Builder(
                                  result.getPendingIntent().getIntentSender())
                              .build();
                      activityResultLauncher.launch(intentSenderRequest);
                    })
                .addOnFailureListener(
                    LoginActivity.this,
                    e -> {
                      // No Google Accounts found. Just continue presenting the signed-out UI.
                      Log.d(TAG, e.getLocalizedMessage());
                      Toast.makeText(
                              LoginActivity.this, "No google account found!", Toast.LENGTH_SHORT)
                          .show();
                    }));
  }
}
