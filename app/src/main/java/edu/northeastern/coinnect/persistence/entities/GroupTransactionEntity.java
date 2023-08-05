package edu.northeastern.coinnect.persistence.entities;

import java.math.BigDecimal;

public class GroupTransactionEntity {
  public int id;
  public BigDecimal totalAmount;
  public String createdUser;

  public GroupTransactionEntity(
      int id, BigDecimal totalAmount, String createdUser) {
    this.id = id;
    this.totalAmount = totalAmount;
    this.createdUser = createdUser;
  }
}
