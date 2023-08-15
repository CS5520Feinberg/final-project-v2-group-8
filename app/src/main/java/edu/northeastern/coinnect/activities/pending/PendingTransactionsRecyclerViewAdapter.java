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
import edu.northeastern.coinnect.utils.TransactionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    Locale locale = context.getResources().getConfiguration().getLocales().get(0);

    holder
        .getCreatorUserNameTextView()
        .setText(pendingTransactionModelList.get(position).getCreatorUser());
    holder
        .getNetAmountOwedTextView()
        .setText(
            TransactionUtils.formatWithCurrency(
                locale, pendingTransactionModelList.get(position).getNetAmountOwed()));
    holder
        .getAmountOwedTextView()
        .setText(
            TransactionUtils.formatWithCurrency(
                locale, pendingTransactionModelList.get(position).getAmountOwed()));
    holder
        .getAmountPaidTextView()
        .setText(
            TransactionUtils.formatWithCurrency(
                locale, pendingTransactionModelList.get(position).getAmountPaid()));
    holder
        .getTotalAmountTextView()
        .setText(
            TransactionUtils.formatWithCurrency(
                locale, pendingTransactionModelList.get(position).getTotalAmount()));
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
