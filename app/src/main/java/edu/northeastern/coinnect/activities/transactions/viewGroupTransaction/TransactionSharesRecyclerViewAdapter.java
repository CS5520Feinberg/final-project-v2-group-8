package edu.northeastern.coinnect.activities.transactions.viewGroupTransaction;

import android.content.Context;
import android.util.Log;
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
  private static final String TAG = "_TransactionSharesRecyclerViewAdapter";
  private Context context;
  private List<GroupTransactionShareModel> groupTransactionShareModelList;

  @NonNull
  @Override
  public GroupTransactionShareViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent, int viewType) {
    Log.d(TAG, "GT RV Adapter Create View Holder started");
    this.context = parent.getContext();

    View view =
        LayoutInflater.from(this.context)
            .inflate(R.layout.card_group_transaction_share, parent, false);

    Log.d(TAG, "GT RV Adapter Create View Holder complete");
    return new GroupTransactionShareViewHolder(view);
  }

  public TransactionSharesRecyclerViewAdapter(
      List<GroupTransactionShareModel> groupTransactionShareModelList) {
    Log.d(TAG, "GT RV Adapter created");
    this.groupTransactionShareModelList = groupTransactionShareModelList;
  }

  @Override
  public void onBindViewHolder(@NonNull GroupTransactionShareViewHolder holder, int position) {
    Log.d(TAG, "GT RV Adapter Bind View Holder started");
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

    Log.d(TAG, "GT RV Adapter Bind View Holder complete");
  }

  @Override
  public int getItemCount() {
    Log.d(TAG, "GT RV Adapter Item Count requested");
    return this.groupTransactionShareModelList.size();
  }

  public void setupList(List<GroupTransactionShareModel> groupTransactionShareModels) {
    Log.d(TAG, "GT RV Adapter Setup list started");
    this.groupTransactionShareModelList = new ArrayList<>();

    this.groupTransactionShareModelList.addAll(groupTransactionShareModels);
    notifyDataSetChanged();
    Log.d(TAG, "GT RV Adapter Setup list complete");
  }
}
