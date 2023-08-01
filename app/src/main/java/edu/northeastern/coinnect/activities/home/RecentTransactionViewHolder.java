package edu.northeastern.coinnect.activities.home;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import org.jetbrains.annotations.NotNull;
import edu.northeastern.coinnect.R;
public class RecentTransactionViewHolder extends RecyclerView.ViewHolder {
    private final TextView transactionDate;
    private final TextView transactionDesc;
    private final TextView transactionAmount;

    public RecentTransactionViewHolder(@NotNull View itemView) {
        super(itemView);
        this.transactionDate = itemView.findViewById(R.id.transactionDate);
        this.transactionDesc = itemView.findViewById(R.id.transactionDesc);
        this.transactionAmount = itemView.findViewById(R.id.transactionAmount);
    }

    public TextView getTransactionDate() { return this.transactionDate; }
    public TextView getTransactionDesc() { return this.transactionDesc; }
    public TextView getTransactionAmount() { return this.transactionAmount; }
}
