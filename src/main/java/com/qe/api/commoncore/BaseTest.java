package com.qe.api.commoncore;

import java.util.Map;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.qe.commoncore.constants.ContextConstant;
import com.qe.commoncore.utils.AssertionUtils;
import com.qe.commoncore.utils.EmailUtil;
import com.qe.commoncore.utils.ReportingUtil;
import com.qe.commoncore.utils.TestSetupUtils;

@Listeners({ com.qe.api.commoncore.TestListener.class })
public class BaseTest {

 // public static CommonConfig config;
	public static ReportingUtil reporter;
	public static AssertionUtils assertion;
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
//        Class<?>[] parameters = testResult.getMethod().getConstructorOrMethod().getParameterTypes();
//        Jira jiraDetails = classDetails.getMethod(className, parameters).getAnnotation(Jira.class);
//                   
        System.out.println("Ending test:" + methodName);
	}

	/**
	 * AfterSuite runs after all the test executions.
	 *
	 * @param context ITestContext is test result data provided by testng.
	 */
	@AfterSuite(alwaysRun = true)
	public void afterSuite(ITestContext context) {
        reporter.extent.flush();
        //send mail
        if(Boolean.parseBoolean(configurator.getParameter(ContextConstant.TRIGGER_MAIL)))
        email.sendMail();
	}

}
