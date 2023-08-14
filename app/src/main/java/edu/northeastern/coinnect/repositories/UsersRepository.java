package edu.northeastern.coinnect.repositories;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

import edu.northeastern.coinnect.models.userModels.User.AbstractUserModel;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import edu.northeastern.coinnect.activities.welcome.WelcomeActivity;
import java.util.HashMap;
import java.util.List;

public class UsersRepository {
  private static final String TAG = "_UsersRepository";
  private final static FirebaseDBHandler firebaseDbHandler = FirebaseDBHandler.getInstance();

  private static UsersRepository INSTANCE;

  private String monthlyBudget;
  private String currentUserName;
  private String userFirstName;
  private String userLastName;

  private List<String> currentUserFriendsList;

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

  public String getCurrentUserName() {
    return this.getFirebaseDbHandler().getCurrentUserName();
  }

  public void setCurrentUserName(String username) {
    this.currentUserName = username;
  }

  public String getMonthlyBudget() {
    return monthlyBudget;
  }

  public void setMonthlyBudget(String amount) {
    this.monthlyBudget = amount;
  }

  public void setUserFirstName(String firstName) {
    this.userFirstName = firstName;
  }

  public void setUserLastName(String lastName) {
    this.userLastName = lastName;
  }

  public String getUserFirstName() {
    return this.userFirstName;
  }

  public String getUserLastName() {
    return this.userLastName;
  }

  public List<String> getCurrentUserFriends() {
    return this.currentUserFriendsList;
  }

  public void setCurrentUserFriendsList(List<String> list) {
    this.currentUserFriendsList = list;
  }

  // TODO: use this method to register user
  public void registerUser(Handler handler, Context activityContext, AbstractUserModel user, String username) {
    firebaseDbHandler.getDbInstance().getReference().child(FirebaseDBHandler.USERS_BUCKET_NAME).get()
            .addOnCompleteListener(task -> {
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
                  firebaseDbHandler.addUser(user);

                  Log.i(TAG, String.format("User %s being logged in", username));

                  firebaseDbHandler.setCurrentUserName(username);
                  handler.post(() -> {
                    Intent intent = new Intent(activityContext, WelcomeActivity.class);
                    activityContext.startActivity(intent);
                  });
                } else {
                  Log.i(TAG, String.format("User %s already exists", username));
                  handler.post(
                          () -> Toast.makeText(activityContext, "User already exists! ",
                                  Toast.LENGTH_SHORT).show());
                }
              }
            });
  }

  // TODO: use this method to login user
  public void loginUser(Handler handler, Context activityContext, String userName) {

    firebaseDbHandler.getDbInstance().getReference().child(FirebaseDBHandler.USERS_BUCKET_NAME).get()
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

  public void getUserFriendsList() {
      firebaseDbHandler.getCurrentUserFriends().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
        @Override
        public void onSuccess(DataSnapshot dataSnapshot) {
          setCurrentUserFriendsList((List<String>) dataSnapshot.getValue());
        }
      });
  }

}
