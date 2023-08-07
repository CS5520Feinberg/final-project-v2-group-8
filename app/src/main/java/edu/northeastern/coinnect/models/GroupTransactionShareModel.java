package edu.northeastern.coinnect.models;

import edu.northeastern.coinnect.models.persistence.entities.GroupTransactionShareEntity;
import java.math.BigDecimal;

public class GroupTransactionShareModel {
  private final String username;
  private final BigDecimal amountOwed;
  private final BigDecimal amountPaid;

  public GroupTransactionShareModel(String username, BigDecimal amountOwed, BigDecimal amountPaid) {
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

  public BigDecimal getAmountOwed() {
    return this.amountOwed;
  }

  public BigDecimal getAmountPaid() {
    return this.amountPaid;
  }
}
