package com.qe.commoncore;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.qe.commoncore.annotations.Jira;
import com.qe.commoncore.constants.ContextConstant;
import com.qe.commoncore.utils.AssertionUtils;
import com.qe.commoncore.utils.JavaUtils;
import com.qe.commoncore.utils.ReportingUtil;
import com.qe.commoncore.utils.TestDataUtil;
import com.qe.retry.Retry;
import com.qe.retry.RetryListener;


@Listeners({ com.qe.commoncore.TestListener.class })
public class BaseTest {

	private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

	private static final String EMPTY_STRING = "";

	// public static CommonConfig config;
	public static ReportingUtil reporter;
	public static AssertionUtils assertion;
	protected static Configurator configurator = Configurator.getInstance();

	// variable to store the method identifier, which refers to the fully qualified
	// method name (e.g., packageName.className.methodName)
	private static ThreadLocal<String> testMethodIdentifier = new ThreadLocal<String>();
	// Variable to store the default scenario name (scenarioName = random integer
	// value) in case data provider is used but schenario was not defined in CSV
	// file.
	private static ThreadLocal<Integer> defaultScenarioName = new ThreadLocal<Integer>();

	@BeforeSuite(alwaysRun = true)
	public static void beforeSuite(ITestContext context) throws Throwable {
		Map<String, String> params = context.getCurrentXmlTest().getAllParameters();
		configurator.initializeParameters(params);

		// Initialize and configure report
		reporter = ReportingUtil.getInstance();
		// reporter.configureReport(context.getSuite().getName(),configurator.getEnvironmentParameter(ContextConstant.ENV_NAME));
		reporter.configureReport(context.getSuite().getName(), configurator.getParameter(ContextConstant.ENV_NAME));

		// Initialize assertion
		assertion = new AssertionUtils(reporter);

	}

	/**
     * BeforeMethod runs before every test execution. 
     * It adds a new test in the extent report 
     * @throws Exception  
     * @BeforeMethod annotated methods. TestNG can inject only one of <ITestResult ,ITestContext, XmlTest, Method, Object[], ITestResult>
     */
    @BeforeMethod(alwaysRun = true)
    public static void beforeMethod(ITestResult testResult, Object[] testDataRow) throws Exception
    {
    	TestDataUtil.setTestDataMapWithHeaders(testDataRow);
    	fetchJiraDetailsAndAddToReport(testResult);
    	System.out.println("Starting test:" + testResult.getMethod().getMethodName());
    }
	
    /**
     * 
     * @param testResult
     * @throws Exception 
     * @description It will fetch Jira details and add to Extent report. 
     * If user enable retry feature then it will append retry count to the method name and log to Extent report
     */
	public static void fetchJiraDetailsAndAddToReport(ITestResult testResult) throws Exception {
		// Start Extent Test
		Class classDetails = testResult.getMethod().getRealClass();
		String method = testResult.getMethod().getMethodName();
		String methodName=getMethodName(classDetails.toString(),method);
		Class<?>[] parameters = testResult.getMethod().getConstructorOrMethod().getParameterTypes();

		Jira jiraDetails = classDetails.getMethod(method, parameters).getAnnotation(Jira.class);
		if (Objects.nonNull(jiraDetails)) {
			if (Objects.nonNull(jiraDetails.jiraTestKey())
					&& !EMPTY_STRING.equalsIgnoreCase(jiraDetails.jiraTestKey())) {
				ReportingUtil.methodVsTest.put(testMethodIdentifier.get(),reporter.startTest(methodName, classDetails.getCanonicalName() + " :: "
						+ JavaUtils.getJiraKeyasLink(jiraDetails.jiraTestKey())));
			} else if (Objects.nonNull(jiraDetails.csvTestKey())
					&& !EMPTY_STRING.equalsIgnoreCase(jiraDetails.csvTestKey())) {
				ReportingUtil.methodVsTest.put(testMethodIdentifier.get(),reporter.startTest(methodName, classDetails.getCanonicalName() + " :: " + jiraDetails.csvTestKey()));
			}
		} else {
			ReportingUtil.methodVsTest.put(testMethodIdentifier.get(),reporter.startTest(methodName, classDetails.getCanonicalName()));
		}
	}
	
	/**
	 * AfterMethod runs after every test execution. T
	 * @param testResult ITestResult is the result object provided by testng.
	 * @throws Exception
	 */
	@AfterMethod(alwaysRun = true)
	public static void afterMethod(ITestResult testResult) throws Exception {
//        Class classDetails = testResult.getMethod().getRealClass();
//        String className = testResult.getName();
//        String methodName = testResult.getMethod().getMethodName();
//        Class<?>[] parameters = testResult.getMethod().getConstructorOrMethod().getParameterTypes();
//        Jira jiraDetails = classDetails.getMethod(className, parameters).getAnnotation(Jira.class);
//                   
//		TestDataUtil.testDataMapWithHeaders.set(null);
//        System.out.println("Ending test:" + methodName);
	}

	/**
	 * AfterSuite runs after all the test executions.
	 *
	 * @param context ITestContext is test result data provided by testng.
	 */
	@AfterSuite(alwaysRun = true)
	public static void afterSuite(ITestContext context) {

	}

	/**
	 * 
	 * @param identifier
	 * @return String
	 * @Desc This method dynamically generates a test method name by analyzing the
	 *       retry count and scenario details.
	 */
	private static synchronized String getMethodName(String cls, String method) {
		String identifier = null;
		Map<String, String> dataprovider = TestDataUtil.testDataMapWithHeaders.get();
		Boolean isContainsScenarioDetails = dataprovider != null
				&& dataprovider.containsKey(TestDataUtil.scenarioClmName)
				&& !dataprovider.get(TestDataUtil.scenarioClmName).equals("");

		// Set the method identifier
		// Append scenario name with the method identifier if scenario values are
		// provided by the user
		// (e.g., identifier = packageName.ClassName.MethodName<_scenarioName>).
		if (isContainsScenarioDetails) {
			identifier = cls.toString() + "." + method + ":" + dataprovider.get(TestDataUtil.scenarioClmName);
			testMethodIdentifier.set(identifier);
		}
		// Append random integer value with the method identifier if scenario values are
		// not provided by the user
		// (e.g., identifier = packageName.ClassName.MethodName<_1234>).
		else if (dataprovider != null && (dataprovider.containsKey(TestDataUtil.scenarioClmName)
				&& dataprovider.get(TestDataUtil.scenarioClmName).equals("")
				|| !dataprovider.containsKey(TestDataUtil.scenarioClmName))) {
			if (Retry.retryCount.get() == 0) {
				defaultScenarioName.set(new Random().nextInt(10000));
			}
			identifier = cls.toString() + "." + method + ":" + defaultScenarioName.get();
			testMethodIdentifier.set(identifier);

		}
		// Else identifier = packageName.ClassName.MethodName
		else {
			identifier = cls.toString() + "." + method;
			testMethodIdentifier.set(identifier);
		}

		// Check if method is retrying
		Boolean isRetry = RetryListener.isRetrying(testMethodIdentifier.get());

		// Append scenario name or retry count to the method name if needed
		// Append scenario name to method name if scenario details are provided by the
		// user
		if (isContainsScenarioDetails)
			method = method + "_" + dataprovider.get(TestDataUtil.scenarioClmName);
		// Append retry count to method name if the method is retrying
		if (isRetry)
			method = method + ":RETRY-" + Retry.retryCount.get();

		return method;
	}

}
