package com.globalrelay.nucleus.testrail;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TestResultDataTest {

    @Test
    void testMerge() {
        final String ex1 = "EX1";
        final String ex2 = "EX2";
        final String testRailId = "c123";
        final String defects = "defects";
        final String annotatedVersion = "v1";

        TestResultData original = new TestResultData();
        original.setTestrailId(testRailId);
        original.setExceptionMsg(ex1);
        original.setDuration("1.5");
        original.setResultName(TestStatus.PASSED.name());
        original.setDefects(defects);
        original.setAnnotatedVersion(annotatedVersion);

        TestResultData other = new TestResultData();
        other.setExceptionMsg(ex2);
        other.setDuration("2.5");
        other.setResultName(TestStatus.FAILED.name());

        original.merge(other);

        assertEquals(testRailId, original.getTestrailId());
        assertEquals(defects, original.getDefects());
        assertEquals(annotatedVersion, original.getAnnotatedVersion());
        assertEquals(ex1 + " \n==========\n " + ex2, original.getExceptionMsg());
        assertEquals("4.0", original.getDuration());
        assertEquals(TestStatus.FAILED.name(), original.getResultName());
    }
}
