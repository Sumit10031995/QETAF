package com.qe.commoncore.utils;

import java.util.Objects;

import org.testng.ITestResult;

import com.qe.commoncore.annotations.Jira;
import com.qe.retry.Retry;
import com.qe.retry.RetryListener;

public class TestSetupUtils {

	private static final String EMPTY_STRING = "";

	// public static CommonConfig config;
	public static ReportingUtil reporter;
	public static AssertionUtils assertion;
	protected static Configurator configurator = Configurator.getInstance();
	private static ThreadLocal<Integer> counter = ThreadLocal.withInitial(()->0);

	// variable to store the method identifier, which refers to the fully qualified
	// method name (e.g., packageName.className.methodName)
	private static ThreadLocal<String> testMethodIdentifier = new ThreadLocal<String>();


	/**
	 * 
	 * @param testResult
	 * @throws Exception
	 * @description It will fetch Jira details and add to Extent report. If user
	 *              enable retry feature then it will append retry count to the
	 *              method name and log to Extent report
	 */
	public static void fetchJiraDetailsAndAddToReport(ITestResult testResult,Object[] testDataRow) throws Exception {
		// Start Extent Test
		Class classDetails = testResult.getMethod().getRealClass();
		String method = testResult.getMethod().getMethodName();
		String methodName = getMethodName(testDataRow,classDetails.toString(),method);
		Class<?>[] parameters = testResult.getMethod().getConstructorOrMethod().getParameterTypes();

		Jira jiraDetails = classDetails.getMethod(method, parameters).getAnnotation(Jira.class);
		if (Objects.nonNull(jiraDetails) && Objects.nonNull(jiraDetails.jiraTestKey())
					&& !EMPTY_STRING.equalsIgnoreCase(jiraDetails.jiraTestKey())) {
				ReportingUtil.methodVsTest.put(testMethodIdentifier.get(),
						reporter.startTest(methodName, classDetails.getCanonicalName() + " :: "
								+ JavaUtils.getJiraKeyasLink(jiraDetails.jiraTestKey())));
				
		} else {
			ReportingUtil.methodVsTest.put(testMethodIdentifier.get(),
					reporter.startTest(methodName, classDetails.getCanonicalName()));
		}
	}

	/**
	 * 
	 * @param identifier
	 * @return String
	 * @Desc This method dynamically generates a test method name by analyzing the
	 *       retry count and scenario details.
	 */
	private static String getMethodName(Object[] dataProvider,String cls, String method) {
		String identifier = cls + "." + method;
		
		String scenario = "";
		if (dataProvider.length > 0) {
			for (Object obj : dataProvider) {
				String name = obj.toString().toLowerCase();
				if (name.contains("scenario")) {
					scenario = name;
					break;
				}
			}
			identifier += scenario;
		}

		if (dataProvider.length > 0 && scenario.equals(EMPTY_STRING)) {
			counter.set(counter.get()+1);
			scenario = String.valueOf(counter.get());
			identifier += scenario;
		}

		testMethodIdentifier.set(identifier);
		Boolean isRetry = RetryListener.isRetrying(testMethodIdentifier.get());

		if (!scenario.equals(EMPTY_STRING))
			method = method + "_" + scenario;
		if (isRetry)
			method = method + ":RETRY-" + Retry.retryCount.get();

		return method;
	}
}
