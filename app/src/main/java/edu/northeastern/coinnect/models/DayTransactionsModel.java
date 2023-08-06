package edu.northeastern.coinnect.models;

import java.util.List;

public class DayTransactionsModel {
  private final Integer day;
  private final List<AbstractTransactionModel> transactionsList;

  public DayTransactionsModel(Integer day, List<AbstractTransactionModel> transactionsList) {
    this.day = day;
    this.transactionsList = transactionsList;
  }

  public Integer getDay() {
    return this.day;
  }

  public List<AbstractTransactionModel> getTransactionsList() {
    return this.transactionsList;
  }
}
