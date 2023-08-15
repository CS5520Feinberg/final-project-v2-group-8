package edu.northeastern.coinnect.activities.pending;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.databinding.ActivityPendingTransactionsBinding;
import edu.northeastern.coinnect.models.transactionModels.PendingTransactionModel;
import edu.northeastern.coinnect.repositories.TransactionsRepository;
import edu.northeastern.coinnect.repositories.UsersRepository;
import java.util.ArrayList;
import java.util.List;

public class PendingTransactionsActivity extends AppCompatActivity implements PendingTransactionsRefreshListener {

  private final Handler handler = new Handler();

  private RecyclerView pendingTransactionsRV;
  private PendingTransactionsRecyclerViewAdapter pendingTransactionsRVA;

  private ProgressBar progressBar;

  private final UsersRepository userRepository = UsersRepository.getInstance();
  private final TransactionsRepository transactionsRepository =
      TransactionsRepository.getInstance();

  private void setupRecyclerView(ActivityPendingTransactionsBinding binding) {
    this.pendingTransactionsRV = binding.rvPendingTransactions;

    this.pendingTransactionsRV.setHasFixedSize(true);
    this.pendingTransactionsRV.setLayoutManager(new LinearLayoutManager(this));
  }

  private void setupRecyclerViewListenerAndAdapter() {
    this.pendingTransactionsRV.setAdapter(this.pendingTransactionsRVA);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityPendingTransactionsBinding binding =
        ActivityPendingTransactionsBinding.inflate(getLayoutInflater());
    View view = binding.getRoot();
    setContentView(view);

    this.progressBar = findViewById(R.id.pendingTransactionsProgressBar);
    userRepository.fetchUserFriendsList();

    List<PendingTransactionModel> pendingTransactionModelsList = new ArrayList<>();
    this.setupRecyclerView(binding);
    this.pendingTransactionsRVA =
        new PendingTransactionsRecyclerViewAdapter(pendingTransactionModelsList, this.transactionsRepository, this);
    this.setupRecyclerViewListenerAndAdapter();
  }

  @Override
  public void refreshPendingTransactionsList() {
    runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

    this.transactionsRepository.getPendingTransactionsList(
        this.handler, this.pendingTransactionsRVA, this.progressBar);
  }

  @Override
  public void endActivity() {
    this.finish();
  }

  @Override
  protected void onResume() {
    super.onResume();
    this.refreshPendingTransactionsList();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }
}
