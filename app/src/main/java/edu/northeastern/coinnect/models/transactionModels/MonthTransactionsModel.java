package edu.northeastern.coinnect.models.transactionModels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MonthTransactionsModel {
  private final Integer month;
  private final List<DayTransactionsModel> dayTransactionsModels;

  public MonthTransactionsModel(Integer month, List<AbstractTransactionModel> transactionModels) {
    Map<Integer, List<AbstractTransactionModel>> transactionModelMap = new HashMap<>();

    for (AbstractTransactionModel transactionModel : transactionModels) {
      if (!transactionModelMap.containsKey(transactionModel.getDayOfMonth())) {
        transactionModelMap.put(transactionModel.getDayOfMonth(), new ArrayList<>());
      }
      transactionModelMap.get(transactionModel.getDayOfMonth()).add(transactionModel);
    }

    List<DayTransactionsModel> dayTransactionModels = new ArrayList<>();

    for (Entry<Integer, List<AbstractTransactionModel>> dayTransactions :
        transactionModelMap.entrySet()) {
      DayTransactionsModel dayTransactionsModel =
          new DayTransactionsModel(dayTransactions.getKey(), dayTransactions.getValue());

      dayTransactionModels.add(dayTransactionsModel);
    }

    this.month = month;
    this.dayTransactionsModels = dayTransactionModels;
  }

  public Integer getMonth() {
    return this.month;
  }

  public List<DayTransactionsModel> getDayTransactionsModels() {
    return this.dayTransactionsModels;
  }
}
