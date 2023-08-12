package edu.northeastern.coinnect.models.transactionModels;

import edu.northeastern.coinnect.models.persistence.entities.GroupTransactionEntity;
import edu.northeastern.coinnect.models.persistence.entities.TransactionEntity;
import java.util.List;
import java.util.stream.Collectors;

public class GroupTransactionModel extends AbstractTransactionModel {
  private int groupTransactionId;
  private String creatorUserName;

  private List<GroupTransactionShareModel> shares;

  public GroupTransactionModel(
      int id,
      String description,
      Double amount,
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

  /**
   * Gets the net amount to display considering the amounts that have been paid to this user.
   *
   * @param currentUserName the user name of the current user.
   * @return the net amount that the current user paid.
   */
  public Double getNetAmount(String currentUserName) {
    if (currentUserName.equals(this.creatorUserName)) {
      Double totalAmount = this.getAmount();

      for (GroupTransactionShareModel share : this.getGroupTransactionShares()) {
        totalAmount = totalAmount - share.getAmountPaid();
      }

      return totalAmount;
    } else {
      for (GroupTransactionShareModel share : this.getGroupTransactionShares()) {
        if (share.getUsername().equals(currentUserName)) {
          return share.getAmountPaid();
        }
      }

      throw new IllegalArgumentException(
          "provided user does not have a share in this group transaction!");
    }
  }
}
