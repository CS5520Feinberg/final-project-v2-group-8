package edu.northeastern.coinnect.persistence.entities;

import java.math.BigDecimal;
import java.util.List;

public class GroupTransaction {
  public int id;
  public BigDecimal totalAmount;
  public String createdUser;

  public GroupTransaction(
      int id, BigDecimal totalAmount, String createdUser) {
    this.id = id;
    this.totalAmount = totalAmount;
    this.createdUser = createdUser;
  }
}
