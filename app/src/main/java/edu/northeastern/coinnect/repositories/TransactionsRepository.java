package edu.northeastern.coinnect.repositories;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;

import edu.northeastern.coinnect.activities.pending.PendingTransactionsRecyclerViewAdapter;

import java.util.List;
import java.util.Map;

import edu.northeastern.coinnect.activities.transactions.TransactionsRecyclerViewAdapter;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import edu.northeastern.coinnect.models.transactionModels.DayTransactionsModel;

public class TransactionsRepository {
    private static final String TAG = "_TransactionsRepository";
    private static final FirebaseDBHandler firebaseDbHandler = FirebaseDBHandler.getInstance();

    private static TransactionsRepository INSTANCE;

    private String latestTransactionId;
    private static ChildEventListener transactionsChildEventListener;

    private TransactionsRepository() {
    }

    public static TransactionsRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TransactionsRepository();
        }

        return INSTANCE;
    }

    public FirebaseDBHandler getFirebaseDbHandler() {
        return firebaseDbHandler;
    }

    public void setLatestTransactionId(String id) {
        this.latestTransactionId = id;
    }

    public String getLatestTransactionId() {
        return this.latestTransactionId;
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
        return getFirebaseDbHandler()
                .addTransaction(year, month, dayOfMonth, amount, description)
                .addOnSuccessListener(dataSnapshot -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Integer id = 0;
                    if (dataSnapshot.getValue() != null) {
                        id = dataSnapshot.getValue(Integer.class);
                    }
                    if (isGroupTransaction) {
                        addGroupTransaction(year, month, dayOfMonth, id.intValue(), userShares);
                    }
                });
    }

    public void addGroupTransaction(Integer year, Integer month, Integer dayOfMonth, Integer transactionId, Map<String, Double> userShares) {
        getFirebaseDbHandler()
                .convertTransactionToGroupTransaction(year, month, dayOfMonth, transactionId, userShares);
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
