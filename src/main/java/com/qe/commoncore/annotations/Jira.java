package com.qe.commoncore.annotations;

import java.lang.annotation.*;

import com.qe.commoncore.constants.GlobalConstants;

/**
 *  Jira annotation is a custom annotation for storing jira and csv key.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Target(ElementType.METHOD)
public @interface Jira {
    String jiraTestKey() default GlobalConstants.EMPTY_STRING;

    String csvTestKey() default GlobalConstants.EMPTY_STRING;

}