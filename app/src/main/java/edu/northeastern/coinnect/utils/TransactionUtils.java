package edu.northeastern.coinnect.utils;

import java.util.Locale;

public class TransactionUtils {
  public static String formatWithCurrency(Locale locale, Double amount) {
    return String.format(locale, "$%1$,.2f", amount);
  }
}
