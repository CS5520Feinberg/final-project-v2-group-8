package edu.northeastern.coinnect.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class GroupTransactionModel extends AbstractTransactionModel {

  private List<GroupTransactionShareModel> shares;

  public GroupTransactionModel(int id, String description, BigDecimal amount, List<GroupTransactionShareModel> shares) {
    super(id, description, amount);
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
