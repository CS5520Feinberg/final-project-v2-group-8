package edu.northeastern.coinnect.activities.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.coinnect.R;

import edu.northeastern.coinnect.activities.FriendsActivity;
import edu.northeastern.coinnect.activities.SettingsActivity;
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

    BottomNavigationView navView = findViewById(R.id.bottomNavigationView2);
    navView.setSelectedItemId(R.id.homeActivity);

    navView.setOnItemSelectedListener(item ->  {
        if(item.getItemId() == R.id.friends) {
          startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
          overridePendingTransition(0,0);

        } else if(item.getItemId() == R.id.homeActivity) {
          return true;
        } else {
          startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
          overridePendingTransition(0,0);
          return true;
        }
        return false;
    });

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