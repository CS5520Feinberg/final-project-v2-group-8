package edu.northeastern.coinnect.activities.home;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.coinnect.R;

import edu.northeastern.coinnect.databinding.ActivityHomeScreenBinding;
import edu.northeastern.coinnect.persistence.entities.Transaction;
import edu.northeastern.coinnect.repositories.TransactionsRepository;

public class HomeActivity extends AppCompatActivity {

  private final Handler handler = new Handler();

  private RecyclerView transactionRecyclerView;

  private RecentTransactionAdapter recentTransactionAdapter;

  private TransactionsRepository transactionsRepository;

  private ProgressBar homeScreenProgressBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    edu.northeastern.coinnect.databinding.ActivityHomeScreenBinding binding = ActivityHomeScreenBinding.inflate(
            getLayoutInflater());
    setContentView(R.layout.activity_home_screen);

    homeScreenProgressBar = findViewById(R.id.homeScreenProgressBar);
    List<Transaction> transactionList = new ArrayList<>();

    this.setupToolbar(binding);
    this.setupRecyclerView(binding);

    this.recentTransactionAdapter = new RecentTransactionAdapter(transactionList);
    transactionsRepository = TransactionsRepository.getInstance();
//    homeScreenProgressBar.setVisibility(View.VISIBLE);

  }

  private void setupToolbar(ActivityHomeScreenBinding binding) {
    // setting toolbar with back button that navigates to the main page.
    Toolbar toolbar = binding.homeScreenToolbar;

    toolbar.setTitle("Home");
    toolbar.setTitleTextColor(Color.WHITE);

    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  private void setupRecyclerView(ActivityHomeScreenBinding binding) {
    this.transactionRecyclerView = binding.transactionRecyclerView;
    this.transactionRecyclerView.setHasFixedSize(true);
    this.transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
  }
}