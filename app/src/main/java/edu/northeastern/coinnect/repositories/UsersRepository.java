package edu.northeastern.coinnect.repositories;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import edu.northeastern.coinnect.models.userModels.User.AbstractUserModel;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import edu.northeastern.coinnect.activities.welcome.WelcomeActivity;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

  public CompletableFuture<Boolean> isUser(String searchUser) {return this.getFirebaseDbHandler().isUser(searchUser);}

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

  public void registerUser(Handler handler, Context activityContext, AbstractUserModel user) {
    firebaseDbHandler.setCurrentUserName(user.getUsername());
    this.setCurrentUserName(user.getUsername());
    this.setMonthlyBudget(String.valueOf(user.getMonthlyBudget()));
    this.setUserFirstName(user.getFirstName());
    this.setUserLastName(user.getLastName());

    firebaseDbHandler
            .getDbInstance()
            .getReference()
            .child(FirebaseDBHandler.USERS_BUCKET_NAME)
            .get()
            .addOnCompleteListener(
                    task -> {
                      if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                      } else {
                        HashMap value = (HashMap) task.getResult().getValue();
                        boolean flag = true;

                        for (Object key : value.keySet()) {
                          if (key.toString().equals(user.getUsername())) {
                            flag = false;
                          }
                        }

                        if (flag) {
                          try {
                            this. encryptPass(user.getPassword());
                          } catch (Exception e) {
                            Log.i(TAG, "Error encrypting password: " + e);
                          }
                          firebaseDbHandler.addUser(user);

                          Intent intent = new Intent(activityContext, WelcomeActivity.class);
                          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                          activityContext.startActivity(intent);

                        } else {
                          handler.post(() -> Toast.makeText(activityContext, "User already exists! ", Toast.LENGTH_SHORT).show());
                        }
                      }
                    });
  }

  public void loginUser(Handler handler, Context activityContext, String userName, String password) {

    Task<DataSnapshot> firebaseCall =
            firebaseDbHandler
                    .getDbInstance()
                    .getReference()
                    .child(FirebaseDBHandler.USERS_BUCKET_NAME)
                    .child(userName)
                    .get();

    if (firebaseCall == null) {
      handler.post(
              () ->
                  Toast
                    .makeText(activityContext, "Incorrect login information!", Toast.LENGTH_SHORT)
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
                          firebaseDbHandler.setCurrentUserName(userName);
                          this.setCurrentUserName(value.get("username").toString());
                          this.setMonthlyBudget(value.get("monthlyBudget").toString());
                          this.setUserFirstName(value.get("firstName").toString());
                          this.setUserLastName(value.get("lastName").toString());

                          Intent intent = new Intent(activityContext, WelcomeActivity.class);
                          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                          activityContext.startActivity(intent);

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

  public boolean comparePasswords(String attemptedPassword, String realPassword)
    throws UnsupportedEncodingException, NoSuchAlgorithmException {
      if (encryptPass(attemptedPassword).equals(realPassword)) {
        return true;
      } else return false;
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



  public void getUserFriendsList() {
      firebaseDbHandler.getCurrentUserFriends().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
        @Override
        public void onSuccess(DataSnapshot dataSnapshot) {
          setCurrentUserFriendsList((List<String>) dataSnapshot.getValue());
        }
      });
  }

}
