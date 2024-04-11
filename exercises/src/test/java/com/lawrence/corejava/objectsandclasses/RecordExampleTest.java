package com.lawrence.corejava.objectsandclasses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecordExampleTest {

    @Test
    void recordShouldWorkWithCanonicalConstructor() {
        RecordExample.Point a = new RecordExample.Point(1.0,1.0);
        assertEquals(1.0d, a.x());
    }

    @Test
    void recordShouldWorkWithNonCanonicalConstructor() {
        RecordExample.Point a = new RecordExample.Point(1.0);
        assertEquals(0.0d, a.y());
    }

    @Test
    void recordShouldWorkWithCustomizedCanonicalConstructor() {
        RecordExample.Range range = new RecordExample.Range(100, 0);
        assertEquals(100, range.max());
    }

}