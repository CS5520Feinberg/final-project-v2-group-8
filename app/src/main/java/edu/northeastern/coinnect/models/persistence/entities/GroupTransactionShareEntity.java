package edu.northeastern.coinnect.models.persistence.entities;


public class GroupTransactionShareEntity {
  public String username;
  public Double amountOwed;
  public Double amountPaid;
  public Integer userTransactionId;

  public GroupTransactionShareEntity(
      String username, Double amountOwed, Double amountPaid, Integer userTransactionId) {
    this.username = username;
    this.amountOwed = amountOwed;
    this.amountPaid = amountPaid;
    this.userTransactionId = userTransactionId;
  }

  public String getUsername() {
    return this.username;
  }

  public Double getAmountOwed() {
    return this.amountOwed;
  }

  public void setAmountPaid(Double amountPaid) {
    this.amountPaid = amountPaid;
  }

  public Double getAmountPaid() {
    return this.amountPaid;
  }

  public Integer getUserTransactionId() {
    return this.userTransactionId;
  }
}
