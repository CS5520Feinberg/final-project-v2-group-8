package edu.northeastern.coinnect.persistence;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.FirebaseDatabase;
import edu.northeastern.coinnect.persistence.entities.Transaction;
import edu.northeastern.coinnect.persistence.entities.User;
import java.math.BigDecimal;
import java.util.Map;

public class FirebaseDBHandler {

  private static final String TAG = "_FirebaseDBHandler";
  private static FirebaseDatabase dbInstance;
  private static FirebaseDBHandler INSTANCE;

  private static String currentUserName = null;

  public static final String USERS_BUCKET_NAME = "USERS";
  public static final String TRANSACTIONS_BUCKET_NAME = "TRANSACTIONS";
  public static final String GROUP_TRANSACTIONS_BUCKET_NAME = "GROUP_TRANSACTIONS";

  public static final String TRANSACTION_ID_COUNTER = "GLOBAL_TRANSACTION_ID_COUNTER";
  public static final String GROUP_TRANSACTION_ID_COUNTER = "GLOBAL_GROUP_TRANSACTION_ID_COUNTER";

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

  public void addUser(String username) {
    User user = new User(username);

    dbInstance.getReference().child("users").child(user.username).setValue(user);
  }

  public void validate_currentUserIsSet() {
    if (currentUserName == null) {
      throw new UnsupportedOperationException("User needs to be set to add transaction");
    }
  }

  // </editor-fold>

  // <editor-fold desc="Transactions">

  public int getNewTransactionId() {
    // NOTE: We are not splitting this up because we need to ensure this happens in one transaction
    // with the Database, ensuring the transactionIds are always unique when set.
    int result;
    try {
      result =
          (int)
              dbInstance.getReference().child(TRANSACTION_ID_COUNTER).get().getResult().getValue();
    } catch (NullPointerException e) {
      dbInstance.getReference().child(TRANSACTION_ID_COUNTER).setValue(1);
      return 0;
    }

    dbInstance.getReference().child(TRANSACTION_ID_COUNTER).setValue(result + 1);

    return result;
  }

  public int getNewGroupTransactionId() {
    // NOTE: We are not splitting this up because we need to ensure this happens in one transaction
    // with the Database, ensuring the transactionIds are always unique when set.
    int result;
    try {
      result =
          (int)
              dbInstance
                  .getReference()
                  .child(GROUP_TRANSACTION_ID_COUNTER)
                  .get()
                  .getResult()
                  .getValue();
    } catch (NullPointerException e) {
      dbInstance.getReference().child(GROUP_TRANSACTION_ID_COUNTER).setValue(1);
      return 0;
    }

    dbInstance.getReference().child(GROUP_TRANSACTION_ID_COUNTER).setValue(result + 1);

    return result;
  }

  public Task addTransaction(
      Integer year, Integer month, Integer dayOfMonth, BigDecimal amount, String description) {
    this.validate_currentUserIsSet();

    Integer transactionId = this.getNewTransactionId();
    Transaction transactionObj = new Transaction(transactionId, description, amount);

    /*
     * "USERS_BUCKET_NAME" -> "User 1" -> "TRANSACTIONS_BUCKET_NAME" -> year -> month -> dayOfMonth -> transactionId -> transactionObj
     */
    return dbInstance
        .getReference()
        .child(USERS_BUCKET_NAME)
        .child(currentUserName)
        .child(TRANSACTIONS_BUCKET_NAME)
        .child(year.toString())
        .child(month.toString())
        .child(dayOfMonth.toString())
        .child(transactionId.toString())
        .setValue(transactionObj);
  }

  public void addGroupTransaction(
      Integer year,
      Integer month,
      Integer dayOfMonth,
      BigDecimal totalAmount,
      String description,
      Map<String, BigDecimal> userShares) {
    // TODO: validate whether current user is set
    // TODO: validate whether user Shares add up to total Amount
    // TODO: validate whether users are all friends
    // TODO: Create group transaction at global level with currentUser as "creator"
    // TODO: Create transaction for currentUser
    // TODO: Create pending transactions for all other users
  }

  public void updateGroupTransactionPaid(
      Integer year,
      Integer month,
      Integer dayOfMonth,
      Integer transactionId,
      BigDecimal amountPaid) {
    // TODO: validate whether current user is set
    // TODO: get transaction, get group transaction
    // TODO: validate whether amount paid is valid
    // TODO: create transaction for current user
    // TODO: remove from pending transaction if amountPaid == amountOwed
    // TODO: update group transaction
  }

  public void convertTransactionToGroupTransaction(
      Integer year,
      Integer month,
      Integer dayOfMonth,
      Integer transactionId,
      Map<String, BigDecimal> userShares) {
    // TODO: validate whether current user is set
    // TODO: validate whether user Shares add up to total Amount
    // TODO: validate whether users are all friends
    // TODO: validate whether given transaction is a regular transaction
    // TODO: create group transaction and update transaction with new amounts
    // TODO: Create pending transactions for all other users
  }

  public void getTransactionForMonth(Integer year, Integer month, ChildEventListener monthTransactionsChildEventListener) {
    // TODO: validate whether current user is set
    // TODO: get currentUser's transactions for year/month
    // TODO: set MonthTransactionsChildEventListener
  }

  public void getTransactionForDay(Integer year, Integer month, Integer dayOfMonth, ChildEventListener dayTransactionsChildEventListener) {
    // TODO: validate whether current user is set
    // TODO: get currentUser's transactions for year/month/dayOfMonth
    // TODO: set DayTransactionsChildEventListener
  }

  public void getPendingTransactions() {
    // TODO: validate whether current user is set
    // TODO: get pending transactions for current user
    // TODO: get the linked group transactions and transactions for the pending transaction
  }

  public void addGroupTransactionChildEventListener(
      ChildEventListener childEventListener, Integer groupTransactionId) {
    // TODO: validate whether current user is set
    // TODO: connect listener to currentUser's friend list
  }

  // </editor-fold>

  // <editor-fold desc="Friends">

  public void addFriendsChildEventListener(ChildEventListener childEventListener) {
    // TODO: validate whether current user is set
    // TODO: connect listener to currentUser's friend list
  }

  public void addFriend(String friendUserName) {
    // TODO: validate whether current user is set
    // TODO: add friend to currentUser's friend list
    // TODO: add currentUser to friend's friend list
  }

  // </editor-fold>
}
