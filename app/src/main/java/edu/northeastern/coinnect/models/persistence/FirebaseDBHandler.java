package edu.northeastern.coinnect.models.persistence;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import edu.northeastern.coinnect.activities.pending.PendingTransactionsRecyclerViewAdapter;
import edu.northeastern.coinnect.activities.transactions.TransactionsRecyclerViewAdapter;
import edu.northeastern.coinnect.models.persistence.entities.FriendsListCallback;
import edu.northeastern.coinnect.models.persistence.entities.GroupTransactionEntity;
import edu.northeastern.coinnect.models.persistence.entities.GroupTransactionEntityCallback;
import edu.northeastern.coinnect.models.persistence.entities.GroupTransactionShareEntity;
import edu.northeastern.coinnect.models.persistence.entities.PendingTransactionEntity;
import edu.northeastern.coinnect.models.persistence.entities.PendingTransactionEntityCallback;
import edu.northeastern.coinnect.models.persistence.entities.TransactionEntity;
import edu.northeastern.coinnect.models.persistence.entities.TransactionEntityCallback;
import edu.northeastern.coinnect.models.persistence.entities.TransactionIdCallback;
import edu.northeastern.coinnect.models.persistence.entities.UserEntity;
import edu.northeastern.coinnect.models.transactionModels.AbstractTransactionModel;
import edu.northeastern.coinnect.models.transactionModels.AbstractTransactionModelCallback;
import edu.northeastern.coinnect.models.transactionModels.DayTransactionsModel;
import edu.northeastern.coinnect.models.transactionModels.GroupTransactionModel;
import edu.northeastern.coinnect.models.transactionModels.MonthTransactionsModel;
import edu.northeastern.coinnect.models.transactionModels.PendingTransactionModel;
import edu.northeastern.coinnect.models.transactionModels.TransactionModel;
import edu.northeastern.coinnect.models.transactionModels.TransactionModelCallback;
import edu.northeastern.coinnect.models.userModels.User.AbstractUserModel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FirebaseDBHandler {

  // <editor-fold desc="Firebase Db Handler Attributes">

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

  // </editor-fold>

  // <editor-fold desc="Constants">

  public static final String USERS_BUCKET_NAME = "USERS";
  public static final String TRANSACTIONS_BUCKET_NAME = "TRANSACTIONS";
  public static final String FRIENDS_BUCKET_NAME = "FRIENDS";
  public static final String PENDING_TRANSACTIONS_BUCKET_NAME = "PENDING_TRANSACTIONS";
  public static final String GROUP_TRANSACTIONS_BUCKET_NAME = "GROUP_TRANSACTIONS";

  public static final String TRANSACTION_ID_COUNTER = "GLOBAL_TRANSACTION_ID_COUNTER";
  public static final String GROUP_TRANSACTION_ID_COUNTER = "GLOBAL_GROUP_TRANSACTION_ID_COUNTER";

  // </editor-fold>

  // <editor-fold desc="Validators">

  private void validate_notNullEntity(Object o, String entityName) {
    if (o == null) {
      throw new UnsupportedOperationException(
          String.format("The entity %s was not found!", entityName));
    }
  }

  private void validate_currentUserIsSet() {
    if (currentUserName == null) {
      throw new UnsupportedOperationException("User needs to be set to make user operations!");
    }
  }

  private void validate_usersAreFriends(Set<String> userNames) {
    this.validate_currentUserIsSet();
    this.getCurrentUserFriends(
        friendsList -> {
          for (String userName : userNames) {
            if (!friendsList.contains(userName) && !userName.equals(this.getCurrentUserName())) {
              throw new UnsupportedOperationException(
                  String.format("User %s is not a friend or doesn't exist!", userName));
            }
          }
        });
  }

  private void validate_isRegularTransaction(TransactionEntity transactionEntity) {
    if (transactionEntity.getIsGroupTransaction()) {
      throw new IllegalArgumentException("This transaction is already a group transaction!");
    }
  }

  private void validate_isGroupTransaction(TransactionEntity transactionEntity) {
    if (!transactionEntity.getIsGroupTransaction()) {
      throw new IllegalArgumentException("This transaction is not a group transaction!");
    }
  }

  private void validate_amountPaid(Double amountPaid) {
    if (amountPaid == null || amountPaid.compareTo(new Double(0)) <= 0) {
      throw new IllegalArgumentException("Amount paid cannot be lesser than or equal to zero!");
    }
  }

  private void validate_sharesAddUpToTotalAmount(
      Double totalAmount, Map<String, Double> userShares) {
    Double calculatingTotal = new Double(0);

    for (Double share : userShares.values()) {
      calculatingTotal = calculatingTotal + share;
    }

    if (!totalAmount.equals(calculatingTotal)) {
      throw new UnsupportedOperationException("User shares need to add up to total Amount!");
    }
  }

  private void validate_sharesPaidAmountRespectsOwedAmount(
      GroupTransactionShareEntity currentUserShareEntity, Double amountPaid) {
    if (amountPaid.compareTo(currentUserShareEntity.getAmountOwed()) > 0) {
      throw new IllegalArgumentException("Amount paid cannot be greater than amount owed!");
    }
  }

  // </editor-fold>

  // <editor-fold desc="Users">

  public String getCurrentUserName() {
    return currentUserName;
  }

  public void setCurrentUserName(String userName) {
    currentUserName = userName;
  }

  public void removeCurrentUserName() {
    currentUserName = null;
  }

  public void addUser(AbstractUserModel user) {
    UserEntity userEntity =
        new UserEntity(
            user.getUsername(),
            user.getFirstName(),
            user.getLastName(),
            user.getPassword(),
            user.getMonthlyBudget());

    dbInstance
        .getReference()
        .child(FirebaseDBHandler.USERS_BUCKET_NAME)
        .child(userEntity.username)
        .setValue(userEntity);
  }

  // </editor-fold>

  // <editor-fold desc="Friends">

  /**
   * isUser -- calls the DB async and returns a response -- TRUE if the username is found, false
   * otherwise
   *
   * @param userName -- string -- the name of the user to search in the DB.
   * @return -- a boolean value on complete.
   */
  public CompletableFuture<Boolean> isUser(String userName) {
    // handles the async call to the DB.
    CompletableFuture<Boolean> future = new CompletableFuture<>();

    // query the DB to see if we get a match for an entered username. If so, return true.
    getUserDatabaseReference()
        .child(userName)
        .addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                  Log.d("USER", "User found");
                  future.complete(true);
                } else {
                  Log.d("USER", "User NOT found");
                  future.complete(false);
                }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
              }
            });
    return future;
  }

  /** getUserDatabaseReference -- returns a database reference to the User's collection. */
  private DatabaseReference getUserDatabaseReference() {
    return dbInstance.getReference().child(USERS_BUCKET_NAME);
  }

  private DatabaseReference getUserFriendsDatabaseReference(String userName) {
    return dbInstance
        .getReference()
        .child(USERS_BUCKET_NAME)
        .child(userName)
        .child(FRIENDS_BUCKET_NAME);
  }

  private DatabaseReference getCurrentUserFriendsDatabaseReference() {
    this.validate_currentUserIsSet();
    return getUserFriendsDatabaseReference(this.getCurrentUserName());
  }

  public void addFriendsChildEventListener(ChildEventListener childEventListener) {
    this.getCurrentUserFriendsDatabaseReference().addChildEventListener(childEventListener);
  }

  public void getCurrentUserFriends(FriendsListCallback friendsListCallback) {

    DatabaseReference friendsReference = this.getCurrentUserFriendsDatabaseReference();
    friendsReference
        .get()
        .addOnSuccessListener(
            dataSnapshot -> {
              List<String> friendsList = new ArrayList<>();
              for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                friendsList.add(friendSnapshot.getValue(String.class));
              }
              friendsListCallback.onCallback(friendsList);
            });
  }

  public void addFriend(String friendUserName) {
    DatabaseReference friendsReference = this.getCurrentUserFriendsDatabaseReference();
    DatabaseReference othersFriendsReference = this.getUserFriendsDatabaseReference(friendUserName);

    friendsReference.child(friendUserName).setValue(friendUserName);
    othersFriendsReference.child(currentUserName).setValue(this.getCurrentUserName());
  }

  public DatabaseReference getUserDatabaseRef(String username) {
    return dbInstance.getReference().child(USERS_BUCKET_NAME).child(username);
  }

  public void setNewPass(String encryptedPass) {
    getUserDatabaseRef(getCurrentUserName()).child("password").setValue(encryptedPass);
  }

  // </editor-fold>

  // <editor-fold desc="Transactions">

  private Task<DataSnapshot> getNewTransactionId(TransactionIdCallback callback) {
    // NOTE: We are not splitting this up because we need to ensure this happens in one transaction
    // with the Database, ensuring the transactionIds are always unique when set.
    this.validate_currentUserIsSet();

    DatabaseReference userTransactionIdCounterReference =
        dbInstance
            .getReference()
            .child(USERS_BUCKET_NAME)
            .child(this.getCurrentUserName())
            .child(TRANSACTION_ID_COUNTER);

    return userTransactionIdCounterReference
        .get()
        .addOnSuccessListener(
            dataSnapshot -> {
              try {
                if (dataSnapshot.getValue() == null) {
                  userTransactionIdCounterReference.setValue(0);
                  callback.onCallback(0);
                } else {
                  Integer result = dataSnapshot.getValue(Integer.class);
                  userTransactionIdCounterReference.setValue(result + 1);
                  callback.onCallback(result);
                }
              } catch (NullPointerException e) {
                userTransactionIdCounterReference.setValue(0);
                callback.onCallback(0);
              }
            });
  }

  private Task<DataSnapshot> getNewGroupTransactionId(TransactionIdCallback callback) {
    // NOTE: We are not splitting this up because we need to ensure this happens in one transaction
    // with the Database, ensuring the transactionIds are always unique when set.
    this.validate_currentUserIsSet();

    DatabaseReference globalGroupTransactionIdCounterReference =
        dbInstance.getReference().child(GROUP_TRANSACTION_ID_COUNTER);

    return globalGroupTransactionIdCounterReference
        .get()
        .addOnSuccessListener(
            dataSnapshot -> {
              try {
                if (dataSnapshot.getValue() == null) {
                  globalGroupTransactionIdCounterReference.setValue(0);
                  callback.onCallback(0);
                } else {
                  Integer result = dataSnapshot.getValue(Integer.class);
                  globalGroupTransactionIdCounterReference.setValue(result + 1);
                  callback.onCallback(result);
                }
              } catch (NullPointerException e) {
                globalGroupTransactionIdCounterReference.setValue(0);
                callback.onCallback(0);
              }
            });
  }

  public void getTransaction(
      Integer year,
      Integer month,
      Integer dayOfMonth,
      Integer transactionId,
      AbstractTransactionModelCallback abstractTransactionModelCallback) {

    this.getTransactionEntity(
        year,
        month,
        dayOfMonth,
        transactionId,
        transactionEntity -> {
          if (transactionEntity.getIsGroupTransaction()) {

            FirebaseDBHandler.this.getGroupTransactionEntity(
                transactionEntity.getGroupTransactionId(),
                groupTransactionEntity ->
                    abstractTransactionModelCallback.onCallback(
                        new GroupTransactionModel(
                            transactionEntity, year, month, dayOfMonth, groupTransactionEntity)));

          } else {
            abstractTransactionModelCallback.onCallback(
                new TransactionModel(transactionEntity, year, month, dayOfMonth));
          }
        });
  }

  private DatabaseReference getUserTransactionsDatabaseReference() {
    this.validate_currentUserIsSet();
    return dbInstance
        .getReference()
        .child(USERS_BUCKET_NAME)
        .child(currentUserName)
        .child(TRANSACTIONS_BUCKET_NAME);
  }

  private DatabaseReference getUserPendingTransactionsDatabaseReference(String userName) {
    return dbInstance
        .getReference()
        .child(USERS_BUCKET_NAME)
        .child(userName)
        .child(PENDING_TRANSACTIONS_BUCKET_NAME);
  }

  private DatabaseReference getGroupTransactionsDatabaseReference() {
    return dbInstance.getReference().child(GROUP_TRANSACTIONS_BUCKET_NAME);
  }

  private DatabaseReference getTransactionEntityDatabaseReference(
      Integer year, Integer month, Integer dayOfMonth, Integer transactionId) {
    return this.getUserTransactionsDatabaseReference()
        .child(year.toString())
        .child(month.toString())
        .child(dayOfMonth.toString())
        .child(transactionId.toString());
  }

  private void getTransactionEntity(
      Integer year,
      Integer month,
      Integer dayOfMonth,
      Integer transactionId,
      TransactionEntityCallback transactionEntityCallback) {

    this.getTransactionEntityDatabaseReference(year, month, dayOfMonth, transactionId)
        .get()
        .addOnSuccessListener(
            dataSnapshot -> {
              TransactionEntity entity = dataSnapshot.getValue(TransactionEntity.class);
              transactionEntityCallback.onCallback(entity);
            });
  }

  private void getGroupTransactionEntity(
      Integer groupTransactionId, GroupTransactionEntityCallback groupTransactionEntityCallback) {

    getGroupTransactionsDatabaseReference()
        .child(groupTransactionId.toString())
        .get()
        .addOnSuccessListener(
            dataSnapshot -> {
              GroupTransactionEntity entity = dataSnapshot.getValue(GroupTransactionEntity.class);
              groupTransactionEntityCallback.onCallback(entity);
            });
  }

  private Task<DataSnapshot> getPendingTransactionEntity(
      Integer groupTransactionId,
      PendingTransactionEntityCallback pendingTransactionEntityCallback) {

    return this.getUserPendingTransactionsDatabaseReference(this.getCurrentUserName())
        .child(groupTransactionId.toString())
        .get()
        .addOnSuccessListener(
            dataSnapshot -> {
              PendingTransactionEntity entity =
                  dataSnapshot.getValue(PendingTransactionEntity.class);
              pendingTransactionEntityCallback.onCallback(entity);
            });
  }

  private Task<Void> addTransactionEntityToDatabase(
      Integer year,
      Integer month,
      Integer dayOfMonth,
      Integer transactionId,
      TransactionEntity transactionEntity) {
    /*
     * "USERS_BUCKET_NAME" -> "User 1" -> "TRANSACTIONS_BUCKET_NAME" -> year -> month -> dayOfMonth -> transactionId -> transactionObj
     */
    return this.getUserTransactionsDatabaseReference()
        .child(year.toString())
        .child(month.toString())
        .child(dayOfMonth.toString())
        .child(transactionId.toString())
        .setValue(transactionEntity);
  }

  private Task<Void> addPendingTransactionEntityToDatabase(
      String userName, PendingTransactionEntity pendingTransactionEntity) {
    /*
     * "USERS_BUCKET_NAME" -> "User 1" -> "PENDING_TRANSACTIONS_BUCKET_NAME" -> groupTransactionId -> pendingTransactionObj
     */
    return this.getUserPendingTransactionsDatabaseReference(userName)
        .child(pendingTransactionEntity.getGroupTransactionId().toString())
        .setValue(pendingTransactionEntity);
  }

  private Task<Void> addGroupTransactionEntityToDatabase(
      Integer groupTransactionId, GroupTransactionEntity groupTransactionEntity) {
    /*
     * "GROUP_TRANSACTIONS_BUCKET_NAME" -> groupTransactionId -> groupTransactionObj
     */
    return this.getGroupTransactionsDatabaseReference()
        .child(groupTransactionId.toString())
        .setValue(groupTransactionEntity);
  }

  public Task<DataSnapshot> addTransaction(
      Integer year, Integer month, Integer dayOfMonth, Double amount, String description) {
    this.validate_currentUserIsSet();
    // Need to wait to get the transaction counter from the DB before proceeding
    return this.getNewTransactionId(
        id -> {
          TransactionEntity transactionEntityObj =
              new TransactionEntity(id, year, month, dayOfMonth, description, amount);
          addTransactionEntityToDatabase(year, month, dayOfMonth, id, transactionEntityObj);
        });
  }

  public void addMonthTransactionChildEventListener(
      ChildEventListener childEventListener, Integer year, Integer month) {
    this.validate_currentUserIsSet();
    getUserTransactionsDatabaseReference()
        .child(year.toString())
        .child(month.toString())
        .addChildEventListener(childEventListener);
  }

  public void removeMonthTransactionChildEventListener(
      ChildEventListener childEventListener, Integer year, Integer month) {
    this.validate_currentUserIsSet();
    getUserTransactionsDatabaseReference()
        .child(year.toString())
        .child(month.toString())
        .removeEventListener(childEventListener);
  }

  public void addDayTransactionChildEventListener(
      ChildEventListener childEventListener, Integer year, Integer month, Integer dayOfMonth) {
    this.validate_currentUserIsSet();
    getUserTransactionsDatabaseReference()
        .child(year.toString())
        .child(month.toString())
        .child(dayOfMonth.toString())
        .addChildEventListener(childEventListener);
  }

  public void removeDayTransactionChildEventListener(
      ChildEventListener childEventListener, Integer year, Integer month, Integer dayOfMonth) {
    this.validate_currentUserIsSet();
    getUserTransactionsDatabaseReference()
        .child(year.toString())
        .child(month.toString())
        .child(dayOfMonth.toString())
        .removeEventListener(childEventListener);
  }

  public Task<DataSnapshot> addGroupTransaction(
      Integer year,
      Integer month,
      Integer dayOfMonth,
      Double totalAmount,
      String description,
      Map<String, Double> userShares) {
    this.validate_currentUserIsSet();
    this.validate_sharesAddUpToTotalAmount(totalAmount, userShares);
    this.validate_usersAreFriends(userShares.keySet());

    // Create group transaction at global level with currentUser as "creator"
    List<GroupTransactionShareEntity> shareEntities = new ArrayList<>();
    Map<String, PendingTransactionEntity> pendingTransactionEntitiesMap = new HashMap<>();

    return this.getNewGroupTransactionId(
        groupTransactionId -> {
          this.getNewTransactionId(
              transactionId -> {
                for (Entry<String, Double> share : userShares.entrySet()) {
                  String userName = share.getKey();
                  Double amountOwed = share.getValue();
                  if (userName.equals(getCurrentUserName())) {
                    shareEntities.add(
                        new GroupTransactionShareEntity(
                            userName, amountOwed, amountOwed, transactionId));
                  } else {
                    Double amountPaid = (double) 0;
                    shareEntities.add(
                        new GroupTransactionShareEntity(userName, amountOwed, amountPaid, null));
                    pendingTransactionEntitiesMap.put(
                        userName,
                        new PendingTransactionEntity(
                            groupTransactionId,
                            totalAmount,
                            amountOwed,
                            amountPaid,
                            description,
                            getCurrentUserName()));
                  }
                }

                GroupTransactionEntity groupTransactionEntity =
                    new GroupTransactionEntity(
                        groupTransactionId, totalAmount, getCurrentUserName(), shareEntities);

                addGroupTransactionEntityToDatabase(groupTransactionId, groupTransactionEntity)
                    .addOnCompleteListener(
                        groupTransactionTask -> {
                          // Create transaction for currentUser
                          TransactionEntity transactionEntityObj =
                              new TransactionEntity(
                                  transactionId,
                                  year,
                                  month,
                                  dayOfMonth,
                                  description,
                                  totalAmount,
                                  groupTransactionId);

                          addTransactionEntityToDatabase(
                                  year, month, dayOfMonth, transactionId, transactionEntityObj)
                              .addOnSuccessListener(
                                  transactionTask -> {
                                    // Create pending transactions for all other users
                                    for (Entry<String, PendingTransactionEntity> entry :
                                        pendingTransactionEntitiesMap.entrySet()) {
                                      addPendingTransactionEntityToDatabase(
                                          entry.getKey(), entry.getValue());
                                    }
                                  });
                        });
              });
        });
  }

  public void convertTransactionToGroupTransaction(
      Integer year,
      Integer month,
      Integer dayOfMonth,
      Integer transactionId,
      Map<String, Double> userShares) {
    this.validate_currentUserIsSet();
    FirebaseDBHandler.this.validate_usersAreFriends(userShares.keySet());

    this.getTransactionEntity(
        year,
        month,
        dayOfMonth,
        transactionId,
        transactionEntity -> {
          FirebaseDBHandler.this.validate_isRegularTransaction(transactionEntity);
          FirebaseDBHandler.this.validate_sharesAddUpToTotalAmount(
              transactionEntity.getAmount(), userShares);

          this.getNewGroupTransactionId(
              groupTransactionId -> {
                List<GroupTransactionShareEntity> shareEntities = new ArrayList<>();
                Map<String, PendingTransactionEntity> pendingTransactionEntitiesMap =
                    new HashMap<>();

                for (Entry<String, Double> share : userShares.entrySet()) {
                  String userName = share.getKey();
                  Double amountOwed = share.getValue();
                  if (userName.equals(this.getCurrentUserName())) {
                    shareEntities.add(
                        new GroupTransactionShareEntity(
                            userName, amountOwed, amountOwed, transactionId));
                  } else {
                    Double amountPaid = (double) 0;
                    shareEntities.add(
                        new GroupTransactionShareEntity(userName, amountOwed, amountPaid, null));
                    pendingTransactionEntitiesMap.put(
                        userName,
                        new PendingTransactionEntity(
                            groupTransactionId,
                            transactionEntity.getAmount(),
                            amountOwed,
                            amountPaid,
                            transactionEntity.getDescription(),
                            this.getCurrentUserName()));
                  }
                }

                GroupTransactionEntity groupTransactionEntity =
                    new GroupTransactionEntity(
                        groupTransactionId,
                        transactionEntity.getAmount(),
                        this.getCurrentUserName(),
                        shareEntities);

                this.addGroupTransactionEntityToDatabase(
                    groupTransactionId, groupTransactionEntity);

                // update transaction with new amounts
                transactionEntity.setGroupTransactionId(groupTransactionId);
                this.getUserTransactionsDatabaseReference()
                    .child(year.toString())
                    .child(month.toString())
                    .child(dayOfMonth.toString())
                    .child(transactionId.toString())
                    .setValue(transactionEntity);

                // Create pending transactions for all other users
                for (Entry<String, PendingTransactionEntity> entry :
                    pendingTransactionEntitiesMap.entrySet()) {
                  this.addPendingTransactionEntityToDatabase(entry.getKey(), entry.getValue());
                }
              });
        });
  }

  public Task<DataSnapshot> updateGroupTransactionPaid(Integer groupTransactionId) {
    this.validate_currentUserIsSet();

    return this.getPendingTransactionEntity(
        groupTransactionId,
        pendingTransactionEntity -> {
          this.getGroupTransactionEntity(
              groupTransactionId,
              groupTransactionEntity -> {
                // validate whether the current user is a part of this group transaction
                GroupTransactionShareEntity currentUserShareEntity = null;
                for (GroupTransactionShareEntity shareEntity : groupTransactionEntity.getShares()) {
                  if (shareEntity.getUsername().equals(this.getCurrentUserName())) {
                    currentUserShareEntity = shareEntity;
                    break;
                  }
                }

                if (currentUserShareEntity == null) {
                  throw new IllegalArgumentException(
                      "Current user is not a part of this group transaction!");
                }

                if (currentUserShareEntity
                    .getAmountPaid()
                    .equals(currentUserShareEntity.getAmountOwed())) {
                  throw new IllegalArgumentException("Amount has already been paid!");
                }

                Double amountPaid = pendingTransactionEntity.getAmountOwed();
                String description = pendingTransactionEntity.getDescription();

                // create transaction for current user
                this.getNewTransactionId(
                    id -> {
                      Calendar todayCalendar = Calendar.getInstance();

                      Integer year = todayCalendar.get(Calendar.YEAR);
                      Integer month = todayCalendar.get(Calendar.MONTH);
                      Integer dayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH);

                      TransactionEntity transactionEntity =
                          new TransactionEntity(
                              id,
                              year,
                              month,
                              dayOfMonth,
                              description,
                              amountPaid,
                              groupTransactionId);

                      FirebaseDBHandler.this.addTransactionEntityToDatabase(
                          year, month, dayOfMonth, id, transactionEntity);
                    });

                // remove from pending transaction if amountPaid == amountOwed
                if (currentUserShareEntity.getAmountOwed().equals(amountPaid)) {
                  FirebaseDBHandler.this
                      .getUserPendingTransactionsDatabaseReference(this.getCurrentUserName())
                      .child(groupTransactionId.toString())
                      .removeValue();
                }
                // update group transaction
                currentUserShareEntity.setAmountPaid(amountPaid);
                FirebaseDBHandler.this
                    .getGroupTransactionsDatabaseReference()
                    .child(groupTransactionId.toString())
                    .setValue(groupTransactionEntity);
              });
        });
  }

  public void getRecentTransactions(
      Handler handler, TransactionsRecyclerViewAdapter adapter, ProgressBar progressBar) {
    this.validate_currentUserIsSet();
    DatabaseReference transactionsDatabaseReference = this.getUserTransactionsDatabaseReference();

    Calendar todayCalendar = Calendar.getInstance();

    DatabaseReference todayTransactionsDR =
        transactionsDatabaseReference
            .child(Integer.toString(todayCalendar.get(Calendar.YEAR)))
            .child(Integer.toString(todayCalendar.get(Calendar.MONTH)))
            .child(Integer.toString(todayCalendar.get(Calendar.DAY_OF_MONTH)));

    todayTransactionsDR
        .get()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
              } else {
                List<AbstractTransactionModel> transactionModels = new ArrayList<>();

                DataSnapshot dayDataSnapshot = task.getResult();

                for (DataSnapshot transactionSnapshot : dayDataSnapshot.getChildren()) {
                  TransactionEntity entity = transactionSnapshot.getValue(TransactionEntity.class);
                  Integer dayOfMonth = entity.getDayOfMonth();
                  Integer month = entity.getMonth();
                  Integer year = entity.getYear();
                  transactionModels.add(new TransactionModel(entity, year, month, dayOfMonth));
                }

                DayTransactionsModel dayTransactionsModel =
                    new DayTransactionsModel(
                        todayCalendar.get(Calendar.DAY_OF_MONTH), transactionModels);

                Log.i(TAG, String.format("Transactions being added to the Recycler View"));
                handler.post(
                    () -> {
                      adapter.setupListForDayOfMonth(dayTransactionsModel);
                      progressBar.setVisibility(View.INVISIBLE);
                    });
              }
            });
  }

  public void getTransactionsForMonth(
      Handler handler,
      TransactionsRecyclerViewAdapter adapter,
      ProgressBar progressBar,
      Integer year,
      Integer month) {

    DatabaseReference monthTransactionsDatabaseReference =
        this.getUserTransactionsDatabaseReference().child(year.toString()).child(month.toString());

    monthTransactionsDatabaseReference
        .get()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
              } else {
                List<AbstractTransactionModel> transactionModels = new ArrayList<>();

                for (DataSnapshot dayDataSnapshot : task.getResult().getChildren()) {
                  for (DataSnapshot transactionSnapshot : dayDataSnapshot.getChildren()) {
                    TransactionEntity entity =
                        transactionSnapshot.getValue(TransactionEntity.class);
                    Integer day = entity.getDayOfMonth();
                    transactionModels.add(new TransactionModel(entity, year, month, day));
                  }
                }

                MonthTransactionsModel monthTransactionsModel =
                    new MonthTransactionsModel(month, transactionModels);

                Log.i(TAG, String.format("Transactions being added to the Recycler View"));
                handler.post(
                    () -> {
                      adapter.setupListForMonth(monthTransactionsModel);
                      progressBar.setVisibility(View.INVISIBLE);
                    });
              }
            });
  }

  public void getPendingTransactions(
      Handler handler, PendingTransactionsRecyclerViewAdapter adapter, ProgressBar progressBar) {
    this.validate_currentUserIsSet();
    DatabaseReference pendingTransactionsDR =
        this.getUserPendingTransactionsDatabaseReference(this.getCurrentUserName());

    pendingTransactionsDR
        .get()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
              } else {
                List<PendingTransactionModel> pendingTransactionModels = new ArrayList<>();

                DataSnapshot dataSnapshot = task.getResult();

                for (DataSnapshot pendingTransactionSnapshot : dataSnapshot.getChildren()) {
                  PendingTransactionEntity entity =
                      pendingTransactionSnapshot.getValue(PendingTransactionEntity.class);
                  pendingTransactionModels.add(new PendingTransactionModel(entity));
                }

                Log.i(TAG, String.format("Transactions being added to the Recycler View"));
                handler.post(
                    () -> {
                      adapter.setupList(pendingTransactionModels);
                      progressBar.setVisibility(View.INVISIBLE);
                    });
              }
            });
  }

  public void addGroupTransactionChildEventListener(
      ChildEventListener childEventListener, Integer groupTransactionId) {
    this.validate_currentUserIsSet();
    getGroupTransactionsDatabaseReference()
        .child(groupTransactionId.toString())
        .addChildEventListener(childEventListener);
  }

  // </editor-fold>

}
