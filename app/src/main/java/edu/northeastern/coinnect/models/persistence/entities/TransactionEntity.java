package edu.northeastern.coinnect.models.persistence.entities;

public class TransactionEntity {
  // this cannot be null as it is a native type
  public int transactionId;
  public Integer year;
  public Integer month;
  public Integer dayOfMonth;
  public String description;
  public Double amount;
  public boolean isGroupTransaction;
  // this can be null as it is an object
  public Integer groupTransactionId;

  public TransactionEntity() {}

  /**
   * Creates a regular transaction.
   *
   * @param transactionId the unique integer identifier of the transaction.
   * @param description the description entered by the user for this transaction.
   * @param amount the amount entered by the user for this transaction.
   */
  public TransactionEntity(
      int transactionId,
      Integer year,
      Integer month,
      Integer dayOfMonth,
      String description,
      Double amount) {
    this.transactionId = transactionId;
    this.year = year;
    this.month = month;
    this.dayOfMonth = dayOfMonth;
    this.description = description;
    this.amount = amount;
    this.isGroupTransaction = false;
    this.groupTransactionId = null;
  }

  /**
   * Creates a group transaction.
   *
   * @param transactionId the unique integer identifier of the transaction.
   * @param description the description entered by the user for this transaction.
   * @param amount the amount entered by the user for this transaction.
   * @param groupTransactionId the group Transaction entry's id.
   */
  public TransactionEntity(
      int transactionId,
      Integer year,
      Integer month,
      Integer dayOfMonth,
      String description,
      Double amount,
      int groupTransactionId) {
    this(transactionId, year, month, dayOfMonth, description, amount);
    this.isGroupTransaction = true;
    this.groupTransactionId = groupTransactionId;
  }

  public int getTransactionId() {
    return this.transactionId;
  }

  public Integer getYear() {
    return this.year;
  }

  public Integer getMonth() {
    return this.month;
  }

  public Integer getDayOfMonth() {
    return this.dayOfMonth;
  }

  public String getDescription() {
    return this.description;
  }

  public Double getAmount() {
    return this.amount;
  }

  public boolean getIsGroupTransaction() {
    return this.isGroupTransaction;
  }

  public Integer getGroupTransactionId() {
    return this.groupTransactionId;
  }

  public void setGroupTransactionId(Integer groupTransactionId) {
    this.isGroupTransaction = true;
    this.groupTransactionId = groupTransactionId;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }
}
