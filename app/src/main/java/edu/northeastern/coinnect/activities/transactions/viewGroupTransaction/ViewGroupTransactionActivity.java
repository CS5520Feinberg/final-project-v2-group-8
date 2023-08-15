package edu.northeastern.coinnect.activities.transactions.viewGroupTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.databinding.ActivityViewGroupTransactionBinding;
import edu.northeastern.coinnect.models.transactionModels.GroupTransactionModel;
import edu.northeastern.coinnect.repositories.TransactionsRepository;

public class ViewGroupTransactionActivity extends AppCompatActivity {

  private static final String TAG = "_ViewGroupTransactionActivity";
  private final Handler handler = new Handler();
  private Integer year;
  private Integer month;
  private Integer dayOfMonth;
  private Integer transactionId;

  private ProgressBar progressBar;

  private final TransactionsRepository transactionsRepository =
      TransactionsRepository.getInstance();

  private void fetchGroupTransactionDetails() {
    runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

    this.transactionsRepository.getTransaction(
        this.year,
        this.month,
        this.dayOfMonth,
        this.transactionId,
        abstractTransactionModel -> {
          GroupTransactionModel groupTransactionModel =
              (GroupTransactionModel) abstractTransactionModel;

          handler.post(
              () -> {
                // TODO: set the fields
                progressBar.setVisibility(View.INVISIBLE);
              });
        });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityViewGroupTransactionBinding binding =
        ActivityViewGroupTransactionBinding.inflate(getLayoutInflater());
    setContentView(R.layout.activity_view_group_transaction);

    Intent intent = getIntent();

    this.year = intent.getIntExtra("TRANSACTION_YEAR", 0);
    this.month = intent.getIntExtra("TRANSACTION_MONTH", 0);
    this.dayOfMonth = intent.getIntExtra("TRANSACTION_DAY_OF_MONTH", 0);
    this.transactionId = intent.getIntExtra("TRANSACTION_TRANSACTION_ID", 0);
  }

  protected void onResume() {
    super.onResume();
    this.fetchGroupTransactionDetails();
  }
}
