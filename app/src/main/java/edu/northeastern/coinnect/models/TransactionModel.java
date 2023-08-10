package edu.northeastern.coinnect.models;

import edu.northeastern.coinnect.models.persistence.entities.TransactionEntity;

public class TransactionModel extends AbstractTransactionModel {


  public TransactionModel(int id, String description, Double amount, int year, int month, int dayOfMonth) {
    super(id, description, amount, year, month, dayOfMonth);
  }

  public TransactionModel(TransactionEntity entity, int year, int month, int dayOfMonth) {
    super(entity.transactionId, entity.description, entity.amount, year, month, dayOfMonth);
    this.isGroupTransaction = entity.getIsGroupTransaction();
  }

  @Override
  public boolean getIsGroupTransaction() {
    return this.isGroupTransaction;
  }
}
