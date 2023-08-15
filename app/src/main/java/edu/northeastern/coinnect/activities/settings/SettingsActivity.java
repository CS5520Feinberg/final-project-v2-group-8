package edu.northeastern.coinnect.activities.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

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

        passwordBtn.setOnClickListener(v -> {
            getInput("Enter new password", "Password", InputType.TYPE_TEXT_VARIATION_PASSWORD);

        });
        budgetBtn.setOnClickListener(v -> {
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

        builder.setTitle(title)
                .setView(input)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
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
                            usersRepository.setMonthlyBudget(value);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

    }

    protected void setupNavBarActions(BottomNavigationView navView) {
        navView.setOnItemSelectedListener(
                item -> {
                    if (item.getItemId() == R.id.settingsActivity) {
                        Intent intent = new Intent(this, FriendsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);

                    } else if (item.getItemId() == R.id.settingsActivity) {
                        item.setChecked(true);
                        return true;
                    } else if (item.getItemId() == R.id.transactionActivity) {
                        Intent intent = new Intent(this, TransactionsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    } else if (item.getItemId() == R.id.friendsActivity) {
                        Intent intent = new Intent(this, FriendsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    } else {
                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    }
                    return false;
                });
    }
}
