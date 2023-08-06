package edu.northeastern.coinnect.activities.friends;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.settings.SettingsActivity;
import edu.northeastern.coinnect.activities.home.HomeActivity;
import edu.northeastern.coinnect.activities.transactions.TransactionsActivity;

public class FriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        BottomNavigationView navView = findViewById(R.id.bottomNavigationView2);
        navView.setSelectedItemId(R.id.friends);
        menuBarActions(navView);
    }

    protected void menuBarActions(BottomNavigationView navView) {
        navView.setOnItemSelectedListener(item ->  {
            if(item.getItemId() == R.id.homeActivity) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0,0);

            } else if(item.getItemId() == R.id.friends) {
                return true;
            } else if (item.getItemId() == R.id.transactionActivity) {
                startActivity(new Intent(getApplicationContext(), TransactionsActivity.class));
                overridePendingTransition(0,0);
            } else {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}