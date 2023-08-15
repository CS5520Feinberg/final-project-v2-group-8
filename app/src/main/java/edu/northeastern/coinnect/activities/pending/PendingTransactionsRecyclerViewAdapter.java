package edu.northeastern.coinnect.activities.pending;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.models.transactionModels.PendingTransactionModel;
import edu.northeastern.coinnect.repositories.TransactionsRepository;
import java.util.ArrayList;
import java.util.List;

public class PendingTransactionsRecyclerViewAdapter
    extends RecyclerView.Adapter<PendingTransactionViewHolder> {
  private Context context;
  private List<PendingTransactionModel> pendingTransactionModelList;
  private TransactionsRepository transactionsRepository;
  private PendingTransactionsRefreshListener refreshListener;

  @NonNull
  @Override
  public PendingTransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    this.context = parent.getContext();

    View view =
        LayoutInflater.from(this.context).inflate(R.layout.card_pending_transaction, parent, false);

    return new PendingTransactionViewHolder(view);
  }

  public PendingTransactionsRecyclerViewAdapter(
      List<PendingTransactionModel> pendingTransactionModelList,
      TransactionsRepository transactionsRepository,
      PendingTransactionsRefreshListener refreshListener) {
    this.pendingTransactionModelList = pendingTransactionModelList;
    this.transactionsRepository = transactionsRepository;
    this.refreshListener = refreshListener;
  }

  @Override
  public void onBindViewHolder(@NonNull PendingTransactionViewHolder holder, int position) {
    holder
        .getCreatorUserNameTextView()
        .setText(pendingTransactionModelList.get(position).getCreatorUser());
    holder
        .getNetAmountOwedTextView()
        .setText(pendingTransactionModelList.get(position).getNetAmountOwed().toString());
    holder
        .getAmountOwedTextView()
        .setText(pendingTransactionModelList.get(position).getAmountOwed().toString());
    holder
        .getAmountPaidTextView()
        .setText(pendingTransactionModelList.get(position).getAmountPaid().toString());
    holder
        .getTotalAmountTextView()
        .setText(pendingTransactionModelList.get(position).getTotalAmount().toString());
    holder
        .getMarkPaidButton()
        .setOnClickListener(
            v ->
                this.transactionsRepository
                    .updateGroupTransactionPaid(
                        pendingTransactionModelList.get(position).getGroupTransactionId())
                    .addOnCompleteListener(
                        task ->
                            PendingTransactionsRecyclerViewAdapter.this.refreshListener
                                .endActivity()));
  }

  @Override
  public int getItemCount() {
    return this.pendingTransactionModelList.size();
  }

  public void setupList(List<PendingTransactionModel> pendingTransactions) {
    this.pendingTransactionModelList = new ArrayList<>();

    this.pendingTransactionModelList.addAll(pendingTransactions);

    notifyDataSetChanged();
  }
}
