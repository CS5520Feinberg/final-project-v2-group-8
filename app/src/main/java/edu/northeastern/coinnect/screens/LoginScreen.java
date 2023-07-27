package edu.northeastern.coinnect.screens;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;

import edu.northeastern.coinnect.R;

public class LoginScreen extends AppCompatActivity {

  SignInClient oneTapClient;
  BeginSignInRequest googleSignUpRequest;
  Button googleSignInButton;
  Button registerButton;
  Animation moveOut;
  TextView title;
  TextView slogan;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    // assigning id's to variables for later use.
    title = findViewById(R.id.titleWelcome);
    slogan = findViewById(R.id.slogan);
    moveOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_fade_out);
    registerButton = findViewById(R.id.registerButton);
    googleSignInButton = findViewById(R.id.signInButton);

    /*
     * grabbing the signUpButton from the layout and attaching the SignInRequest logic.
     * -- note --
     *  This was largely implemented by following the following documentation: https://developer.android.com/training/id-auth/authenticate
     *
     */
    oneTapClient = Identity.getSignInClient(this);
    googleSignUpRequest = BeginSignInRequest.builder()
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
    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
      if (result.getResultCode() == Activity.RESULT_OK) {
        try {
          SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
          String idToken = credential.getGoogleIdToken();
          if (idToken !=  null) {
            String name = credential.getDisplayName();
            runAnimations();
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

    /*
     * setting the one tap flow to attach to the button for signing in with Google.
     */
    googleSignInButton.setOnClickListener(v -> oneTapClient.beginSignIn(googleSignUpRequest)
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

  public void launchRegisterAction(View view) {
    Intent intent = new Intent(LoginScreen.this, RegisterScreen.class);
    runAnimations();
    startActivity(intent);
    finish();
  }

  private void runAnimations() {
    title.startAnimation(moveOut);
    slogan.startAnimation(moveOut);
    googleSignInButton.startAnimation(moveOut);
    registerButton.startAnimation(moveOut);
  }
}