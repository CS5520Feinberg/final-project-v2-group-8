package edu.northeastern.coinnect.activities.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.persistence.entities.Transaction;

public class RecentTransactionAdapter extends RecyclerView.Adapter<RecentTransactionViewHolder> {

    private Context context;
    private List<Transaction> transactionList;

    public RecentTransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public RecentTransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(this.context)
                .inflate(R.layout.recent_transaction_card, parent, false);
        return new RecentTransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentTransactionViewHolder holder, int position) {
//        holder.getTransactionDate().setText(transactionList.get(position).g);
    }

    @Override
    public int getItemCount() {
        return this.transactionList.size();
    }
}
