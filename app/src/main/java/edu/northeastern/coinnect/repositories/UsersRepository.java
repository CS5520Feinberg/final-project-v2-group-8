package edu.northeastern.coinnect.repositories;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import edu.northeastern.coinnect.models.AbstractUserModel;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import edu.northeastern.coinnect.activities.welcome.WelcomeActivity;
import java.util.HashMap;

public class UsersRepository {
  private static final String TAG = "_UsersRepository";
  private final static FirebaseDBHandler firebaseDbHandler = FirebaseDBHandler.getInstance();

  private static UsersRepository INSTANCE;

  private UsersRepository() {
  }

  public static UsersRepository getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new UsersRepository();
    }

    return INSTANCE;
  }

  public FirebaseDBHandler getFirebaseDbHandler() {
    return firebaseDbHandler;
  }

  // TODO: use this method to register user
  public void registerUser(Handler handler, Context activityContext, AbstractUserModel user) {
    firebaseDbHandler.getDbInstance().getReference().child("users").get()
        .addOnCompleteListener(task -> {
          if (!task.isSuccessful()) {
            Log.e("firebase", "Error getting data", task.getException());
          } else {
            Log.e("firebase", "hey whats up you made it here");
            HashMap value = (HashMap) task.getResult().getValue();
            boolean flag = true;
            for (Object key : value.keySet()) {
              Log.e("firebase", key.toString());
              if (key.toString().equals(user.getUsername())) {
                flag = false;
              }
            }

            if (flag) {
              Log.i(TAG, String.format("User %s being added to database", user.getUsername()));
              firebaseDbHandler.addUser(user);

              Log.i(TAG, String.format("User %s being logged in", user.getUsername()));

              firebaseDbHandler.setCurrentUserName(user.getUsername());
              handler.post(() -> {
                Intent intent = new Intent(activityContext, WelcomeActivity.class);
                activityContext.startActivity(intent);
              });
            } else {
              Log.i(TAG, String.format("User %s already exists", user.getUsername()));
              handler.post(
                  () -> Toast.makeText(activityContext, "User already exists! ",
                      Toast.LENGTH_SHORT).show());
            }
          }
        });
  }

  // TODO: use this method to login user
  public void loginUser(Handler handler, Context activityContext, String userName) {
    firebaseDbHandler.getDbInstance().getReference().child("users").get()
        .addOnCompleteListener(task -> {
          if (!task.isSuccessful()) {
            Log.e(TAG, "Error getting data", task.getException());
          } else {
            HashMap value = (HashMap) task.getResult().getValue();
            boolean flag = true;
            for (Object key : value.keySet()) {
              if (key.toString().equals(userName)) {
                flag = false;
              }
            }

            if (!flag) {
              Log.i(TAG, String.format("User %s being logged in", userName));

              firebaseDbHandler.setCurrentUserName(userName);
              handler.post(() -> {
                Intent intent = new Intent(activityContext, WelcomeActivity.class);
                activityContext.startActivity(intent);
              });
            } else {
              Log.i(TAG, String.format("User %s does not exist", userName));
              handler.post(() -> Toast.makeText(activityContext,
                  "User does not exist, please register. ", Toast.LENGTH_SHORT).show());
            }
          }
        });
  }
}
