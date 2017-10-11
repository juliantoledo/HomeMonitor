// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devpartners.homemonitor.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Utility class for Dates.
 *
 * This class handles the basic formats that the AdWords API provide.
 *
 * @author jtoledo@google.com (Julian Toledo)
 * @author gustavomoreira (Gustavo Moreira)
 */
public final class DateUtil {

  protected static final String DATE_FORMAT_REPORT = "yyyyMMdd";
  private static final DateTimeFormatter dfYearMonthDayNoDash =
      DateTimeFormat.forPattern(DATE_FORMAT_REPORT);

  private static final String DATE_FORMAT_SHORT = "yyyy-MM-dd";
  private static final DateTimeFormatter dfYearMonthDay =
      DateTimeFormat.forPattern(DATE_FORMAT_SHORT);

  private static final String DATE_FORMAT_SHORT_WITHOUTDAY = "yyyy-MM";
  private static final DateTimeFormatter dfYearMonth =
      DateTimeFormat.forPattern(DATE_FORMAT_SHORT_WITHOUTDAY);

  private static final String DATE_FORMAT_SHORT_WITHOUTDAY_NODASH = "yyyyMM";
  private static final DateTimeFormatter dfYearMonthNoDash =
      DateTimeFormat.forPattern(DATE_FORMAT_SHORT_WITHOUTDAY_NODASH);

  protected static final String FULL_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

  private static final List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
  static {
    formatters.add(dfYearMonthDay);
    formatters.add(dfYearMonth);
    formatters.add(dfYearMonthDayNoDash);
  }

  /**
   * Private constructor.
   */
  private DateUtil() {}

  /**
   * Formats the date to the format: yyyyMMdd
   *
   * @param date the date as a {@code java.util.Date}
   * @return the {@code String} that represents the date as yyyyMMdd
   */
  public static String formatYearMonthDayNoDash(Date date) {
    return DateUtil.formatYearMonthDayNoDash(new DateTime(date));
  }

  /**
   * Formats the date to the format: yyyyMMdd
   *
   * @param date the date as a {@code org.jodatime.DateTime}
   * @return the {@code String} that represents the date as yyyyMMdd
   */
  public static String formatYearMonthDayNoDash(DateTime date) {
    return DateUtil.dfYearMonthDayNoDash.print(date);
  }

  /**
   * Formats the date to the ISO format without the time zone information:
   * yyyy-MM-dd
   *
   * @param date the date as a {@code java.util.Date}
   * @return the {@code String} that represents the date in ISO format
   */
  public static String formatYearMonthDay(Date date) {
    return DateUtil.formatYearMonthDay(new DateTime(date));
  }

  /**
   * Formats the date to the ISO format without the time zone information:
   * yyyy-MM-dd
   *
   * @param date the date as a {@code org.jodatime.DateTime}
   * @return the {@code String} that represents the date in ISO format
   */
  public static String formatYearMonthDay(DateTime date) {
    return DateUtil.dfYearMonthDay.print(date);
  }

  /**
   * Formats the date to the ISO format without the time zone and day
   * information: yyyy-MM
   *
   * @param date the date as a {@code java.util.Date}
   * @return the {@code String} that represents the date in ISO format
   */
  public static String formatYearMonth(Date date) {
    return DateUtil.formatYearMonth(new DateTime(date));
  }

  /**
   * Formats the date to the ISO format without the time zone and day
   * information: yyyy-MM
   *
   * @param date the date as a {@code org.jodatime.DateTime}
   * @return the {@code String} that represents the date in ISO format
   */
  public static String formatYearMonth(DateTime date) {
    return DateUtil.dfYearMonth.print(date);
  }

  /**
   * Formats the date to the ISO format without the time zone and day
   * information: yyyyMM
   *
   * @param date the date as a {@code java.util.Date}
   * @return the {@code String} that represents the date in ISO format
   */
  public static String formatYearMonthNoDash(Date date) {
    return DateUtil.formatYearMonthNoDash(new DateTime(date));
  }

  /**
   * Formats the date to the ISO format without the time zone and day
   * information: yyyyMM
   *
   * @param date the date as a {@code org.jodatime.DateTime}
   * @return the {@code String} that represents the date in ISO format
   */
  public static String formatYearMonthNoDash(DateTime date) {
    return DateUtil.dfYearMonthNoDash.print(date);
  }

  /**
   * Attempts to parse the given {@code String} to a {@code DateTime} using one
   * of the known formatters.
   *
   * The attempt falls back to all the formatters, and if the format is unknown,
   * {@code null} is returned.
   *
   * @param timestamp the time stamp in {@code String} format.
   * @return the parsed {@code DateTime}, or {@code null} in case that the
   *         format is unknown.
   */
  public static DateTime parseDateTime(String timestamp) {

    if (timestamp != null) {

      for (DateTimeFormatter formatter : DateUtil.formatters) {
        try {
          LocalDateTime localDateTime = formatter.parseLocalDateTime(timestamp);
          return localDateTime.plusHours(12).toDateTime(DateTimeZone.UTC);

        } catch (IllegalArgumentException e) {
          // silently skips to the next formatter
        }
      }
    }
    return null;
  }

  /**
   * Create a {@code DateTime} that represents the last month, and formats it to
   * the yyyy-MM format.
   *
   * @return the date formatted
   */
  public static String lastMonthInYearMonthFormat() {

    DateTime lastMonth = new DateTime().minusMonths(1);
    return DateUtil.dfYearMonth.print(lastMonth);
  }

  /**
   * Get a DateTime for the first day of the previous month.
   * 
   * @return DateTime
   */
  public static DateTime firstDayPreviousMonth() {
    return new DateTime().minusMonths(1).dayOfMonth().withMinimumValue();
  }

  /**
   * Get a DateTime for the last day of the previous month.
   * 
   * @return DateTime
   */
  public static DateTime lastDayPreviousMonth() {
    return new DateTime().minusMonths(1).dayOfMonth().withMaximumValue();
  }

  /**
   * Get a DateTime for the first day of a month.
   * 
   * @return DateTime
   */
  public static DateTime firstDayMonth(DateTime datetime) {
    return datetime.dayOfMonth().withMinimumValue();
  }

  /**
   * Get a DateTime for the last day of a month.
   * 
   * @return DateTime
   */
  public static DateTime lastDayMonth(DateTime datetime) {
    return datetime.dayOfMonth().withMaximumValue();
  }

  /**
   * Get a DateTime in the Google Charts format
   * 
   * Important: This data will be used in JavaScript, where months are indexed starting at zero and go up through eleven,
   * with January being month 0 and December being month 11
   * 
   * @return String
   */
  public static String getGoogleChartsDate(Date date) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    return "Date(" + calendar.get(Calendar.YEAR) + "," + calendar.get(Calendar.MONTH) + "," + calendar.get(Calendar.DATE) + ")";
  }

  public static String getGoogleChartsDateTime(Date date) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    return "Date(" + calendar.get(Calendar.YEAR) + "," + calendar.get(Calendar.MONTH) + "," + calendar.get(Calendar.DATE) + "," +
                     calendar.get(Calendar.HOUR) + "," + calendar.get(Calendar.MINUTE) + "," + calendar.get(Calendar.SECOND) + "," + calendar.get(Calendar.MILLISECOND) + ")";
  }
}
