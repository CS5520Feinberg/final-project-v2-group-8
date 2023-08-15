package edu.northeastern.coinnect.activities.transactions.viewGroupTransaction;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import edu.northeastern.coinnect.R;
import org.jetbrains.annotations.NotNull;

public class GroupTransactionShareViewHolder extends RecyclerView.ViewHolder {
  private final TextView userNameTV;
  private final TextView amountOwedTV;
  private final TextView amountPaidTV;

  public GroupTransactionShareViewHolder(@NotNull View itemView) {
    super(itemView);
    this.userNameTV = itemView.findViewById(R.id.tv_gt_share_userName);
    this.amountOwedTV = itemView.findViewById(R.id.tv_gt_share_amountOwed);
    this.amountPaidTV = itemView.findViewById(R.id.tv_gt_share_amountPaid);
  }

  public TextView getUserNameTV() {
    return userNameTV;
  }

  public TextView getAmountOwedTV() {
    return amountOwedTV;
  }

  public TextView getAmountPaidTV() {
    return amountPaidTV;
  }
}
