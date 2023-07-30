package edu.northeastern.coinnect.persistence;

import android.content.Context;
import android.os.Handler;
import com.google.firebase.database.FirebaseDatabase;
import edu.northeastern.coinnect.persistence.entities.User;

public class FirebaseDBHandler {

  private static final String TAG = "_FirebaseDBHandler";
  private static FirebaseDatabase dbInstance;
  private static FirebaseDBHandler INSTANCE;

  private static String currentUserName = null;

  private static void initDbReference() {
    if (dbInstance == null) {
      dbInstance = FirebaseDatabase.getInstance();
    }
  }

  private FirebaseDBHandler() {
    initDbReference();
  }

  public static FirebaseDBHandler getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new FirebaseDBHandler();
    }

    return INSTANCE;
  }

  public FirebaseDatabase getDbInstance() {
    return dbInstance;
  }

  public void setCurrentUserName(String userName) {
    currentUserName = userName;
  }

  public void addUser(String username) {
    User user = new User(username);

    dbInstance.getReference().child("users").child(user.username).setValue(user);
  }
}
