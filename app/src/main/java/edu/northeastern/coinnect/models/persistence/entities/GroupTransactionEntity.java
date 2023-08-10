package edu.northeastern.coinnect.models.persistence.entities;

import java.util.List;

public class GroupTransactionEntity {
  public int groupTransactionId;
  public Double totalAmount;
  public String creatorUserName;
  public List<GroupTransactionShareEntity> shares;

  public GroupTransactionEntity(
      int groupTransactionId,
      Double totalAmount,
      String creatorUserName,
      List<GroupTransactionShareEntity> shares) {
    this.groupTransactionId = groupTransactionId;
    this.totalAmount = totalAmount;
    this.creatorUserName = creatorUserName;
    this.shares = shares;
  }

  public int getGroupTransactionId() {
    return this.groupTransactionId;
  }

  public Double getTotalAmount() {
    return this.totalAmount;
  }

  public String getCreatorUserName() {
    return this.creatorUserName;
  }

  public List<GroupTransactionShareEntity> getShares() {
    return this.shares;
  }
}
