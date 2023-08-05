package edu.northeastern.coinnect.persistence.entities;

import java.math.BigDecimal;

public class GroupTransactionShare {
  public String username;
  public BigDecimal amountOwed;
  public BigDecimal amountPaid;

  public GroupTransactionShare(String username, BigDecimal amountOwed, BigDecimal amountPaid) {
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
