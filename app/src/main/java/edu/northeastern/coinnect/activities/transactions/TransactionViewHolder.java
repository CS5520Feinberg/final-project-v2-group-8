package edu.northeastern.coinnect.activities.transactions;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import edu.northeastern.coinnect.R;
import org.jetbrains.annotations.NotNull;

public class TransactionViewHolder extends RecyclerView.ViewHolder {
  private final TextView transactionDate;
  private final TextView transactionDay;
  private final TextView transactionDescription;
  private final TextView transactionAmount;
  private final ImageView groupIcon;
  private final View view;

  public TransactionViewHolder(@NotNull View itemView) {
    super(itemView);
    this.transactionDate = itemView.findViewById(R.id.tv_transactionDate);
    this.transactionDay = itemView.findViewById(R.id.tv_transactionDay);
    this.transactionDescription = itemView.findViewById(R.id.tv_transactionDescription);
    this.transactionAmount = itemView.findViewById(R.id.tv_transactionAmount);
    this.groupIcon = itemView.findViewById(R.id.img_group_icon);
    this.view = itemView;
  }

  public TextView getTransactionDate() {
    return this.transactionDate;
  }

  public TextView getTransactionDay() {
    return this.transactionDay;
  }

  public TextView getTransactionDescription() {
    return this.transactionDescription;
  }

  public TextView getTransactionAmount() {
    return this.transactionAmount;
  }

  public ImageView getGroupIcon() {
    return groupIcon;
  }

  public View getView() {
    return this.view;
  }
}
