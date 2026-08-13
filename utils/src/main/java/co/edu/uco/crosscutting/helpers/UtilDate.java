package co.edu.uco.crosscutting.helpers;

import co.edu.uco.crosscutting.exceptions.CrossWordsException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Date;

import static co.edu.uco.crosscutting.helpers.EnumConstants.DATE_FORMAT;
import static co.edu.uco.crosscutting.helpers.EnumConstants.ERROR_DATE_FORMAT_INVALID;
import static co.edu.uco.crosscutting.helpers.UtilNumeric.ZERO;
import static co.edu.uco.crosscutting.helpers.UtilObject.*;

public final class UtilDate {
    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern(DATE_FORMAT.getValue())
            .optionalStart()
            .appendFraction(ChronoField.MICRO_OF_SECOND, ZERO, 9, true)
            .optionalEnd()
            .toFormatter();
    public static final LocalDateTime TIME = LocalDateTime.now();
    private UtilDate() {}
    public static boolean isNull(Date date) {
        return isNullObject(date);
    }
    public static Date getDefaultIsNull(Date value) {
        return getDefaultIsNullObject(value, new Date());
    }
    public static LocalDateTime getDefaultTime(LocalDateTime value, LocalDateTime defaultValue) {
        return getDefaultIsNullObject(value,defaultValue);
    }
    public static LocalDateTime getDefaultTimeIfNull(LocalDateTime value) {
        return getDefaultTime(value,TIME);
    }
    public static boolean isBetween(Date date, Date init, Date end) {
        return (date.after(init) && date.before(end));
    }
    public static boolean isBefore(Date compare, Date date) {
        return compare.before(date);
    }
    public static boolean isBefore(Date compare) {
        return compare.before(getLocalDataTimeADate(TIME));
    }
    public static boolean isBetweenIncludingInit(Date date, Date init, Date end) {
        return (isBetween(date, init, end) || date.equals(init));
    }
    public static boolean isBetweenIncludingEnd(Date date, Date init, Date end) {
        return (isBetween(date, init, end) || date.equals(end));
    }
    public static boolean isBetweenIncludingRanges(Date date, Date init, Date end) {
        return (isBetweenIncludingEnd(date, init, end) || isBetweenIncludingInit(date, init, end));
    }
    public static LocalDate currentDate() {
        return LocalDate.now();
    }
    public static Date getLocalDateADate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    public static LocalDate getDateALocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    public static Date getLocalDataTimeADate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    public static LocalDateTime getDateALocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    public static LocalDateTime parseDate(String date) {
        try {
            return LocalDateTime.parse(date, formatter);
        } catch (DateTimeParseException exception) {
            throw CrossWordsException.build(ERROR_DATE_FORMAT_INVALID.getValue(), exception);
        }
    }
    public static LocalDateTime parseDate(String date, String pattern) {
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(date, customFormatter);
    }
}