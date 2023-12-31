package edu.northeastern.coinnect.models.transactionModels;

import edu.northeastern.coinnect.models.persistence.entities.TransactionEntity;

public class TransactionModel extends AbstractTransactionModel {


  public TransactionModel(int id, String description, Double amount, int year, int month, int dayOfMonth, boolean isGroupTransaction) {
    super(id, description, amount, year, month, dayOfMonth, isGroupTransaction);
  }

  public TransactionModel(TransactionEntity entity, int year, int month, int dayOfMonth) {
    super(entity.transactionId, entity.description, entity.amount, year, month, dayOfMonth, entity.getIsGroupTransaction());
    this.isGroupTransaction = entity.getIsGroupTransaction();
  }

  @Override
  public boolean getIsGroupTransaction() {
    return this.isGroupTransaction;
  }
}
