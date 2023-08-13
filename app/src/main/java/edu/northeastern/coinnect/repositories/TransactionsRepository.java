package edu.northeastern.coinnect.repositories;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import edu.northeastern.coinnect.activities.pending.PendingTransactionsRecyclerViewAdapter;
import edu.northeastern.coinnect.activities.transactions.TransactionsRecyclerViewAdapter;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import edu.northeastern.coinnect.models.transactionModels.DayTransactionsModel;

public class TransactionsRepository {
  private static final String TAG = "_TransactionsRepository";
  private static final FirebaseDBHandler firebaseDbHandler = FirebaseDBHandler.getInstance();

  private static TransactionsRepository INSTANCE;

  private static ChildEventListener transactionsChildEventListener;

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
      String description) {
    return getFirebaseDbHandler()
        .addTransaction(year, month, dayOfMonth, amount, description)
        .addOnCompleteListener(
            task -> {
              handler.post(
                  () -> {
                    // TODO: use TransactionRecyclerViewAdapter
                    // adapter.addTransaction(transactionModel);
                    progressBar.setVisibility(View.INVISIBLE);
                  });
            });
  }

  public void getRecentTransactionsList(
      Handler handler,
      TransactionsRecyclerViewAdapter adapter,
      ProgressBar progressBar) {
    firebaseDbHandler.getRecentTransactions(handler, adapter, progressBar);
  }

  public void getPendingTransactionsList(
      Handler handler,
      PendingTransactionsRecyclerViewAdapter adapter,
      ProgressBar progressBar) {
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

  public void getTransactionsForDayOfMonthList(
      Handler handler,
      TransactionsRecyclerViewAdapter adapter,
      ProgressBar progressBar,
      Integer year,
      Integer month,
      Integer dayOfMonth) {
    DayTransactionsModel dayTransactionsModel =
        firebaseDbHandler.getTransactionForDay(year, month, dayOfMonth);

    Log.i(TAG, String.format("Transactions being added to the Recycler View"));
    handler.post(
        () -> {
          adapter.setupListForDayOfMonth(dayTransactionsModel);
          progressBar.setVisibility(View.INVISIBLE);
        });
  }

  public void addMonthTransactionChildEventListener(
      ChildEventListener childEventListener, Integer year, Integer month) {
    transactionsChildEventListener = childEventListener;

    firebaseDbHandler.addMonthTransactionChildEventListener(
        transactionsChildEventListener, year, month);
  }

  public void removeMonthTransactionsChildEventListener(Integer year, Integer month) {
    firebaseDbHandler.removeMonthTransactionChildEventListener(
        transactionsChildEventListener, year, month);
  }

  public void addDayTransactionChildEventListener(
      ChildEventListener childEventListener, Integer year, Integer month, Integer dayOfMonth) {
    transactionsChildEventListener = childEventListener;

    firebaseDbHandler.addDayTransactionChildEventListener(
        transactionsChildEventListener, year, month, dayOfMonth);
  }

  public void removeDayTransactionsChildEventListener(
      Integer year, Integer month, Integer dayOfMonth) {
    firebaseDbHandler.removeDayTransactionChildEventListener(
        transactionsChildEventListener, year, month, dayOfMonth);
  }
}
