package edu.northeastern.coinnect.activities.transactions.viewGroupTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.databinding.ActivityViewGroupTransactionBinding;
import edu.northeastern.coinnect.models.transactionModels.GroupTransactionModel;
import edu.northeastern.coinnect.repositories.TransactionsRepository;
import edu.northeastern.coinnect.utils.CalendarUtils;
import edu.northeastern.coinnect.utils.TransactionUtils;
import java.util.Locale;

public class ViewGroupTransactionActivity extends AppCompatActivity {

  private static final String TAG = "_ViewGroupTransactionActivity";
  private final Handler handler = new Handler();
  private Integer year;
  private Integer month;
  private Integer dayOfMonth;
  private Integer transactionId;

  private TextView dateTV;
  private TextView amountTV;
  private TextView descriptionTV;

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
                Locale locale = this.getResources().getConfiguration().getLocales().get(0);

                this.dateTV.setText(
                    CalendarUtils.getDayFormattedDate(
                        groupTransactionModel.getYear(),
                        groupTransactionModel.getMonth(),
                        groupTransactionModel.getDayOfMonth(),
                        locale));
                this.amountTV.setText(
                    TransactionUtils.formatWithCurrency(locale, groupTransactionModel.getAmount()));
                this.descriptionTV.setText(groupTransactionModel.getDescription());

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

    this.dateTV = findViewById(R.id.tv_gt_date);
    this.amountTV = findViewById(R.id.tv_gt_amount);
    this.descriptionTV = findViewById(R.id.tv_gt_description);
    progressBar = findViewById(R.id.transactionsDetailsProgressBar);

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
