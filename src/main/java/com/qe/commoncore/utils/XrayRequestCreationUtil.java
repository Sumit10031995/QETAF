package com.qe.commoncore.utils;


import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import com.qe.api.enums.TestStatus;
import com.qe.commoncore.constants.XrayIntegration;
import com.qe.commoncore.model.TestCaseStatus;

public class XrayRequestCreationUtil {

    private static final Logger log =LoggerFactory.getLogger(XrayRequestCreationUtil.class);
    private static final String EMPTY_STRING="";

    private XrayRequestCreationUtil() {
    }

    /**
     * XrayRequest is for creating TestCaseStatus list.
     *
     * @param context Set of ITestResult.
     *
     * @return List of test cases updated with test statuses.
     */
    public static List<TestCaseStatus> xrayRequest(Set<ITestResult> context) {
        List<TestCaseStatus> testCaseStatuses = new LinkedList<>();
        TestCaseStatus testCaseStatus = null;
        String testCaseName = "";
        
        for (ITestResult testResult : context) {
            testCaseStatus = new TestCaseStatus();
            testCaseName = testResult.getName();
            switch (testResult.getStatus()) {
                case 1:
                    testCaseStatus.setStatus(TestStatus.PASS);
                    break;
                case 2:
                    testCaseStatus.setStatus(TestStatus.FAIL);
                    break;
                default:
                    testCaseStatus.setStatus(TestStatus.BACKLOG);
            }

            Object jiraTestKey = testResult.getAttribute(XrayIntegration.JIRA_TEST_KEY);

            if (Objects.nonNull(jiraTestKey) && !EMPTY_STRING.equalsIgnoreCase((String) jiraTestKey)) {
                log.info("JIRA TEST KEY: {}", jiraTestKey);
                testCaseStatus.setTestKey(jiraTestKey.toString());
            } else {
                testCaseStatus = null;
                //log.error("Invalid jira key");
                log.warn("JIRA key not found for test: " + testCaseName);
            }
            if (Objects.nonNull(testCaseStatus)) {
                testCaseStatuses.add(testCaseStatus);
            }
        }

        return testCaseStatuses;
    }
}
