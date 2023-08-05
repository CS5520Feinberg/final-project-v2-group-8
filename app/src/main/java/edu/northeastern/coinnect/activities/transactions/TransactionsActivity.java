package edu.northeastern.coinnect.activities.transactions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.friends.FriendsActivity;
import edu.northeastern.coinnect.activities.home.HomeActivity;
import edu.northeastern.coinnect.activities.settings.SettingsActivity;

public class TransactionsActivity extends AppCompatActivity {

    private BottomNavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        navView = findViewById(R.id.bottomNavigationViewTransactions);
        navView.setSelectedItemId(R.id.transactionActivity);
        menuBarActions(navView);
    }

    protected void menuBarActions(BottomNavigationView navView) {
        navView.setOnItemSelectedListener(item ->  {
            if(item.getItemId() == R.id.homeActivity) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0,0);
            } else if(item.getItemId() == R.id.friends) {
                startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
                overridePendingTransition(0,0);
            } else if (item.getItemId() == R.id.transactionActivity) {
                return true;
            } else {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}