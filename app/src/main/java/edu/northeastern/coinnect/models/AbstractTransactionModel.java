package edu.northeastern.coinnect.models;

import java.math.BigDecimal;

public abstract class AbstractTransactionModel {

  // this cannot be null as it is a native type
  private int id;
  private String description;
  private BigDecimal amount;

  public AbstractTransactionModel(int id, String description, BigDecimal amount) {
    this.id = id;
    this.description = description;
    this.amount = amount;
  }

  public int getId() {
    return this.id;
  }

  public String getDescription() {
    return this.description;
  }

  public BigDecimal getAmount() {
    return this.amount;
  }

  public abstract boolean getIsGroupTransaction();
}
