package edu.northeastern.coinnect.models.transactionModels;

public abstract class AbstractTransactionModel {

  // this cannot be null as it is a native type
  private int id;
  private String description;
  private Double amount;

  private int year;
  private int month;
  private int dayOfMonth;

  boolean isGroupTransaction;

  public AbstractTransactionModel(int id, String description, Double amount, int year, int month, int dayOfMonth, boolean isGroupTransaction) {
    this.id = id;
    this.description = description;
    this.amount = amount;

    this.year = year;
    this.month = month;
    this.dayOfMonth = dayOfMonth;

    this.isGroupTransaction = isGroupTransaction;
  }

  public Integer getId() {
    return this.id;
  }

  public String getDescription() {
    return this.description;
  }

  public Double getAmount() {
    return this.amount;
  }

  public Integer getYear() {
    return this.year;
  }

  public Integer getMonth() {
    return this.month;
  }

  public Integer getDayOfMonth() {
    return this.dayOfMonth;
  }

  public abstract boolean getIsGroupTransaction();
}
