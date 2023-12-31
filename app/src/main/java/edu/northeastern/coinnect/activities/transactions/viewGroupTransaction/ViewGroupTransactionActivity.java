package edu.northeastern.coinnect.activities.transactions.viewGroupTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.models.transactionModels.GroupTransactionModel;
import edu.northeastern.coinnect.models.transactionModels.GroupTransactionShareModel;
import edu.northeastern.coinnect.repositories.TransactionsRepository;
import edu.northeastern.coinnect.utils.CalendarUtils;
import edu.northeastern.coinnect.utils.TransactionUtils;
import java.util.ArrayList;
import java.util.List;
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
  private RecyclerView transactionSharesRV;
  private TransactionSharesRecyclerViewAdapter transactionSharesRVA;

  private ProgressBar progressBar;

  private final TransactionsRepository transactionsRepository =
      TransactionsRepository.getInstance();

  private void fetchGroupTransactionDetails() {
    runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
    Log.d(TAG, "fetch GT details started");

    this.transactionsRepository.getTransaction(
        this.year,
        this.month,
        this.dayOfMonth,
        this.transactionId,
        abstractTransactionModel -> {
          GroupTransactionModel groupTransactionModel =
              (GroupTransactionModel) abstractTransactionModel;

          Log.d(TAG, "GT details available");
          handler.post(
              () -> {
                Log.d(TAG, "GT Details rendering started");
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

                this.transactionSharesRVA.setupList(
                    groupTransactionModel.getGroupTransactionShares());

                Log.d(TAG, "GT Details rendering complete");
                progressBar.setVisibility(View.INVISIBLE);
              });
        });
  }

  private void setupRecyclerView() {
    this.transactionSharesRV.setHasFixedSize(true);
    this.transactionSharesRV.setLayoutManager(new LinearLayoutManager(this));
  }

  private void setupRecyclerViewListenerAndAdapter() {
    this.transactionSharesRV.setAdapter(this.transactionSharesRVA);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_group_transaction);

    this.dateTV = findViewById(R.id.tv_gt_date);
    this.amountTV = findViewById(R.id.tv_gt_amount);
    this.descriptionTV = findViewById(R.id.tv_gt_description);
    this.transactionSharesRV = findViewById(R.id.rv_transaction_shares);
    this.progressBar = findViewById(R.id.transactionsDetailsProgressBar);

    Intent intent = getIntent();

    this.year = intent.getIntExtra("TRANSACTION_YEAR", 0);
    this.month = intent.getIntExtra("TRANSACTION_MONTH", 0);
    this.dayOfMonth = intent.getIntExtra("TRANSACTION_DAY_OF_MONTH", 0);
    this.transactionId = intent.getIntExtra("TRANSACTION_TRANSACTION_ID", 0);

    List<GroupTransactionShareModel> groupTransactionShareModelList = new ArrayList<>();

    Log.d(TAG, "Group Transactions RV setup started");

    this.setupRecyclerView();
    this.transactionSharesRVA =
        new TransactionSharesRecyclerViewAdapter(groupTransactionShareModelList);
    this.setupRecyclerViewListenerAndAdapter();

    Log.d(TAG, "Group Transactions RV setup complete");
  }

  protected void onResume() {
    super.onResume();
    this.fetchGroupTransactionDetails();
  }
}
