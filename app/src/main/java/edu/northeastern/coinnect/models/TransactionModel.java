package edu.northeastern.coinnect.models;

import java.math.BigDecimal;

public class TransactionModel extends AbstractTransactionModel {


  public TransactionModel(int id, String description, BigDecimal amount) {
    super(id, description, amount);
  }

  @Override
  public boolean getIsGroupTransaction() {
    return false;
  }
}
