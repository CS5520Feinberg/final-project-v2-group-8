package edu.northeastern.coinnect.models;

import edu.northeastern.coinnect.persistence.entities.PendingTransactionEntity;
import java.math.BigDecimal;

public class PendingTransactionModel {
  private final Integer groupTransactionId;
  private final BigDecimal totalAmount;
  private final BigDecimal amountOwed;
  private final BigDecimal amountPaid;
  private final String creatorUser;

  public PendingTransactionModel(PendingTransactionEntity entity) {
    this.groupTransactionId = entity.getGroupTransactionId();
    this.totalAmount = entity.getTotalAmount();
    this.amountOwed = entity.getAmountOwed();
    this.amountPaid = entity.getAmountPaid();
    this.creatorUser = entity.getCreatorUser();
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
