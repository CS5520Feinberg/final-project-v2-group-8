package edu.northeastern.coinnect.activities.pending;

public interface PendingTransactionsRefreshListener {
  void refreshPendingTransactionsList();

  void endActivity();
}
