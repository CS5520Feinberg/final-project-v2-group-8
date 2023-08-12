package edu.northeastern.coinnect.models.persistence;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import edu.northeastern.coinnect.models.transactionModels.AbstractTransactionModel;
import edu.northeastern.coinnect.models.userModels.User.AbstractUserModel;
import edu.northeastern.coinnect.models.transactionModels.DayTransactionsModel;
import edu.northeastern.coinnect.models.transactionModels.GroupTransactionModel;
import edu.northeastern.coinnect.models.transactionModels.MonthTransactionsModel;
import edu.northeastern.coinnect.models.transactionModels.PendingTransactionModel;
import edu.northeastern.coinnect.models.transactionModels.TransactionModel;
import edu.northeastern.coinnect.models.persistence.entities.GroupTransactionEntity;
import edu.northeastern.coinnect.models.persistence.entities.GroupTransactionShareEntity;
import edu.northeastern.coinnect.models.persistence.entities.PendingTransactionEntity;
import edu.northeastern.coinnect.models.persistence.entities.TransactionEntity;
import edu.northeastern.coinnect.models.persistence.entities.UserEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

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
      throw new UnsupportedOperationException("User needs to be set to add transaction!");
    }
  }

  private void validate_usersAreFriends(Set<String> userNames) {
    this.validate_currentUserIsSet();
    List<String> allFriendsUserNames = this.getCurrentUserFriends();

    for (String userName : userNames) {
      if (!allFriendsUserNames.contains(userName)) {
        throw new UnsupportedOperationException(
            String.format("User %s is not a friend or doesn't exist!", userName));
      }
    }
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

    AtomicLong result = new AtomicLong();
    try {
      Task<DataSnapshot> getValueTask = userTransactionIdCounterReference.get();
      getValueTask.addOnSuccessListener(
          res -> {
            result.set((Long) res.getValue());
          });
    } catch (NullPointerException e) {
      userTransactionIdCounterReference.setValue(1);
      return 0;
    }

    userTransactionIdCounterReference.setValue(result.get() + 1);

    return (int) result.get();
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

  private TransactionEntity getTransactionEntity(
      Integer year, Integer month, Integer dayOfMonth, Integer transactionId) {
    DataSnapshot snapshot =
        this.getUserTransactionsDatabaseReference()
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
        getGroupTransactionsDatabaseReference()
            .child(groupTransactionId.toString())
            .get()
            .getResult();

    return snapshot.getValue(GroupTransactionEntity.class);
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

  public Task<Void> addTransaction(
      Integer year, Integer month, Integer dayOfMonth, Double amount, String description) {
    this.validate_currentUserIsSet();

    Integer transactionId = this.getNewTransactionId();
    TransactionEntity transactionEntityObj =
        new TransactionEntity(transactionId, year, month, dayOfMonth, description, amount);

    return this.addTransactionEntityToDatabase(
        year, month, dayOfMonth, transactionId, transactionEntityObj);
  }

  public void addGroupTransaction(
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
    Integer groupTransactionId = this.getNewGroupTransactionId();
    Integer transactionId = this.getNewTransactionId();

    List<GroupTransactionShareEntity> shareEntities = new ArrayList<>();
    Map<String, PendingTransactionEntity> pendingTransactionEntitiesMap = new HashMap<>();

    for (Entry<String, Double> share : userShares.entrySet()) {
      String userName = share.getKey();
      Double amountOwed = share.getValue();
      if (userName.equals(this.getCurrentUserName())) {
        shareEntities.add(
            new GroupTransactionShareEntity(userName, amountOwed, amountOwed, transactionId));
      } else {
        Double amountPaid = new Double(0);
        shareEntities.add(new GroupTransactionShareEntity(userName, amountOwed, amountPaid, null));
        pendingTransactionEntitiesMap.put(
            userName,
            new PendingTransactionEntity(
                groupTransactionId,
                totalAmount,
                amountOwed,
                amountPaid,
                this.getCurrentUserName()));
      }
    }

    GroupTransactionEntity groupTransactionEntity =
        new GroupTransactionEntity(
            groupTransactionId, totalAmount, this.getCurrentUserName(), shareEntities);

    this.addGroupTransactionEntityToDatabase(groupTransactionId, groupTransactionEntity)
        .getResult();

    // Create transaction for currentUser
    TransactionEntity transactionEntityObj =
        new TransactionEntity(
            transactionId, year, month, dayOfMonth, description, totalAmount, groupTransactionId);

    this.addTransactionEntityToDatabase(
            year, month, dayOfMonth, transactionId, transactionEntityObj)
        .getResult();

    // Create pending transactions for all other users
    for (Entry<String, PendingTransactionEntity> entry : pendingTransactionEntitiesMap.entrySet()) {
      this.addPendingTransactionEntityToDatabase(entry.getKey(), entry.getValue());
    }
  }

  public void convertTransactionToGroupTransaction(
      Integer year,
      Integer month,
      Integer dayOfMonth,
      Integer transactionId,
      Map<String, Double> userShares) {
    this.validate_currentUserIsSet();

    TransactionEntity transactionEntity =
        this.getTransactionEntity(year, month, dayOfMonth, transactionId);

    this.validate_isRegularTransaction(transactionEntity);
    this.validate_sharesAddUpToTotalAmount(transactionEntity.getAmount(), userShares);
    this.validate_usersAreFriends(userShares.keySet());

    Integer groupTransactionId = this.getNewGroupTransactionId();

    List<GroupTransactionShareEntity> shareEntities = new ArrayList<>();
    Map<String, PendingTransactionEntity> pendingTransactionEntitiesMap = new HashMap<>();

    for (Entry<String, Double> share : userShares.entrySet()) {
      String userName = share.getKey();
      Double amountOwed = share.getValue();
      if (userName.equals(this.getCurrentUserName())) {
        shareEntities.add(
            new GroupTransactionShareEntity(userName, amountOwed, amountOwed, transactionId));
      } else {
        Double amountPaid = new Double(0);
        shareEntities.add(new GroupTransactionShareEntity(userName, amountOwed, amountPaid, null));
        pendingTransactionEntitiesMap.put(
            userName,
            new PendingTransactionEntity(
                groupTransactionId,
                transactionEntity.getAmount(),
                amountOwed,
                amountPaid,
                this.getCurrentUserName()));
      }
    }

    GroupTransactionEntity groupTransactionEntity =
        new GroupTransactionEntity(
            groupTransactionId,
            transactionEntity.getAmount(),
            this.getCurrentUserName(),
            shareEntities);

    this.addGroupTransactionEntityToDatabase(groupTransactionId, groupTransactionEntity)
        .getResult();

    // update transaction with new amounts
    transactionEntity.setGroupTransactionId(groupTransactionId);
    this.getUserTransactionsDatabaseReference()
        .child(year.toString())
        .child(month.toString())
        .child(dayOfMonth.toString())
        .child(transactionId.toString())
        .setValue(transactionEntity);

    // Create pending transactions for all other users
    for (Entry<String, PendingTransactionEntity> entry : pendingTransactionEntitiesMap.entrySet()) {
      this.addPendingTransactionEntityToDatabase(entry.getKey(), entry.getValue());
    }
  }

  public void updateGroupTransactionPaid(
      Integer year,
      Integer month,
      Integer dayOfMonth,
      Integer groupTransactionId,
      String description,
      Double amountPaid) {
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

    this.validate_sharesPaidAmountRespectsOwedAmount(currentUserShareEntity, amountPaid);

    TransactionEntity transactionEntity;

    if (currentUserShareEntity.getUserTransactionId() != null) {
      // update existing transaction instead of creating new one if this is an update
      transactionEntity =
          getTransactionEntity(
              year, month, dayOfMonth, currentUserShareEntity.getUserTransactionId());

      transactionEntity.setAmount(amountPaid);

      this.getUserTransactionsDatabaseReference()
          .child(year.toString())
          .child(month.toString())
          .child(dayOfMonth.toString())
          .child(Integer.toString(transactionEntity.getTransactionId()))
          .setValue(transactionEntity);
    } else {
      // create transaction for current user
      Integer transactionId = this.getNewTransactionId();
      transactionEntity =
          new TransactionEntity(
              transactionId, year, month, dayOfMonth, description, amountPaid, groupTransactionId);

      this.addTransactionEntityToDatabase(year, month, dayOfMonth, transactionId, transactionEntity)
          .getResult();
    }

    // remove from pending transaction if amountPaid == amountOwed
    if (currentUserShareEntity.getAmountOwed().equals(amountPaid)) {
      this.getUserPendingTransactionsDatabaseReference(this.getCurrentUserName())
          .child(groupTransactionId.toString())
          .removeValue();
    }
    // update group transaction
    currentUserShareEntity.setAmountPaid(amountPaid);
    this.getGroupTransactionsDatabaseReference()
        .child(groupTransactionId.toString())
        .setValue(groupTransactionEntity);
  }

  public MonthTransactionsModel getTransactionForMonth(
      Integer year, Integer month, ChildEventListener monthTransactionsChildEventListener) {
    this.validate_currentUserIsSet();
    DatabaseReference monthTransactionsDatabaseReference =
        this.getUserTransactionsDatabaseReference().child(year.toString()).child(month.toString());

    List<AbstractTransactionModel> transactionModels = new ArrayList<>();
    for (DataSnapshot dayDataSnapshot :
        monthTransactionsDatabaseReference.get().getResult().getChildren()) {
      Integer day = Integer.getInteger(dayDataSnapshot.getKey());

      for (DataSnapshot transactionSnapshot : dayDataSnapshot.getChildren()) {
        TransactionEntity entity = transactionSnapshot.getValue(TransactionEntity.class);
        transactionModels.add(new TransactionModel(entity, year, month, day));
      }
    }

    MonthTransactionsModel monthTransactions = new MonthTransactionsModel(month, transactionModels);

    monthTransactionsDatabaseReference.addChildEventListener(monthTransactionsChildEventListener);
    return monthTransactions;
  }

  public DayTransactionsModel getTransactionForDay(
      Integer year,
      Integer month,
      Integer dayOfMonth,
      ChildEventListener dayTransactionsChildEventListener) {
    this.validate_currentUserIsSet();
    DatabaseReference dayTransactionsDatabaseReference =
        this.getUserTransactionsDatabaseReference()
            .child(year.toString())
            .child(month.toString())
            .child(dayOfMonth.toString());

    List<AbstractTransactionModel> transactionModels = new ArrayList<>();

    for (DataSnapshot transactionSnapshot :
        dayTransactionsDatabaseReference.get().getResult().getChildren()) {
      TransactionEntity entity = transactionSnapshot.getValue(TransactionEntity.class);
      transactionModels.add(new TransactionModel(entity, year, month, dayOfMonth));
    }

    DayTransactionsModel dayTransactions = new DayTransactionsModel(dayOfMonth, transactionModels);

    dayTransactionsDatabaseReference.addChildEventListener(dayTransactionsChildEventListener);
    return dayTransactions;
  }

  public List<PendingTransactionModel> getPendingTransactions() {
    this.validate_currentUserIsSet();
    DatabaseReference pendingTransactionsDR =
        this.getUserPendingTransactionsDatabaseReference(this.getCurrentUserName());

    List<PendingTransactionModel> pendingTransactionModels = new ArrayList<>();

    for (DataSnapshot pendingTransactionSnapshot :
        pendingTransactionsDR.get().getResult().getChildren()) {
      PendingTransactionEntity entity =
          pendingTransactionSnapshot.getValue(PendingTransactionEntity.class);
      pendingTransactionModels.add(new PendingTransactionModel(entity));
    }

    return pendingTransactionModels;
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
