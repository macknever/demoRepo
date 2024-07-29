package com.lawrence.corejava.inputandoutput;

import java.util.regex.Matcher;
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

    /**
     * Remove duplicated words from a sentence
     * <p>
     * e.g. We went went to library -> we went to library
     * </p>
     *
     * <li>
     *     \\b : boundary of word
     *     \\w : [a-zA-Z0-9_]
     *     (?: )
     * </li>
     *
     * @return Origin sentence without duplication
     */
    public static String matchSequences(String sentence) {
        final String dupliateFinderRegex = "\\b(\\w+)(?:\\W+\\1\\b)+";

        Pattern pattern = Pattern.compile(dupliateFinderRegex);
        Matcher matcher = pattern.matcher(sentence);

        while (matcher.find()) {
            sentence = sentence.replace(matcher.group(), matcher.group(1));
        }

        return sentence;

    }
}
