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
    private String currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        currentUser = getIntent().getStringExtra("USER_NAME");
        navView = findViewById(R.id.bottomNavigationViewTransactions);
        navView.setSelectedItemId(R.id.transactionActivity);
        menuBarActions(navView);
    }

    protected void menuBarActions(BottomNavigationView navView) {
        navView.setOnItemSelectedListener(item ->  {
            if(item.getItemId() == R.id.homeActivity) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("USER_NAME", currentUser);
                startActivity(intent);
                overridePendingTransition(0,0);
            } else if(item.getItemId() == R.id.friends) {
                Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
                intent.putExtra("USER_NAME", currentUser);
                startActivity(intent);
                overridePendingTransition(0,0);
            } else if (item.getItemId() == R.id.transactionActivity) {
                item.setChecked(true);
                return true;
            } else {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra("USER_NAME", currentUser);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}