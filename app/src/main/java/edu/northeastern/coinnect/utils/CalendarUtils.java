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
}
