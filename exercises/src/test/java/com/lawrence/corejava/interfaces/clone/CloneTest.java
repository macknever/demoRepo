package com.lawrence.corejava.interfaces.clone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CloneTest {

    private static final String JOHN = "john";
    private static final String PETER = "peter";
    private static final String MATH = "math";
    private static final String MUSIC = "music";

    @Test
    void objectCloneShouldWorkIfClassImplementsCloneable() throws CloneNotSupportedException {
        Student john = new Student(JOHN);
        Student peter = (Student) john.clone();
        peter.setName(PETER);
        assertThat(peter.getName()).isEqualTo(PETER);
        assertThat(john.getName()).isEqualTo(JOHN);
    }

    @Test
    void cloneThrowsExceptionIfNotImplementCloneable() {
        Teacher mathTeacher = new Teacher(JOHN, MATH);
        assertThrows(CloneNotSupportedException.class, () -> mathTeacher.clone());
    }

}
