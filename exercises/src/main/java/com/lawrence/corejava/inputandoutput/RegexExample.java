package com.lawrence.corejava.inputandoutput;

import java.util.regex.Pattern;

public class RegexExample {
    public static boolean isMatch(final String regex, final String... toBeChecks) {
        Pattern pattern = Pattern.compile(regex);
        boolean ret = true;
        for (String toBeCheck : toBeChecks) {
            boolean isMatch = pattern.matcher(toBeCheck).matches();
            ret &= isMatch;
            if (!isMatch) {
                System.out.printf("%s is not a match of %s \n", toBeCheck, regex);
            }
        }
        return ret;
    }
}
