package edu.northeastern.coinnect.activities.transactions;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.northeastern.coinnect.R;

public class TransactionsActivity extends AppCompatActivity {

    private BottomNavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        navView = findViewById(R.id.bottomNavigationViewTransactions);
        navView.setSelectedItemId(R.id.transactionActivity);
    }
}