package edu.northeastern.coinnect.models;

import java.math.BigDecimal;

public abstract class AbstractTransactionModel {

  // this cannot be null as it is a native type
  private int id;
  private String description;
  private BigDecimal amount;

  private int year;
  private int month;
  private int dayOfMonth;

  public AbstractTransactionModel(int id, String description, BigDecimal amount, int year, int month, int dayOfMonth) {
    this.id = id;
    this.description = description;
    this.amount = amount;

    this.year = year;
    this.month = month;
    this.dayOfMonth = dayOfMonth;
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

  public int getYear() {
    return this.year;
  }

  public int getMonth() {
    return this.month;
  }

  public int getDayOfMonth() {
    return this.dayOfMonth;
  }

  public abstract boolean getIsGroupTransaction();
}