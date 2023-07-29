package edu.northeastern.coinnect.persistence;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDBHandler {

  private static final String TAG = "_FirebaseDBHandler";
  private static FirebaseDatabase dbInstance;

  private static FirebaseDBHandler INSTANCE;

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
}
