package edu.northeastern.coinnect.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class GroupTransactionModel extends AbstractTransactionModel {

  private List<GroupTransactionShareModel> shares;

  public GroupTransactionModel(int id, String description, BigDecimal amount, int year, int month, int dayOfMonth, List<GroupTransactionShareModel> shares) {
    super(id, description, amount, year, month, dayOfMonth);
    this.shares = shares;
  }

  @Override
  public boolean getIsGroupTransaction() {
    return true;
  }

  public List<GroupTransactionShareModel> getGroupTransactionShares() {
    return this.shares;
  }
}
