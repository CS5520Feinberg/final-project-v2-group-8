package edu.northeastern.coinnect.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarUtils {
  public static String getDayOfWeek(Calendar calendar, Locale locale) {
    DateFormat formatter = new SimpleDateFormat("EEEE", locale);

    return formatter.format(calendar.getTime());
  }

  public static String getDayFormattedDate(Calendar calendar, Locale locale) {
    String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale);

    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    return String.format(locale, "%s, %d", month, dayOfMonth);
  }

  public static String getMonthFormattedDate(Integer year, Integer month, Locale locale) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, 1);
    String monthStr = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale);

    int dayOfMonth = calendar.get(Calendar.YEAR);
    return String.format(locale, "%s, %d", monthStr, dayOfMonth);
  }
}
