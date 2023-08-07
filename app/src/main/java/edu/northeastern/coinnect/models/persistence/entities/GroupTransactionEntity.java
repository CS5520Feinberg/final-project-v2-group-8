package edu.northeastern.coinnect.models.persistence.entities;

import java.math.BigDecimal;
import java.util.List;

public class GroupTransactionEntity {
  public int groupTransactionId;
  public BigDecimal totalAmount;
  public String creatorUserName;
  public List<GroupTransactionShareEntity> shares;

  public GroupTransactionEntity(
      int groupTransactionId,
      BigDecimal totalAmount,
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

  public BigDecimal getTotalAmount() {
    return this.totalAmount;
  }

  public String getCreatorUserName() {
    return this.creatorUserName;
  }

  public List<GroupTransactionShareEntity> getShares() {
    return this.shares;
  }
}
