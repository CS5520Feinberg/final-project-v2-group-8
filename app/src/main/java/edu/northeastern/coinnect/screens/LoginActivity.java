package edu.northeastern.coinnect.screens;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import edu.northeastern.coinnect.R;

public class LoginActivity extends AppCompatActivity {

  // setting up OAUTH2 authentication for 1-tap sign in.
  SignInClient oneTapClient;
  BeginSignInRequest signUpRequest;
  Button signUpButton;

  private static final int REQ_ONE_TAP = 2;
  private boolean showOneTapUI = true;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    signUpButton = findViewById(R.id.signInButton);
    oneTapClient = Identity.getSignInClient(this);
    signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build();

    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
      if (result.getResultCode() == Activity.RESULT_OK) {
        try {
          SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
          String idToken = credential.getGoogleIdToken();
          if (idToken !=  null) {
            String email = credential.getId();
            // TODO showing toast for now
            Toast.makeText(getApplicationContext(), "Email: "+ email, Toast.LENGTH_SHORT).show();
          }
        } catch (ApiException e) {
          e.printStackTrace();
        }
      }

    });

    signUpButton.setOnClickListener(v -> {
      oneTapClient.beginSignIn(signUpRequest)
              .addOnSuccessListener(LoginActivity.this, result -> {
                IntentSenderRequest intentSenderRequest =
                        new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                activityResultLauncher.launch(intentSenderRequest);
              })
              .addOnFailureListener(LoginActivity.this, e -> {
                // No Google Accounts found. Just continue presenting the signed-out UI.
                Log.d(TAG, e.getLocalizedMessage());
              });
    });
  }
}