package edu.northeastern.coinnect.models;

import edu.northeastern.coinnect.persistence.entities.GroupTransactionEntity;
import edu.northeastern.coinnect.persistence.entities.TransactionEntity;
import java.math.BigDecimal;

public class TransactionModel extends AbstractTransactionModel {


  public TransactionModel(int id, String description, BigDecimal amount, int year, int month, int dayOfMonth) {
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
