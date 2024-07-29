package com.lawrence.corejava.regex;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.lawrence.corejava.inputandoutput.RegexExample;

class RegexExercisesTest {

    /**
     * Write a Java program to check whether a string contains only a certain set of characters (in this case a-z,
     * A-Z and 0-9).
     */
    @Test
    void groupMatchShouldWork() {

        final String PATTERN_ONE = "\\w+";

        final String toBeCheck = RandomStringUtils.random(10, true, true);
        Assertions.assertTrue(RegexExample.isMatch(PATTERN_ONE, toBeCheck));
    }

    /**
     * Write a Java program that matches a string that has a p followed by zero or more q's.
     */
    @Test
    void matchPQ() {
        final String patternPQ = "^pq+";
        final String patternPQ2 = "^pq*";
        final String tobeMatch = "p";
        Assertions.assertTrue(RegexExample.isMatch(patternPQ, "p", "pq", "pqqq"));
    }

    @Test
    void removeDuplicatedTest() {
        String duplicated = "Team Team is good good";
        String correct = "Team is good";
        Assertions.assertEquals(correct, RegexExample.removeDuplicated(duplicated));
    }

    @Test
    void emailAddressTest() {
        String email1 = "abc@coolmail.com";
        String notEmail = "abccool";
        Assertions.assertTrue(RegexExample.isEmailAddress(email1));
        Assertions.assertFalse(RegexExample.isEmailAddress(notEmail));
    }

}
