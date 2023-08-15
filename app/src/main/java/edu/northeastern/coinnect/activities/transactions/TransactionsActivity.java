package edu.northeastern.coinnect.activities.transactions;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.friends.FriendsActivity;
import edu.northeastern.coinnect.activities.home.HomeActivity;
import edu.northeastern.coinnect.activities.settings.SettingsActivity;
import edu.northeastern.coinnect.activities.transactions.viewGroupTransaction.ViewGroupTransactionActivity;
import edu.northeastern.coinnect.activities.transactions.viewTransaction.ViewTransactionActivity;
import edu.northeastern.coinnect.databinding.ActivityTransactionsBinding;
import edu.northeastern.coinnect.models.transactionModels.AbstractTransactionModel;
import edu.northeastern.coinnect.repositories.TransactionsRepository;
import edu.northeastern.coinnect.utils.CalendarUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransactionsActivity extends AppCompatActivity {
  private static final String TAG = "_TransactionsActivity";

  private final Handler handler = new Handler();

  private RecyclerView transactionsRV;
  private TransactionsRecyclerViewAdapter transactionsRVA;
  private MaterialButton previousMonthButton;
  private MaterialButton nextMonthButton;
  private TextView currentMonthTextView;
  private ProgressBar progressBar;
  private BottomNavigationView bottomNavigationView;

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
    //     set up a listener for transaction card click -> Edit Transaction
    TransactionCardClickListener transactionCardClickListener =
        (year, month, dayOfMonth, transactionId, isGroupTransaction) -> {
          if (isGroupTransaction) {
            Intent intent =
                new Intent(TransactionsActivity.this, ViewGroupTransactionActivity.class);
            intent.putExtra("TRANSACTION_YEAR", year);
            intent.putExtra("TRANSACTION_MONTH", month);
            intent.putExtra("TRANSACTION_DAY_OF_MONTH", dayOfMonth);
            intent.putExtra("TRANSACTION_TRANSACTION_ID", transactionId);
            startActivity(intent);
          } else {
            Intent intent = new Intent(TransactionsActivity.this, ViewTransactionActivity.class);
            intent.putExtra("TRANSACTION_YEAR", year);
            intent.putExtra("TRANSACTION_MONTH", month);
            intent.putExtra("TRANSACTION_DAY_OF_MONTH", dayOfMonth);
            intent.putExtra("TRANSACTION_TRANSACTION_ID", transactionId);
            startActivity(intent);
          }
        };

    this.transactionsRVA.setCardClickListener(transactionCardClickListener);
    this.transactionsRV.setAdapter(this.transactionsRVA);
  }

  private void fetchTransactionsForSetMonth() {
    runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

    this.transactionsRepository.getTransactionsForMonthList(
        this.handler, this.transactionsRVA, this.progressBar, this.year, this.month);
  }

  private void switchDate_setMonthLabel() {
    Locale locale = this.getResources().getConfiguration().getLocales().get(0);
    String dateString = CalendarUtils.getMonthFormattedDate(this.year, this.month, locale);

    this.currentMonthTextView.setText(dateString);
  }

  private void switchDate_NextMonth() {
    this.month++;
    if (this.month > 11) {
      this.year++;
      this.month = 0;
    }

    this.switchDate_setMonthLabel();
    this.fetchTransactionsForSetMonth();
  }

  private void switchDate_PreviousMonth() {
    this.month--;
    if (this.month < 0) {
      this.year--;
      this.month = 11;
    }

    this.switchDate_setMonthLabel();
    this.fetchTransactionsForSetMonth();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActivityTransactionsBinding binding = ActivityTransactionsBinding.inflate(getLayoutInflater());
    View view = binding.getRoot();
    setContentView(view);

    this.bottomNavigationView = findViewById(R.id.bottomNavigationViewTransactions);
    this.previousMonthButton = findViewById(R.id.btn_previousMonth);
    this.nextMonthButton = findViewById(R.id.btn_nextMonth);
    this.currentMonthTextView = findViewById(R.id.tv_currentMonth);
    progressBar = findViewById(R.id.pb_transactions);

    this.setupDateForSwitcher();

    this.bottomNavigationView.setSelectedItemId(R.id.transactionActivity);
    this.setupNavBarActions(this.bottomNavigationView);

    List<AbstractTransactionModel> transactionModelsList = new ArrayList<>();

    this.setupRecyclerView(binding);

    this.transactionsRVA = new TransactionsRecyclerViewAdapter(transactionModelsList);
    this.setupRecyclerViewListenerAndAdapter();

    this.previousMonthButton.setOnClickListener(
        v -> TransactionsActivity.this.switchDate_PreviousMonth());

    this.nextMonthButton.setOnClickListener(v -> TransactionsActivity.this.switchDate_NextMonth());
  }

  @Override
  protected void onResume() {
    super.onResume();

    this.switchDate_setMonthLabel();
    this.fetchTransactionsForSetMonth();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  protected void setupNavBarActions(BottomNavigationView navView) {
    navView.setOnItemSelectedListener(
        item -> {
          Intent intent;
          if (item.getItemId() == R.id.homeActivity) {
            intent = new Intent(this, HomeActivity.class);
            ;
          } else if (item.getItemId() == R.id.transactionActivity) {
            return true;
          } else if (item.getItemId() == R.id.friendsActivity) {
            intent = new Intent(this, FriendsActivity.class);
          } else if (item.getItemId() == R.id.settingsActivity) {
            intent = new Intent(this, SettingsActivity.class);
          } else {
            return false;
          }

          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
          overridePendingTransition(0, 0);
          finish();
          return true;
        });
  }
}
