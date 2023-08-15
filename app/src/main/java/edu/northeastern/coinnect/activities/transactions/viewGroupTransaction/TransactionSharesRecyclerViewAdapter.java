package edu.northeastern.coinnect.activities.transactions.viewGroupTransaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.models.transactionModels.GroupTransactionShareModel;
import edu.northeastern.coinnect.utils.TransactionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionSharesRecyclerViewAdapter
    extends RecyclerView.Adapter<GroupTransactionShareViewHolder> {
  private Context context;
  private List<GroupTransactionShareModel> groupTransactionShareModelList;

  public TransactionSharesRecyclerViewAdapter(
      List<GroupTransactionShareModel> groupTransactionShareModelList) {
    this.groupTransactionShareModelList = groupTransactionShareModelList;
  }

  @NonNull
  @Override
  public GroupTransactionShareViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent, int viewType) {
    this.context = parent.getContext();

    View view =
        LayoutInflater.from(this.context)
            .inflate(R.layout.card_group_transaction_share, parent, false);

    return new GroupTransactionShareViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull GroupTransactionShareViewHolder holder, int position) {
    Locale locale = context.getResources().getConfiguration().getLocales().get(0);

    holder.getUserNameTV().setText(groupTransactionShareModelList.get(position).getUsername());
    holder
        .getAmountOwedTV()
        .setText(
            TransactionUtils.formatWithCurrency(
                locale, groupTransactionShareModelList.get(position).getAmountOwed()));
    holder
        .getAmountPaidTV()
        .setText(
            TransactionUtils.formatWithCurrency(
                locale, groupTransactionShareModelList.get(position).getAmountPaid()));
  }

  @Override
  public int getItemCount() {
    return this.groupTransactionShareModelList.size();
  }

  public void setupList(List<GroupTransactionShareModel> groupTransactionShareModels) {
    this.groupTransactionShareModelList = new ArrayList<>();

    this.groupTransactionShareModelList.addAll(groupTransactionShareModels);
    notifyDataSetChanged();
  }
}
