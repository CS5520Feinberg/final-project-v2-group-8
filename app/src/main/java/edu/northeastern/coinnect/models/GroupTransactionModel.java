package edu.northeastern.coinnect.models;

import edu.northeastern.coinnect.persistence.entities.GroupTransactionEntity;
import edu.northeastern.coinnect.persistence.entities.TransactionEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class GroupTransactionModel extends AbstractTransactionModel {
  private int groupTransactionId;
  private String creatorUserName;

  private List<GroupTransactionShareModel> shares;

  public GroupTransactionModel(
      int id,
      String description,
      BigDecimal amount,
      int year,
      int month,
      int dayOfMonth,
      int groupTransactionId,
      String creatorUserName,
      List<GroupTransactionShareModel> shares) {
    super(id, description, amount, year, month, dayOfMonth);
    this.groupTransactionId = groupTransactionId;
    this.creatorUserName = creatorUserName;
    this.shares = shares;
  }

  public GroupTransactionModel(
      TransactionEntity transactionEntity,
      int year,
      int month,
      int dayOfMonth,
      GroupTransactionEntity groupTransactionEntity) {
    super(
        transactionEntity.transactionId,
        transactionEntity.description,
        transactionEntity.amount,
        year,
        month,
        dayOfMonth);
    this.groupTransactionId = groupTransactionEntity.getGroupTransactionId();
    this.creatorUserName = groupTransactionEntity.getCreatorUserName();
    this.shares =
        groupTransactionEntity.getShares().stream()
            .map(GroupTransactionShareModel::new)
            .collect(Collectors.toList());
  }

  @Override
  public boolean getIsGroupTransaction() {
    return true;
  }

  public int getGroupTransactionId() {
    return this.groupTransactionId;
  }

  public String getCreatorUserName() {
    return this.creatorUserName;
  }

  public List<GroupTransactionShareModel> getGroupTransactionShares() {
    return this.shares;
  }
}
