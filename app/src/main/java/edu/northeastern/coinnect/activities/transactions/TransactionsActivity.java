package edu.northeastern.coinnect.activities.transactions;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.friends.FriendsActivity;
import edu.northeastern.coinnect.activities.home.HomeActivity;
import edu.northeastern.coinnect.activities.settings.SettingsActivity;
import edu.northeastern.coinnect.activities.transactions.addTransaction.AddTransactionActivity;
import edu.northeastern.coinnect.databinding.ActivityTransactionsBinding;
import edu.northeastern.coinnect.models.transactionModels.AbstractTransactionModel;
import edu.northeastern.coinnect.repositories.TransactionsRepository;
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

  private final TransactionsRepository transactionsRepository =
      TransactionsRepository.getInstance();

  private void setupDateForSwitcher() {
    Calendar today = Calendar.getInstance();
    this.year = today.get(Calendar.YEAR);
    this.month = today.get(Calendar.MONTH);
  }

  private void setupRecyclerView(ActivityTransactionsBinding binding) {
    this.transactionsRV = binding.rvTransactions;

    this.transactionsRV.setHasFixedSize(true);
    this.transactionsRV.setLayoutManager(new LinearLayoutManager(this));
  }

  private void setupRecyclerViewListenerAndAdapter() {
    // set up a listener for transaction card click -> Edit Transaction
//    TransactionCardClickListener transactionCardClickListener =
//        () -> {
//          Intent intent = new Intent(TransactionsActivity.this, AddTransactionActivity.class);
//          startActivity(intent);
//        };
//
//    this.transactionsRVA.setCardClickListener(transactionCardClickListener);
    this.transactionsRV.setAdapter(this.transactionsRVA);
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
    BottomNavigationView navView = findViewById(R.id.bottomNavigationViewTransactions);

    this.setupDateForSwitcher();

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
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  protected void menuBarActions(BottomNavigationView navView) {
    navView.setOnItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.homeActivity) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
          } else if (item.getItemId() == R.id.friendsActivity) {
            Intent intent = new Intent(this, FriendsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
          } else if (item.getItemId() == R.id.transactionActivity) {
            item.setChecked(true);
            return true;
          } else if (item.getItemId() == R.id.settingsActivity) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
          }
          return false;
        });
  }
}
