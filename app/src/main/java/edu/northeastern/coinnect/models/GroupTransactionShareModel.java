package edu.northeastern.coinnect.models;

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
