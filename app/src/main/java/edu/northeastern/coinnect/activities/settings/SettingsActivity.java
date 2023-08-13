package edu.northeastern.coinnect.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.friends.FriendsActivity;
import edu.northeastern.coinnect.activities.home.HomeActivity;
import edu.northeastern.coinnect.activities.transactions.TransactionsActivity;
import edu.northeastern.coinnect.repositories.UsersRepository;

public class SettingsActivity extends AppCompatActivity {

  private String currentUser;
  private final UsersRepository usersRepository = UsersRepository.getInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    currentUser = this.usersRepository.getCurrentUserName();

    BottomNavigationView navView = findViewById(R.id.bottom_nav_home);
    navView.setSelectedItemId(R.id.settings);
    menuBarActions(navView);
  }

  protected void menuBarActions(BottomNavigationView navView) {
    navView.setOnItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.friends) {
            Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);

          } else if (item.getItemId() == R.id.settings) {
            item.setChecked(true);
            return true;
          } else if (item.getItemId() == R.id.transactionActivity) {
            Intent intent = new Intent(getApplicationContext(), TransactionsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
          } else {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
          }
          return false;
        });
  }
}
