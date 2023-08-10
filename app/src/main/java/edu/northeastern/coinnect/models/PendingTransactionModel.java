package edu.northeastern.coinnect.models;

import edu.northeastern.coinnect.models.persistence.entities.PendingTransactionEntity;
import java.math.BigDecimal;

public class PendingTransactionModel {
  private final Integer groupTransactionId;
  private final Double totalAmount;
  private final Double amountOwed;
  private final Double amountPaid;
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
