package edu.northeastern.coinnect.activities.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.friends.FriendsActivity;
import edu.northeastern.coinnect.activities.home.HomeActivity;
import edu.northeastern.coinnect.activities.transactions.TransactionsActivity;
import edu.northeastern.coinnect.repositories.UsersRepository;

public class SettingsActivity extends AppCompatActivity {

  private String currentUser;
  private Button budgetBtn;
  private Button passwordBtn;
  private final UsersRepository usersRepository = UsersRepository.getInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    currentUser = this.usersRepository.getCurrentUserName();

    budgetBtn = findViewById(R.id.btnBudget);
    passwordBtn = findViewById(R.id.btnPassword);

    passwordBtn.setOnClickListener(
        v -> {
          getInput("Enter new password", "Password", InputType.TYPE_TEXT_VARIATION_PASSWORD);
        });
    budgetBtn.setOnClickListener(
        v -> {
          getInput("Enter new monthly budget", "Budget amount", InputType.TYPE_CLASS_NUMBER);
        });

    BottomNavigationView navView = findViewById(R.id.bottom_nav_home);
    navView.setSelectedItemId(R.id.settingsActivity);
    this.setupNavBarActions(navView);
  }

  private void getInput(String title, String hint, int inputType) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    final EditText input = new EditText(this);
    input.setInputType(inputType);
    input.setHint(hint);
    FrameLayout container = new FrameLayout(SettingsActivity.this);
    FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    params.leftMargin = 56;
    params.rightMargin = 56;
    input.setLayoutParams(params);
    container.addView(input);
    builder
        .setTitle(title)
        .setView(container)
        .setPositiveButton(
            "Confirm",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                if (inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                  try {
                    usersRepository.setNewPass(input.getText().toString());
                  } catch (Exception e) {
                    System.out.println("error: " + e);
                  }

                } else {
                  String value = input.getText().toString();
                    try {
                        usersRepository.setNewMonthlyBudget(value);
                    } catch (Exception e) {
                        System.out.println("error: " + e);
                    }
                }
              }
            })
        .setNegativeButton("Cancel", null)
        .show();
  }

  protected void setupNavBarActions(BottomNavigationView navView) {
    navView.setOnItemSelectedListener(
        item -> {
            Intent intent;
            if (item.getItemId() == R.id.homeActivity) {
                intent = new Intent(this, HomeActivity.class);;
            } else if (item.getItemId() == R.id.transactionActivity) {
                intent = new Intent(this, TransactionsActivity.class);
            } else if (item.getItemId() == R.id.friendsActivity) {
                intent = new Intent(this, FriendsActivity.class);
            } else if (item.getItemId() == R.id.settingsActivity) {
                return true;
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
