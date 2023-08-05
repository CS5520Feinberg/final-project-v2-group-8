package edu.northeastern.coinnect.models;

import java.math.BigDecimal;

public class TransactionModel extends AbstractTransactionModel {


  public TransactionModel(int id, String description, BigDecimal amount, int year, int month, int dayOfMonth) {
    super(id, description, amount, year, month, dayOfMonth);
  }

  @Override
  public boolean getIsGroupTransaction() {
    return false;
  }
}
