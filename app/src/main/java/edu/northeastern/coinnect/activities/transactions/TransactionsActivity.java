package edu.northeastern.coinnect.activities.transactions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.friends.FriendsActivity;
import edu.northeastern.coinnect.activities.home.HomeActivity;
import edu.northeastern.coinnect.activities.settings.SettingsActivity;
import edu.northeastern.coinnect.activities.transactions.addTransaction.AddTransactionActivity;
import edu.northeastern.coinnect.databinding.ActivityTransactionsBinding;
import edu.northeastern.coinnect.models.persistence.entities.TransactionEntity;
import edu.northeastern.coinnect.models.transactionModels.AbstractTransactionModel;
import edu.northeastern.coinnect.models.transactionModels.MonthTransactionsModel;
import edu.northeastern.coinnect.models.transactionModels.TransactionModel;
import edu.northeastern.coinnect.repositories.TransactionsRepository;
import edu.northeastern.coinnect.repositories.UsersRepository;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {
  private static final String TAG = "_TransactionsActivity";

  private final Handler handler = new Handler();

  private RecyclerView transactionsRV;
  private TransactionsRecyclerViewAdapter transactionsRVA;
  private ProgressBar progressBar;

  private Integer year;
  private Integer month;

  private final UsersRepository usersRepository = UsersRepository.getInstance();
  private final TransactionsRepository transactionsRepository =
      TransactionsRepository.getInstance();

  private void setupDateForSwitcher() {
    Calendar today = Calendar.getInstance();
    this.year = today.get(Calendar.YEAR);
    this.month = today.get(Calendar.MONTH);
  }

  private void setupRecyclerView(ActivityTransactionsBinding binding) {
    // setting this as fixed for now, but if we need to adapt this later we can.
    this.transactionsRV = binding.rvTransactions;

    this.transactionsRV.setHasFixedSize(true);
    this.transactionsRV.setLayoutManager(new LinearLayoutManager(this));
  }

  private void setupRecyclerViewListenerAndAdapter() {
    // set up a listener for transaction card click
    TransactionCardClickListener transactionCardClickListener =
        () -> {
          Intent intent = new Intent(TransactionsActivity.this, AddTransactionActivity.class);
          startActivity(intent);
        };

    this.transactionsRVA.setCardClickListener(transactionCardClickListener);
    this.transactionsRV.setAdapter(this.transactionsRVA);
  }

  private ChildEventListener getMonthTransactionChildEventListener(Context activityContext) {
    return new ChildEventListener() {

      private Integer year = TransactionsActivity.this.year;
      private Integer month = TransactionsActivity.this.month;
      @Override
      public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//        TransactionEntity transactionEntity = snapshot.getValue(TransactionEntity.class);
//
//        int year = 2023;
//        int month = 1;
//        int dayOfMonth = 1;
//
//        TransactionsActivity.this.transactionsRVA.addCard(
//            new TransactionModel(transactionEntity, year, month, dayOfMonth));
      }

      @Override
      public void onChildChanged(
          @NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        List<AbstractTransactionModel> transactionModels = new ArrayList<>();

        Iterable<DataSnapshot> daysSnapshot = snapshot.getChildren();
        for(DataSnapshot daySnapshot : daysSnapshot) {
          Iterable<DataSnapshot> transactionsSnapshot = daySnapshot.getChildren();

          for(DataSnapshot transactionSnapshot : transactionsSnapshot) {
            TransactionEntity transactionEntity = transactionSnapshot.getValue(TransactionEntity.class);

            int transactionYear = transactionEntity.getYear();
            int transactionMonth = transactionEntity.getMonth();
            int transactionDayOfMonth = transactionEntity.getDayOfMonth();

            transactionModels.add(
                new TransactionModel(
                    transactionEntity,
                    transactionYear,
                    transactionMonth,
                    transactionDayOfMonth));
          }
        }

        MonthTransactionsModel monthTransactionsModel = new MonthTransactionsModel(this.month, transactionModels);

        TransactionsActivity.this.transactionsRVA.setupListForMonth(monthTransactionsModel);
      }

      @Override
      public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//        TransactionEntity transactionEntity = snapshot.getValue(TransactionEntity.class);
//        TransactionsActivity.this.transactionsRVA.removeCard(transactionEntity.getTransactionId());
      }

      @Override
      public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        // user will never get moved
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, "onCancelled:" + error);
        Toast.makeText(activityContext, "DBError: " + error, Toast.LENGTH_SHORT).show();
      }
    };
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    edu.northeastern.coinnect.databinding.ActivityTransactionsBinding binding =
        ActivityTransactionsBinding.inflate(getLayoutInflater());
    View view = binding.getRoot();
    setContentView(view);

    setContentView(view);

    progressBar = findViewById(R.id.pb_transactions);

    this.setupDateForSwitcher();

    BottomNavigationView navView = findViewById(R.id.bottomNavigationViewTransactions);
    navView.setSelectedItemId(R.id.transactionActivity);
    menuBarActions(navView);

    List<AbstractTransactionModel> transactionModelsList = new ArrayList<>();

    this.setupRecyclerView(binding);

    this.transactionsRVA = new TransactionsRecyclerViewAdapter(transactionModelsList);
    this.setupRecyclerViewListenerAndAdapter();
  }

  @Override
  protected void onResume() {
    super.onResume();

    runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

    this.transactionsRepository.getTransactionsForMonthList(
        this.handler, this.transactionsRVA, this.progressBar, this.year, this.month);
//    this.transactionsRepository.addMonthTransactionChildEventListener(
//        this.getMonthTransactionChildEventListener(this), this.year, this.month);
  }

  @Override
  protected void onPause() {
    super.onPause();
//    this.transactionsRepository.removeMonthTransactionsChildEventListener(this.year, this.month);
  }

  protected void menuBarActions(BottomNavigationView navView) {
    navView.setOnItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.homeActivity) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
          } else if (item.getItemId() == R.id.friends) {
            Intent intent = new Intent(this, FriendsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
          } else if (item.getItemId() == R.id.transactionActivity) {
            item.setChecked(true);
            return true;
          } else {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
          }
          return false;
        });
  }
}
