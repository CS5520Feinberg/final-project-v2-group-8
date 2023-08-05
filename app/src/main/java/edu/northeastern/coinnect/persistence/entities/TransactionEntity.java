package edu.northeastern.coinnect.persistence.entities;

import java.math.BigDecimal;

public class TransactionEntity {
  // this cannot be null as it is a native type
  public int id;
  public String description;
  public BigDecimal amount;
  public boolean isGroupTransaction;
  // this can be null as it is an object
  public Integer groupTransactionId;

  /**
   * Creates a regular transaction.
   * @param id the unique integer identifier of the transaction.
   * @param description the description entered by the user for this transaction.
   * @param amount the amount entered by the user for this transaction.
   */
  public TransactionEntity(int id, String description, BigDecimal amount) {
    this.id = id;
    this.description = description;
    this.amount = amount;
    this.isGroupTransaction = false;
    this.groupTransactionId = null;
  }

  /**
   * Creates a group transaction.
   * @param id the unique integer identifier of the transaction.
   * @param description the description entered by the user for this transaction.
   * @param amount the amount entered by the user for this transaction.
   * @param groupTransactionId the group Transaction entry's id.
   */
  public TransactionEntity(int id, String description, BigDecimal amount, int groupTransactionId) {
    this(id, description, amount);
    this.isGroupTransaction = true;
    this.groupTransactionId = groupTransactionId;
  }

  public int getId() {
    return this.id;
  }

  public String getDescription() {
    return this.description;
  }

  public BigDecimal getAmount() {
    return this.amount;
  }

  public boolean getIsGroupTransaction() {
    return this.isGroupTransaction;
  }

  public Integer getGroupTransactionId() {
    return this.groupTransactionId;
  }
}
