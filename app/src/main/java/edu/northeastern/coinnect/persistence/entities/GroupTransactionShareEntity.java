package edu.northeastern.coinnect.persistence.entities;

import java.math.BigDecimal;

public class GroupTransactionShareEntity {
  public String username;
  public BigDecimal amountOwed;
  public BigDecimal amountPaid;
  public Integer userTransactionId;

  public GroupTransactionShareEntity(
      String username, BigDecimal amountOwed, BigDecimal amountPaid, Integer userTransactionId) {
    this.username = username;
    this.amountOwed = amountOwed;
    this.amountPaid = amountPaid;
    this.userTransactionId = userTransactionId;
  }

  public String getUsername() {
    return this.username;
  }

  public BigDecimal getAmountOwed() {
    return this.amountOwed;
  }

  public void setAmountPaid(BigDecimal amountPaid) {
    this.amountPaid = amountPaid;
  }

  public BigDecimal getAmountPaid() {
    return this.amountPaid;
  }

  public Integer getUserTransactionId() {
    return this.userTransactionId;
  }
}
