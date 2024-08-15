package com.lawrence.corejava.datetime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeExample {

    private static final String SIMPLE_DATE_FORMAT = "MM-yy-dd";
    private static final String SIMPLE_TIME_FORMAT = "hh:mm:ss";

    public void showDate(final LocalDate date, final String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        final String formattedDate = formatter.format(date);
        System.out.println(formattedDate);
    }

    public void showTime(final LocalTime time, final String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        final String formattedTime = formatter.format(time);
        System.out.println(formattedTime);
    }

    public void showSimpleDate(final LocalDate date) {
        showDate(date, SIMPLE_DATE_FORMAT);
    }

    public void showSimpleTime(final LocalTime time) {
        showTime(time, SIMPLE_TIME_FORMAT);
    }

}
