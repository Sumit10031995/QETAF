package com.qe.ui.commoncore;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.aventstack.extentreports.Status;
import com.qe.api.commoncore.Configurator;
import com.qe.commoncore.constants.ContextConstant;
import com.qe.commoncore.utils.AssertionUtils;
import com.qe.commoncore.utils.EmailUtil;
import com.qe.commoncore.utils.FileUtil;
import com.qe.commoncore.utils.ReportingUtil;
import com.qe.commoncore.utils.TestSetupUtils;
import com.qe.ui.utils.UIUtility;

@Listeners(com.qe.ui.commoncore.TestListener.class)
public class BaseTest extends UIUtility implements BrowserDriver{
	private static final Logger logger = Logger.getLogger(BaseTest.class.getName());
	private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<WebDriver>();
	private static final String uiConfigProperty = "uiConfig.properties";
    private static final EmailUtil email=new EmailUtil();

	// public static CommonConfig config;
	public static ReportingUtil reporter;
	public static AssertionUtils assertion;
	protected static Configurator configurator = Configurator.getInstance();


	@BeforeSuite(alwaysRun = true)
	public  void beforeSuite(ITestContext context) throws Throwable {
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
	 * 
	 * @throws Exception this method is use to lunch a browser
	 */
	@BeforeMethod(alwaysRun = true)
	protected void lunchBrowser(ITestResult testResult, Object[] testDataRow) throws Exception {
		//System.setProperty("webdriver.http.factory", "jdk-http-client");
		String browser = Configurator.getInstance().getParameter(ContextConstant.BROWSER);

		if (browser.toLowerCase().contains("chrome")) {
			ChromeOptions co = new ChromeOptions();
			if(Boolean.parseBoolean(Configurator.getInstance().getParameter(ContextConstant.ENABLE_HEADLESS_MODE))) {
			co.setHeadless(true);
			}
			tlDriver.set(new ChromeDriver(co));
		} else if (browser.toLowerCase().contains("firefox")) {
			tlDriver.set(new FirefoxDriver());
		} else {
			reporter.logTestStepDetails(Status.FAIL,"Invalid browser name");
			throw new Exception("Invalid browser name");
		}
		
		initUtrility(getDriver());
		getDriver().manage().window().maximize();
		//getDriver().manage().deleteAllCookies();
		getDriver().manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		getDriver().get(FileUtil.getPropertyDetails(uiConfigProperty, "com.qe.ui.app.url"));
		getDriver().manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
    	TestSetupUtils.fetchJiraDetailsAndAddToReport(testResult,testDataRow);
    	System.out.println("Starting test:" + testResult.getMethod().getMethodName());	
    	}

	/**
	 * 
	 * @return this method use to get driver object
	 */
	@Override
	public WebDriver getDriver() {
		return tlDriver.get();
	}

	/**
	 * this method use to quit the browser
	 */
	@AfterMethod(alwaysRun = true)
	protected void quitQuit(ITestResult testResult) {
		getDriver().quit();
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
