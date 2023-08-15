package edu.northeastern.coinnect.repositories;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import edu.northeastern.coinnect.activities.pending.PendingTransactionsRecyclerViewAdapter;
import edu.northeastern.coinnect.activities.transactions.TransactionsRecyclerViewAdapter;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import java.util.Map;

public class TransactionsRepository {
  private static final String TAG = "_TransactionsRepository";
  private static final FirebaseDBHandler firebaseDbHandler = FirebaseDBHandler.getInstance();

  private static TransactionsRepository INSTANCE;

  private TransactionsRepository() {}

  public static TransactionsRepository getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new TransactionsRepository();
    }

    return INSTANCE;
  }

  public FirebaseDBHandler getFirebaseDbHandler() {
    return firebaseDbHandler;
  }

  public Task<DataSnapshot> addTransaction(
      Handler handler,
      Context activityContext,
      ProgressBar progressBar,
      Integer year,
      Integer month,
      Integer dayOfMonth,
      Double amount,
      String description,
      boolean isGroupTransaction,
      Map<String, Double> userShares) {

    if (isGroupTransaction) {
      return getFirebaseDbHandler()
          .addGroupTransaction(year, month, dayOfMonth, amount, description, userShares)
          .addOnSuccessListener(dataSnapshot -> progressBar.setVisibility(View.INVISIBLE));
    } else {
      return getFirebaseDbHandler()
          .addTransaction(year, month, dayOfMonth, amount, description)
          .addOnSuccessListener(dataSnapshot -> progressBar.setVisibility(View.INVISIBLE));
    }
  }

  public void convertTransactionToGroupTransaction(
      Integer year,
      Integer month,
      Integer dayOfMonth,
      Integer transactionId,
      Map<String, Double> userShares) {
    getFirebaseDbHandler()
        .convertTransactionToGroupTransaction(year, month, dayOfMonth, transactionId, userShares);
  }

  public Task<DataSnapshot> updateGroupTransactionPaid(Integer groupTransactionId) {
    return getFirebaseDbHandler().updateGroupTransactionPaid(groupTransactionId);
  }

  public void getRecentTransactionsList(
      Handler handler, TransactionsRecyclerViewAdapter adapter, ProgressBar progressBar) {
    firebaseDbHandler.getRecentTransactions(handler, adapter, progressBar);
  }

  public void getPendingTransactionsList(
      Handler handler, PendingTransactionsRecyclerViewAdapter adapter, ProgressBar progressBar) {
    firebaseDbHandler.getPendingTransactions(handler, adapter, progressBar);
  }

  public void getTransactionsForMonthList(
      Handler handler,
      TransactionsRecyclerViewAdapter adapter,
      ProgressBar progressBar,
      Integer year,
      Integer month) {
    firebaseDbHandler.getTransactionsForMonth(handler, adapter, progressBar, year, month);
  }
}
