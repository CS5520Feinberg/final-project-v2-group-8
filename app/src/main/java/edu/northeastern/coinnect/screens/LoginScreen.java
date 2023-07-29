package edu.northeastern.coinnect.screens;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

public class LoginScreen extends AppCompatActivity {

  // setting up OAUTH2 authentication for 1-tap sign in.
  SignInClient oneTapClient;
  BeginSignInRequest signUpRequest;
  Button signUpButton;

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
    signUpButton = findViewById(R.id.signInButton);
    oneTapClient = Identity.getSignInClient(this);
    signUpRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
            .setSupported(true)
            .setServerClientId(getString(R.string.web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build())
        .build();

    /*
     * Launching the Google sign in flow. If the user returns a token (meaning we find their google
     * acct), we can launch a loading animation (?) and redirect to the home page.
     */
    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartIntentSenderForResult(), result -> {
          if (result.getResultCode() == Activity.RESULT_OK) {
            try {
              SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(
                  result.getData());
              String idToken = credential.getGoogleIdToken();
              if (idToken != null) {
                String name = credential.getDisplayName();

                Intent intent = new Intent(LoginScreen.this, WelcomeScreen.class);
                intent.putExtra("USER_NAME", name);

                startActivity(intent);
                finish();
              }
            } catch (ApiException e) {
              e.printStackTrace();
            }
          }

        });

    signUpButton.setOnClickListener(v -> oneTapClient.beginSignIn(signUpRequest)
        .addOnSuccessListener(LoginScreen.this, result -> {
          IntentSenderRequest intentSenderRequest =
              new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
          activityResultLauncher.launch(intentSenderRequest);
        })
        .addOnFailureListener(LoginScreen.this, e -> {
          // No Google Accounts found. Just continue presenting the signed-out UI.
          Log.d(TAG, e.getLocalizedMessage());
        }));
  }
}