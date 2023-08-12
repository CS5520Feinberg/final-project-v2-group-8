package edu.northeastern.coinnect.activities.transactions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.models.transactionModels.AbstractTransactionModel;
import edu.northeastern.coinnect.utils.CalendarUtils;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransactionRecyclerViewAdapter extends RecyclerView.Adapter<TransactionViewHolder> {
  private Context context;
  private List<AbstractTransactionModel> transactionModelsList;
  private TransactionCardClickListener listener;

  public TransactionRecyclerViewAdapter(List<AbstractTransactionModel> transactionModelsList) {
    this.transactionModelsList = transactionModelsList;
  }

  @NonNull
  @Override
  public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    this.context = parent.getContext();

    View view = LayoutInflater.from(this.context).inflate(R.layout.card_transaction, parent, false);

    return new TransactionViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
    Locale locale = context.getResources().getConfiguration().getLocales().get(0);

    Calendar calendar = Calendar.getInstance();
    calendar.set(
        transactionModelsList.get(position).getYear(),
        transactionModelsList.get(position).getMonth(),
        transactionModelsList.get(position).getDayOfMonth());

    holder.getTransactionDate().setText(transactionModelsList.get(position).getDayOfMonth());
    holder.getTransactionDay().setText(CalendarUtils.getDayOfWeek(calendar, locale));
    holder
        .getTransactionAmount()
        .setText(String.format(locale, "%1$,.2f", transactionModelsList.get(position).getAmount()));
    holder
        .getTransactionDescription()
        .setText(transactionModelsList.get(position).getDescription());

    holder
        .getView()
        .setOnClickListener(
            v -> {
              int currentPosition = holder.getAdapterPosition();
              if (currentPosition != RecyclerView.NO_POSITION) {
                listener.onOpenTransactionClick();
              }
            });
  }

  @Override
  public int getItemCount() {
    return this.transactionModelsList.size();
  }

  private void removeItem(int position) {
    this.transactionModelsList.remove(position);
    notifyItemRemoved(position);
  }

  public void addCard(AbstractTransactionModel newCard) {
    this.transactionModelsList.add(newCard);
    notifyItemInserted(this.transactionModelsList.size() - 1);
  }

  public void setCardClickListener(TransactionCardClickListener listener) {
    this.listener = listener;
  }
}
