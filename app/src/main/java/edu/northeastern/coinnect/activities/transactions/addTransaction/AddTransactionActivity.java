package edu.northeastern.coinnect.activities.transactions.addTransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.home.HomeActivity;
import edu.northeastern.coinnect.repositories.TransactionsRepository;
import java.util.Calendar;

public class AddTransactionActivity extends AppCompatActivity {

  private final Handler handler = new Handler();
  private TextView transactionAmountTextView, transactionDescTextView, transactionDateTextView;

  private CheckBox isGroupTransactionCheckBox;

  private Button addTransactionBtn, cancelTransactionBtn;

  private TransactionsRepository transactionsRepository;

  public ProgressBar addTransactionProgressbar;
  int day;
  int month;
  int year;

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

    transactionsRepository = TransactionsRepository.getInstance();
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
          transactionsRepository.addTransaction(
              handler,
              this,
              addTransactionProgressbar,
              year,
              month,
              day,
              Double.parseDouble(amount),
              desc).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
  }
}
