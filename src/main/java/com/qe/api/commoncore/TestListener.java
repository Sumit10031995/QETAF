package com.qe.api.commoncore;

import java.util.Objects;

import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.Status;
import com.qe.commoncore.annotations.Jira;
import com.qe.commoncore.utils.JavaUtils;

public class TestListener implements ITestListener {

	private static final String EMPTY_STRING = "";

	@Override
	public void onTestSkipped(ITestResult testResult) {
		try {
			// 'wasRetried()' method return true if the test method is part of retry, else
			// false.
			// This mechanism helps to identify retry skipped method details.
			// If test is skipped due to a retry action then we are not logging those
			// details to extent report
			if (!testResult.wasRetried()) {

				Class classDetails = testResult.getMethod().getRealClass();
				String method = testResult.getMethod().getMethodName();
				Class<?>[] parameters = testResult.getMethod().getConstructorOrMethod().getParameterTypes();

				Jira jiraDetails = classDetails.getMethod(method, parameters).getAnnotation(Jira.class);

				if (Objects.nonNull(jiraDetails) && Objects.nonNull(jiraDetails.jiraTestKey())
						&& !EMPTY_STRING.equalsIgnoreCase(jiraDetails.jiraTestKey())) {
					BaseTest.reporter.startTest(method, classDetails.getCanonicalName() + " :: "
							+ JavaUtils.getJiraKeyasLink(jiraDetails.jiraTestKey()));

				} else {
					BaseTest.reporter.startTest(method, classDetails.getCanonicalName());
				}
				BaseTest.reporter.createTestStep(testResult.getMethod().getMethodName());
				BaseTest.reporter.logTestStepDetails(Status.SKIP, testResult.getThrowable().getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * TestNG retry mechanism will work if assertions are inside a @Test method. The
	 * retry mechanism is specially designed to re-run the @Test method in case of
	 * failure.
	 * 
	 * Below method present in IInvokedMethodListener interface. This is the other
	 * approach to call assertAll() method, But while using this method code will
	 * get assertion error outside of @Test method and the result is retry mechanism
	 * won't work.
	 */
//	@Override
//	public void afterInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
//		if(method.isTestMethod()) {
//			BaseTest.assertion.assertAll();
//		}
//	}

	/**
	 * This method is use to log failure status to the extent report, on test
	 * failure
	 */
	@Override
	public void onTestFailure(ITestResult testResult) {
		AssertionError assertionError = new AssertionError(testResult.getThrowable().getMessage(),
				testResult.getThrowable());
		testResult.setThrowable(assertionError);
		BaseTest.reporter.logTestStepDetails(Status.FAIL, testResult.getThrowable().getMessage());
	}

}