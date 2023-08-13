package edu.northeastern.coinnect.models.persistence.entities;

public class PendingTransactionEntity {
  public Integer groupTransactionId;
  public Double totalAmount;
  public Double amountOwed;
  public Double amountPaid;
  public String creatorUser;

  public PendingTransactionEntity() {}

  public PendingTransactionEntity(Integer groupTransactionId, Double totalAmount, Double amountOwed, Double amountPaid, String creatorUser) {
    this.groupTransactionId = groupTransactionId;
    this.totalAmount = totalAmount;
    this.amountOwed = amountOwed;
    this.amountPaid = amountPaid;
    this.creatorUser = creatorUser;
  }

  public Integer getGroupTransactionId() {
    return groupTransactionId;
  }

  public Double getTotalAmount() {
    return totalAmount;
  }

  public Double getAmountOwed() {
    return amountOwed;
  }

  public Double getAmountPaid() {
    return amountPaid;
  }

  public String getCreatorUser() {
    return creatorUser;
  }
}
