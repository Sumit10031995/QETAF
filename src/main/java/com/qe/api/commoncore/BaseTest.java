package com.qe.api.commoncore;

import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.qe.commoncore.annotations.Jira;
import com.qe.commoncore.constants.ContextConstant;
import com.qe.commoncore.constants.XrayIntegration;
import com.qe.commoncore.model.TestCaseStatus;
import com.qe.commoncore.utils.AssertionUtils;
import com.qe.commoncore.utils.Configurator;
import com.qe.commoncore.utils.EmailUtil;
import com.qe.commoncore.utils.KeyIndexInfoTreadUtil;
import com.qe.commoncore.utils.ReportingUtil;
import com.qe.commoncore.utils.TestSetupUtils;
import com.qe.xray.utils.XrayRequestCreationUtil;
import com.qe.xray.utils.XrayUtil;


@Listeners({ com.qe.api.commoncore.TestListener.class })
public class BaseTest {

 // public static CommonConfig config;
	private static final String EMPTY_STRING="";
	public static ReportingUtil reporter;
	public static AssertionUtils assertion;
    public static XrayUtil xrayUtil;
	public static Configurator configurator = Configurator.getInstance();
    private static final EmailUtil email=new EmailUtil();

	@BeforeSuite(alwaysRun = true)
	public void beforeSuite(ITestContext context) throws Throwable {
		Map<String, String> params = context.getCurrentXmlTest().getAllParameters();
		configurator.initializeParameters(params);

		// Initialize and configure report
		reporter = ReportingUtil.getInstance();
		// reporter.configureReport(context.getSuite().getName(),configurator.getEnvironmentParameter(ContextConstant.ENV_NAME));
		reporter.configureReport(context.getSuite().getName(), configurator.getParameter(ContextConstant.ENV_NAME));

		// Initialize assertion
		assertion = new AssertionUtils(reporter);
		
		//Initialize xRayUtil for JIRA/XRAY Integration
        xrayUtil = XrayUtil.getInstance();

	}

	/**
     * BeforeMethod runs before every test execution. 
     * It adds a new test in the extent report 
     * @throws Exception  
     * @BeforeMethod annotated methods. TestNG can inject only one of <ITestResult ,ITestContext, XmlTest, Method, Object[], ITestResult>
     */
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(ITestResult testResult, Object[] testDataRow) throws Exception
    {
    	TestSetupUtils.fetchJiraDetailsAndAddToReport(testResult,testDataRow);
    	System.out.println("Starting test:" + testResult.getMethod().getMethodName());
    }
	
	
	/**
	 * AfterMethod runs after every test execution. T
	 * @param testResult ITestResult is the result object provided by testng.
	 * @throws Exception
	 */
	@AfterMethod(alwaysRun = true)
	public void afterMethod(ITestResult testResult) throws Exception {
		 Class classDetails = testResult.getMethod().getRealClass();
	        String className = testResult.getName();
	        String methodName = testResult.getMethod().getMethodName();
	        Class<?>[] parameters = testResult.getMethod().getConstructorOrMethod().getParameterTypes();

	        Jira jiraDetails = classDetails.getMethod(className, parameters).getAnnotation(Jira.class);
	        if (Objects.nonNull(jiraDetails)) {
	            if (Objects.nonNull(jiraDetails.jiraTestKey()) && !EMPTY_STRING.equalsIgnoreCase(jiraDetails.jiraTestKey())) {
	                testResult.setAttribute(XrayIntegration.JIRA_TEST_KEY, jiraDetails.jiraTestKey());
	            } else if (Objects.nonNull(jiraDetails.csvTestKey()) && !EMPTY_STRING.equalsIgnoreCase(jiraDetails.csvTestKey())) {

	                Map<String, Integer> keyDetailsIndexMapping = KeyIndexInfoTreadUtil.getKeyIndexInfoDetails();

	                if (Objects.nonNull(keyDetailsIndexMapping) &&
	                        Objects.nonNull(keyDetailsIndexMapping.get(XrayIntegration.JIRA_TEST_KEY)) &&
	                        Objects.nonNull(keyDetailsIndexMapping.get(XrayIntegration.CSV_TEST_KEY))) {

	                    int jiraTestKeyIndex = keyDetailsIndexMapping.get(XrayIntegration.JIRA_TEST_KEY);
	                    int csvTestKeyIndex = keyDetailsIndexMapping.get(XrayIntegration.CSV_TEST_KEY);
	                    Object[] inputData = testResult.getParameters();
	                    Object jiraTestKey = inputData[jiraTestKeyIndex];
	                    Object csvTestKey = inputData[csvTestKeyIndex];

	                    if (Objects.nonNull(jiraTestKey) && Objects.nonNull(csvTestKey) &&
	                            jiraDetails.csvTestKey().equalsIgnoreCase(csvTestKey.toString())) {
	                        testResult.setAttribute(XrayIntegration.JIRA_TEST_KEY, jiraTestKey);
	                    }
	                }
	            }
	        }
        System.out.println("Ending test:" + methodName);
	}

	/**
	 * AfterSuite runs after all the test executions.
	 *
	 * @param context ITestContext is test result data provided by testng.
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	@AfterSuite(alwaysRun = true)
	public void afterSuite(ITestContext context) throws NoSuchMethodException, SecurityException {
		 //boolean updateReportInJira = config.generateJiraReport();
        boolean updateReportInJira = Boolean.parseBoolean(configurator.getParameter(ContextConstant.PUBLISH_RESULTS_TO_XRAY));
        String executionKey = configurator.getParameter(ContextConstant.JIRA_TESTEXECUTION_KEY);

        Stack<TestCaseStatus> testCaseStatusStack = new Stack<>();
        if (updateReportInJira) {
        	testCaseStatusStack.addAll(XrayRequestCreationUtil.xrayRequest(context.getPassedTests().getAllResults()));
        	testCaseStatusStack.addAll(XrayRequestCreationUtil.xrayRequest(context.getFailedTests().getAllResults()));
        	testCaseStatusStack.addAll(XrayRequestCreationUtil.xrayRequest(context.getSkippedTests().getAllResults()));
        }

        if (!testCaseStatusStack.isEmpty()) {
             xrayUtil.addJiraIssue(testCaseStatusStack);
        }
        reporter.extent.flush();
        //send mail
        if(Boolean.parseBoolean(configurator.getParameter(ContextConstant.TRIGGER_MAIL)))
        email.sendMail();
	}

}
