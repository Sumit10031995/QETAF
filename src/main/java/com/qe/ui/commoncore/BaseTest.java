package com.qe.ui.commoncore;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.qe.api.commoncore.Configurator;
import com.qe.commoncore.constants.ContextConstant;
import com.qe.commoncore.utils.AssertionUtils;
import com.qe.commoncore.utils.FileUtil;
import com.qe.commoncore.utils.ReportingUtil;

@Listeners(com.qe.ui.commoncore.TestListener.class)
public class BaseTest {
	private static final Logger logger = Logger.getLogger(BaseTest.class.getName());
	protected static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<WebDriver>();
	private static final String uiConfigProperty = "uiConfig.properties";

	
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
	protected void lunchBrowser() throws Exception {
		System.setProperty("webdriver.http.factory", "jdk-http-client");
		String browser = Configurator.getInstance().getParameter(ContextConstant.BROWSER);

		if (browser.toLowerCase().contains("chrome")) {
			ChromeOptions co = new ChromeOptions();
			tlDriver.set(new ChromeDriver(co));
		} else if (browser.toLowerCase().contains("firefox")) {
			tlDriver.set(new FirefoxDriver());
		} else {
			throw new Exception("Invalid browser name");
		}
		getDriver().manage().window().maximize();
		getDriver().manage().deleteAllCookies();
		getDriver().manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		getDriver().get(FileUtil.getPropertyDetails(uiConfigProperty, "com.qe.ui.app.url"));
		getDriver().manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

	}

	/**
	 * 
	 * @return this method use to get driver object
	 */
	public static WebDriver getDriver() {
		return tlDriver.get();
	}

	/**
	 * this method use to quit the browser
	 */
	@AfterMethod(alwaysRun = true)
	protected static void quitQuit() {
		getDriver().quit();
		logger.info("Browser closed successfully");

	}

//	public <T> T initializeElements(Class<T> pageObjectClass) {
//		T page = PageFactory.initElements(getDriver(), pageObjectClass);
//		return page;
//	}

}
