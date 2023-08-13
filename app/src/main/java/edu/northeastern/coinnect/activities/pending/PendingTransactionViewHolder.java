package edu.northeastern.coinnect.activities.pending;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import edu.northeastern.coinnect.R;
import org.jetbrains.annotations.NotNull;

public class PendingTransactionViewHolder extends RecyclerView.ViewHolder {
  private final TextView creatorUserNameTextView;
  private final TextView netAmountOwedTextView;
  private final MaterialButton moreDetailsButton;
  private final TextView amountOwedTextView;
  private final TextView amountPaidTextView;
  private final TextView totalAmountTextView;

  public PendingTransactionViewHolder(@NotNull View itemView) {
    super(itemView);
    this.creatorUserNameTextView = itemView.findViewById(R.id.tv_CreatorUserName);
    this.netAmountOwedTextView = itemView.findViewById(R.id.tv_netAmountOwed);
    this.moreDetailsButton = itemView.findViewById(R.id.btn_expandPendingTransaction);
    this.amountOwedTextView = itemView.findViewById(R.id.tv_amountOwed);
    this.amountPaidTextView = itemView.findViewById(R.id.tv_amountPaid);
    this.totalAmountTextView = itemView.findViewById(R.id.tv_amountTotal);
  }

  public TextView getCreatorUserNameTextView() {
    return creatorUserNameTextView;
  }

  public TextView getNetAmountOwedTextView() {
    return netAmountOwedTextView;
  }

  public MaterialButton getMoreDetailsButton() {
    return moreDetailsButton;
  }

  public TextView getAmountOwedTextView() {
    return amountOwedTextView;
  }

  public TextView getAmountPaidTextView() {
    return amountPaidTextView;
  }

  public TextView getTotalAmountTextView() {
    return totalAmountTextView;
  }
}
