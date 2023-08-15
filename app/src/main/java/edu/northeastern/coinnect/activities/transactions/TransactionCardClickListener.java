package edu.northeastern.coinnect.activities.transactions;

public interface TransactionCardClickListener {
  void onOpenTransactionClick(Integer year, Integer month, Integer dayOfMonth, Integer transactionId, boolean isGroupTransaction);
}
