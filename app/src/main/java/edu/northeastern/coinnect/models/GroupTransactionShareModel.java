package edu.northeastern.coinnect.models;

import edu.northeastern.coinnect.models.persistence.entities.GroupTransactionShareEntity;
import java.math.BigDecimal;

public class GroupTransactionShareModel {
  private final String username;
  private final Double amountOwed;
  private final Double amountPaid;

  public GroupTransactionShareModel(String username, Double amountOwed, Double amountPaid) {
    this.username = username;
    this.amountOwed = amountOwed;
    this.amountPaid = amountPaid;
  }

  public GroupTransactionShareModel(GroupTransactionShareEntity entity) {
    this.username = entity.getUsername();
    this.amountOwed = entity.getAmountOwed();
    this.amountPaid = entity.getAmountPaid();
  }

  public String getUsername() {
    return this.username;
  }

  public Double getAmountOwed() {
    return this.amountOwed;
  }

  public Double getAmountPaid() {
    return this.amountPaid;
  }
}
