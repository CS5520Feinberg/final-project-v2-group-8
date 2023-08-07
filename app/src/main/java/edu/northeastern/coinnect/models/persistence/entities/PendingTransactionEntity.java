package edu.northeastern.coinnect.models.persistence.entities;

import java.math.BigDecimal;

public class PendingTransactionEntity {
  public Integer groupTransactionId;
  public BigDecimal totalAmount;
  public BigDecimal amountOwed;
  public BigDecimal amountPaid;
  public String creatorUser;

  public PendingTransactionEntity(Integer groupTransactionId, BigDecimal totalAmount, BigDecimal amountOwed, BigDecimal amountPaid, String creatorUser) {
    this.groupTransactionId = groupTransactionId;
    this.totalAmount = totalAmount;
    this.amountOwed = amountOwed;
    this.amountPaid = amountPaid;
    this.creatorUser = creatorUser;
  }

  public Integer getGroupTransactionId() {
    return groupTransactionId;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public BigDecimal getAmountOwed() {
    return amountOwed;
  }

  public BigDecimal getAmountPaid() {
    return amountPaid;
  }

  public String getCreatorUser() {
    return creatorUser;
  }
}
