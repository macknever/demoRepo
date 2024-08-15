package com.lawrence.corejava.datetime;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class DateTimeExampleTest {

    DateTimeExample de = new DateTimeExample();

    @Test
    void showDateExample() {
        final LocalDate date = LocalDate.now();
        System.out.println(date);
        de.showSimpleDate(date);
    }

    @Test
    void showTimeExample() {
        final LocalTime time = LocalTime.now();
        System.out.println(time);
        de.showSimpleTime(time);
    }

}
