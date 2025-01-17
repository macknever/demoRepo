package com.globalrelay.nucleus.testrail;

public class TestRailAnnotationNotUsedException extends RuntimeException {
    private static final String ERROR_MESSAGE = "Use @TestRail annotation...";

    public TestRailAnnotationNotUsedException() {
        super(ERROR_MESSAGE);
    }
}
