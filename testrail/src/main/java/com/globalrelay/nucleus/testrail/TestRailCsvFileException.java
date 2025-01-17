package com.globalrelay.nucleus.testrail;

public class TestRailCsvFileException extends RuntimeException {
    public TestRailCsvFileException(String message, Throwable exception) {
        super(message, exception);
    }
}
