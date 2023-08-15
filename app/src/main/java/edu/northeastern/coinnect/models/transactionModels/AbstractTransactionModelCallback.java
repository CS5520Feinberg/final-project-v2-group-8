package edu.northeastern.coinnect.models.transactionModels;

import edu.northeastern.coinnect.models.persistence.entities.TransactionEntity;

public interface AbstractTransactionModelCallback {
  public void onCallback(AbstractTransactionModel abstractTransactionModel);
}
