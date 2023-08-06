package edu.northeastern.coinnect.persistence;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import edu.northeastern.coinnect.models.AbstractTransactionModel;
import edu.northeastern.coinnect.models.GroupTransactionModel;
import edu.northeastern.coinnect.models.TransactionModel;
import edu.northeastern.coinnect.persistence.entities.GroupTransactionEntity;
import edu.northeastern.coinnect.persistence.entities.GroupTransactionShareEntity;
import edu.northeastern.coinnect.persistence.entities.TransactionEntity;
import edu.northeastern.coinnect.persistence.entities.UserEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirebaseDBHandler {

  private static final String TAG = "_FirebaseDBHandler";
  private static FirebaseDatabase dbInstance;
  private static FirebaseDBHandler INSTANCE;

  private static String currentUserName = null;

  public static final String USERS_BUCKET_NAME = "USERS";
  public static final String TRANSACTIONS_BUCKET_NAME = "TRANSACTIONS";
  public static final String FRIENDS_BUCKET_NAME = "FRIENDS";
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

  // <editor-fold desc="Validators">

  public void validate_notNullEntity(Object o, String entityName) {
    if (o == null) {
      throw new UnsupportedOperationException(
          String.format("The entity %s was not found!", entityName));
    }
  }

  public void validate_currentUserIsSet() {
    if (currentUserName == null) {
      throw new UnsupportedOperationException("User needs to be set to add transaction!");
    }
  }

  public void validate_usersAreFriends(List<String> userNames) {}

  public void validate_amountPaid(BigDecimal amountPaid) {
    if (amountPaid == null || amountPaid.compareTo(new BigDecimal(0)) <= 0) {
      throw new IllegalArgumentException("Amount paid cannot be lesser than or equal to zero!");
    }
  }

  public void validate_sharesAddUpToTotalAmount(
      BigDecimal totalAmount, Map<String, BigDecimal> userShares) {
    BigDecimal calculatingTotal = new BigDecimal(0);

    for (BigDecimal share : userShares.values()) {
      calculatingTotal = calculatingTotal.add(share);
    }

    if (!totalAmount.equals(calculatingTotal)) {
      throw new UnsupportedOperationException("User shares need to add up to total Amount!");
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

  public void addUser(String username) {
    UserEntity userEntity = new UserEntity(username);

    dbInstance.getReference().child("users").child(userEntity.username).setValue(userEntity);
  }

  // </editor-fold>

  // <editor-fold desc="Friends">

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

  public List<String> getCurrentUserFriends() {
    List<String> friends = new ArrayList<>();

    DatabaseReference friendsReference = this.getCurrentUserFriendsDatabaseReference();
    for (DataSnapshot child : friendsReference.get().getResult().getChildren()) {
      friends.add(child.getKey());
    }

    return friends;
  }

  public void addFriend(String friendUserName) {
    DatabaseReference friendsReference = this.getCurrentUserFriendsDatabaseReference();
    DatabaseReference othersFriendsReference = this.getUserFriendsDatabaseReference(friendUserName);

    friendsReference.child(friendUserName).push().setValue(true);
    othersFriendsReference.child(this.getCurrentUserName()).push().setValue(true);
  }

  // </editor-fold>

  // <editor-fold desc="Transactions">

  private int getNewTransactionId() {
    // NOTE: We are not splitting this up because we need to ensure this happens in one transaction
    // with the Database, ensuring the transactionIds are always unique when set.
    this.validate_currentUserIsSet();

    DatabaseReference userTransactionIdCounterReference =
        dbInstance
            .getReference()
            .child(USERS_BUCKET_NAME)
            .child(this.getCurrentUserName())
            .child(TRANSACTION_ID_COUNTER);

    int result;
    try {
      result = (int) userTransactionIdCounterReference.get().getResult().getValue();

    } catch (NullPointerException e) {
      userTransactionIdCounterReference.setValue(1);
      return 0;
    }

    userTransactionIdCounterReference.setValue(result + 1);

    return result;
  }

  private int getNewGroupTransactionId() {
    // NOTE: We are not splitting this up because we need to ensure this happens in one transaction
    // with the Database, ensuring the transactionIds are always unique when set.
    this.validate_currentUserIsSet();

    DatabaseReference globalGroupTransactionIdCounterReference =
        dbInstance.getReference().child(GROUP_TRANSACTION_ID_COUNTER);

    int result;
    try {
      result = (int) globalGroupTransactionIdCounterReference.get().getResult().getValue();

    } catch (NullPointerException e) {
      globalGroupTransactionIdCounterReference.setValue(1);
      return 0;
    }

    globalGroupTransactionIdCounterReference.setValue(result + 1);

    return result;
  }

  public AbstractTransactionModel getTransaction(
      Integer year, Integer month, Integer dayOfMonth, Integer transactionId) {
    TransactionEntity entity = this.getTransactionEntity(year, month, dayOfMonth, transactionId);

    if (entity.getIsGroupTransaction()) {
      GroupTransactionEntity groupTransactionEntity =
          this.getGroupTransactionEntity(entity.getGroupTransactionId());

      return new GroupTransactionModel(entity, year, month, dayOfMonth, groupTransactionEntity);
    } else {
      return new TransactionModel(entity, year, month, dayOfMonth);
    }
  }

  private TransactionEntity getTransactionEntity(
      Integer year, Integer month, Integer dayOfMonth, Integer transactionId) {
    DataSnapshot snapshot =
        dbInstance
            .getReference()
            .child(USERS_BUCKET_NAME)
            .child(currentUserName)
            .child(TRANSACTIONS_BUCKET_NAME)
            .child(year.toString())
            .child(month.toString())
            .child(dayOfMonth.toString())
            .child(transactionId.toString())
            .get()
            .getResult();

    return snapshot.getValue(TransactionEntity.class);
  }

  private GroupTransactionEntity getGroupTransactionEntity(Integer groupTransactionId) {
    DataSnapshot snapshot =
        dbInstance
            .getReference()
            .child(GROUP_TRANSACTIONS_BUCKET_NAME)
            .child(groupTransactionId.toString())
            .get()
            .getResult();

    return snapshot.getValue(GroupTransactionEntity.class);
  }

  public Task addTransaction(
      Integer year, Integer month, Integer dayOfMonth, BigDecimal amount, String description) {
    this.validate_currentUserIsSet();

    Integer transactionId = this.getNewTransactionId();
    TransactionEntity transactionEntityObj =
        new TransactionEntity(transactionId, description, amount);

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
        .setValue(transactionEntityObj);
  }

  public void addGroupTransaction(
      Integer year,
      Integer month,
      Integer dayOfMonth,
      BigDecimal totalAmount,
      String description,
      Map<String, BigDecimal> userShares) {
    this.validate_currentUserIsSet();
    this.validate_sharesAddUpToTotalAmount(totalAmount, userShares);
    // TODO: validate whether users are all friends
    // TODO: Create group transaction at global level with currentUser as "creator"
    // TODO: Create transaction for currentUser
    // TODO: Create pending transactions for all other users
  }

  public void convertTransactionToGroupTransaction(
      Integer year,
      Integer month,
      Integer dayOfMonth,
      Integer transactionId,
      Map<String, BigDecimal> userShares) {
    this.validate_currentUserIsSet();

    TransactionEntity transactionEntity =
        this.getTransactionEntity(year, month, dayOfMonth, transactionId);

    this.validate_sharesAddUpToTotalAmount(transactionEntity.getAmount(), userShares);

    // TODO: validate whether users are all friends
    // TODO: validate whether given transaction is a regular transaction
    // TODO: create group transaction and update transaction with new amounts
    // TODO: Create pending transactions for all other users
  }

  public void updateGroupTransactionPaid(int groupTransactionId, BigDecimal amountPaid) {
    this.validate_currentUserIsSet();
    this.validate_amountPaid(amountPaid);

    GroupTransactionEntity groupTransactionEntity =
        this.getGroupTransactionEntity(groupTransactionId);

    // validate whether the current user is a part of this group transaction
    GroupTransactionShareEntity currentUserShareEntity = null;
    for (GroupTransactionShareEntity shareEntity : groupTransactionEntity.getShares()) {
      if (shareEntity.getUsername() == this.getCurrentUserName()) {
        currentUserShareEntity = shareEntity;
        break;
      }
    }

    if (currentUserShareEntity == null) {
      throw new IllegalArgumentException("Current user is not a part of this group transaction!");
    }

    // validate whether amount paid is lesser than or equal to amount owed
    if (currentUserShareEntity
            .getAmountPaid()
            .add(amountPaid)
            .compareTo(currentUserShareEntity.getAmountOwed())
        > 0) {
      throw new IllegalArgumentException("Amount paid cannot be greater than amount owed!");
    }
    // TODO: create transaction for current user
    // TODO: remove from pending transaction if amountPaid == amountOwed
    // TODO: update group transaction
  }

  public void getTransactionForMonth(
      Integer year, Integer month, ChildEventListener monthTransactionsChildEventListener) {
    this.validate_currentUserIsSet();
    // TODO: get currentUser's transactions for year/month
    // TODO: set MonthTransactionsChildEventListener
  }

  public void getTransactionForDay(
      Integer year,
      Integer month,
      Integer dayOfMonth,
      ChildEventListener dayTransactionsChildEventListener) {
    this.validate_currentUserIsSet();
    // TODO: get currentUser's transactions for year/month/dayOfMonth
    // TODO: set DayTransactionsChildEventListener
  }

  public void getPendingTransactions() {
    this.validate_currentUserIsSet();
    // TODO: get pending transactions for current user
    // TODO: get the linked group transactions and transactions for the pending transaction
  }

  public void addGroupTransactionChildEventListener(
      ChildEventListener childEventListener, Integer groupTransactionId) {
    this.validate_currentUserIsSet();
    // TODO: connect listener to currentUser's friend list
  }

  // </editor-fold>

}
