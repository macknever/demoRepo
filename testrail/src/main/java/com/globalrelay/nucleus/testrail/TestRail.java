package com.globalrelay.nucleus.testrail;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TestRail {
    /**
     * Corresponding TestRail testcase ID.
     * With or without the leading "C" prefix.
     * For example, "C1234".
     */
    String id() default "";

    /**
     * Optional test case version.
     * For example, "1.0".
     */
    String version() default "";

    /**
     * Optional Jira ticket number containing requirements for the test.
     * For example, "NUC-1234".
     */
    String requirement() default "";

    /**
     * Optional Jira ticket numbers of related defects.
     * Multiple tickets separated by a comma.
     */
    String defects() default "";
}
