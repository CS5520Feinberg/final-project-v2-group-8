package edu.northeastern.coinnect.activities.transactions.addTransaction;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.home.HomeActivity;
import edu.northeastern.coinnect.repositories.TransactionsRepository;
import edu.northeastern.coinnect.repositories.UsersRepository;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class AddTransactionActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private TextView transactionAmountTextView, transactionDescTextView, transactionDateTextView;

    private CheckBox isGroupTransactionCheckBox;

    private Button addTransactionBtn, cancelTransactionBtn;
    private ImageButton addFriendToShareBtn;
    private ConstraintLayout groupLayout;
    private LinearLayout layoutOfUserShares;
    private Spinner spinner;

    private TransactionsRepository transactionsRepository;
    private UsersRepository usersRepository;
    private Map<String, Double> userShares;
    public ProgressBar addTransactionProgressbar;
    int day;
    int month;
    int year;
    LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        transactionAmountTextView = findViewById(R.id.inputTransactionAmount);
        transactionDescTextView = findViewById(R.id.inputTransactionDesc);
        transactionDateTextView = findViewById(R.id.inputTransactionDate);
        isGroupTransactionCheckBox = findViewById(R.id.checkGroupTransaction);
        addTransactionBtn = findViewById(R.id.saveTransactionBtn);
        cancelTransactionBtn = findViewById(R.id.canceltransactionBtn);
        groupLayout = findViewById(R.id.groupTransactionContainer);
        layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addFriendToShareBtn = findViewById(R.id.addFriendToShareBtn);
        layoutOfUserShares = findViewById(R.id.layoutOfUserShares);
        groupLayout.setVisibility(View.INVISIBLE);

        transactionsRepository = TransactionsRepository.getInstance();
        usersRepository = UsersRepository.getInstance();
//        friendsList = usersRepository.getCurrentUserFriends();
        addTransactionProgressbar = findViewById(R.id.addTransactionProgressBar);
        addTransactionProgressbar.setVisibility(View.INVISIBLE);
        transactionDateTextView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar cldr = Calendar.getInstance();
                        day = cldr.get(Calendar.DAY_OF_MONTH);
                        month = cldr.get(Calendar.MONTH);
                        year = cldr.get(Calendar.YEAR);
                        // date picker dialog
                        DatePickerDialog picker = new DatePickerDialog(AddTransactionActivity.this,
                                (view, year1, monthOfYear, dayOfMonth) -> {
                                    day = dayOfMonth;
                                    month = monthOfYear;
                                    year = year1;
                                    transactionDateTextView.setText(
                                            dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                                }, year, month, day);
                        picker.show();
                    }
                });

        addTransactionBtn.setOnClickListener(
                v -> {
                    String amount = transactionAmountTextView.getText().toString().trim();
                    String desc = transactionDescTextView.getText().toString().trim();
                    addTransactionProgressbar.setVisibility(View.VISIBLE);
                    transactionsRepository.addTransaction(handler, this, addTransactionProgressbar,
                            year,
                            month,
                            day,
                            Double.parseDouble(amount),
                            desc,
                            isGroupTransactionCheckBox.isChecked(),
                            userShares
                            ).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                        }
                    });
                });

        cancelTransactionBtn.setOnClickListener(
                v -> {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                });

        isGroupTransactionCheckBox.setOnClickListener( v-> {
            if(isGroupTransactionCheckBox.isChecked()) {
                groupLayout.setVisibility(View.VISIBLE);
                addNewFriendToShareLayout();
            } else {
                groupLayout.setVisibility(View.INVISIBLE);
            }
        });

        addFriendToShareBtn.setOnClickListener(v -> {
            addNewFriendToShareLayout();
        });
    }

    public void addNewFriendToShareLayout() {
        View addView = layoutInflater.inflate(R.layout.group_transaction_share, null);
        layoutOfUserShares.addView(addView);
    }
}
