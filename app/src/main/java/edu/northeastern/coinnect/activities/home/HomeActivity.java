package edu.northeastern.coinnect.activities.home;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.friends.FriendsActivity;
import edu.northeastern.coinnect.activities.pending.PendingTransactionsActivity;
import edu.northeastern.coinnect.activities.settings.SettingsActivity;
import edu.northeastern.coinnect.activities.transactions.TransactionsActivity;
import edu.northeastern.coinnect.activities.transactions.TransactionsRecyclerViewAdapter;
import edu.northeastern.coinnect.activities.transactions.addTransaction.AddTransactionActivity;
import edu.northeastern.coinnect.databinding.ActivityHomeScreenBinding;
import edu.northeastern.coinnect.models.transactionModels.AbstractTransactionModel;
import edu.northeastern.coinnect.repositories.TransactionsRepository;
import edu.northeastern.coinnect.repositories.UsersRepository;
import edu.northeastern.coinnect.utils.CalendarUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

  private final Handler handler = new Handler();
  private static final UsersRepository userRepository = UsersRepository.getInstance();

  private RecyclerView recentTransactionsRV;

  private TransactionsRecyclerViewAdapter recentTransactionsRVA;

  private final TransactionsRepository transactionsRepository =
      TransactionsRepository.getInstance();

  private ProgressBar progressBar;

  private FloatingActionButton addTransactionFAB;
  private TextView greetingTextView;
  private TextView budgetTextView;
  private TextView dateTextView;
  private MaterialButton pendingTransactionsButton;
  private BottomNavigationView bottomNavigationView;

  private void setupRecyclerView(ActivityHomeScreenBinding binding) {
    this.recentTransactionsRV = binding.rvRecentTransactions;

    this.recentTransactionsRV.setHasFixedSize(true);
    this.recentTransactionsRV.setLayoutManager(new LinearLayoutManager(this));
  }

  private void setupRecyclerViewListenerAndAdapter() {
    // set up a listener for transaction card click -> Edit Transaction
    //    TransactionCardClickListener transactionCardClickListener =
    //        () -> {
    //          Intent intent = new Intent(HomeActivity.this, AddTransactionActivity.class);
    //          startActivity(intent);
    //        };

    //    this.transactionsRVA.setCardClickListener(transactionCardClickListener);
    this.recentTransactionsRV.setAdapter(this.recentTransactionsRVA);
  }

  private void setupAddTransactionFAB() {
    this.addTransactionFAB.setOnClickListener(
        view -> {
          Intent intent = new Intent(getApplicationContext(), AddTransactionActivity.class);
          startActivity(intent);
        });
  }

  private void setupUserGreeting() {
    Locale locale = this.getResources().getConfiguration().getLocales().get(0);

    String userBudget = userRepository.getMonthlyBudget();
    String userFirstName = userRepository.getUserFirstName();

    String dateString = CalendarUtils.getDayFormattedDate(Calendar.getInstance(), locale);

    this.greetingTextView.setText(String.format("Hello %s", userFirstName));
    this.budgetTextView.setText(String.format("$%s", userBudget));
    this.dateTextView.setText(dateString);
  }

  private void setupPendingTransactions() {
    this.pendingTransactionsButton.setOnClickListener(view -> {
      Intent intent = new Intent(this, PendingTransactionsActivity.class);
      startActivity(intent);
    });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityHomeScreenBinding binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
    View view = binding.getRoot();
    setContentView(view);

    this.addTransactionFAB = findViewById(R.id.fab_addTransactionButton);
    this.greetingTextView = findViewById(R.id.tv_greeting);
    this.budgetTextView = findViewById(R.id.set_budget);
    this.dateTextView = findViewById(R.id.tv_today_date);
    this.pendingTransactionsButton = findViewById(R.id.btn_pendingTransactions);
    this.bottomNavigationView = findViewById(R.id.bottom_nav_home);
    this.progressBar = findViewById(R.id.homeScreenProgressBar);
    userRepository.fetchUserFriendsList();



    this.setupAddTransactionFAB();
    this.setupUserGreeting();
    this.setupPendingTransactions();

    this.bottomNavigationView.setSelectedItemId(R.id.homeActivity);
    this.setupNavBarActions(this.bottomNavigationView);
    List<AbstractTransactionModel> transactionModelsList = new ArrayList<>();

    this.setupRecyclerView(binding);

    this.recentTransactionsRVA = new TransactionsRecyclerViewAdapter(transactionModelsList);
    this.setupRecyclerViewListenerAndAdapter();
  }

  protected void setupNavBarActions(BottomNavigationView navView) {
    navView.setOnItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.friendsActivity) {
            Intent intent = new Intent(this, FriendsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
          } else if (item.getItemId() == R.id.homeActivity) {
            return true;
          } else if (item.getItemId() == R.id.transactionActivity) {
            Intent intent = new Intent(this, TransactionsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
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

  @Override
  protected void onResume() {
    super.onResume();

    runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

    this.transactionsRepository.getRecentTransactionsList(
        this.handler, this.recentTransactionsRVA, this.progressBar);
  }

  @Override
  protected void onPause() {
    super.onPause();
  }
}
