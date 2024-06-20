package com.qe.commoncore.utils;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import io.restassured.response.Response;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;

public class ReportingUtil {
	public static ExtentReports extent;
	public static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<ExtentTest>();
	public static ThreadLocal<LinkedHashMap<String, ExtentTest>> extentTestSteps = new ThreadLocal<LinkedHashMap<String, ExtentTest>>();
	public static ExtentSparkReporter spartReporter;
	public static String nameFormat = null;
	public static String reportPath = null;
	private static ReportingUtil report = null;
	static String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	/**
	 * methodVsTest is employed to establish a correlation between the method name and its corresponding ExtentTest object. 
	 * This association allows us to retrieve the ExtentTest object in case of method retries, using the fully qualified method name. 
	 * Additionally, the removeTest method aids in the removal of the associated ExtentTest object from the ExtentReport when necessary.
    */
    public static Map<String, ExtentTest> methodVsTest=new HashMap();
    
	/**
	 * Singleton
	 */
	
	/**
	 * Private constructor
	 */
	private ReportingUtil() {
		// do nothing
	}

	/**
	 * initializes ReportingUtil object if it was not created earlier
	 */
	public static synchronized ReportingUtil getInstance() {
		if (report == null) {
			report = new ReportingUtil();
		}
		return report;
	}

	/**
	 * 
	 * Configures extent report
	 * 
	 * @param testReportName String
	 * @param testenv String
	 * @param serviceURL String
	 * @throws Exception 
	 * 
	 */
	public void configureReport(String testSuiteName, String testenv) throws Exception {
		try {
			
			//System.out.println(testReportName + "   "+  testenv);
			//spartReporter = new ExtentSparkReporter(System.getProperty("user.dir") + File.separator + "/test-report/ExtentReport.html");
			
			String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
			
			/* for now commenting the code for Jenkins email reporting
			*  String reportName = System.getProperty("user.dir") + File.separator + "/test-report/" + "Test_Results_" + testSuiteName.replace(" ", "_") + "_" + timeStamp + ".html";
			*/
			
			reportPath = System.getProperty("user.dir") + File.separator + "/test-report/" + "Test_Results_" + testSuiteName.replace(" ", "_")  + ".html";
			
			spartReporter = new ExtentSparkReporter(reportPath);
			
			extent = new ExtentReports();
			
			extent.attachReporter(spartReporter);
			
			//testenv = testenv.equals("")?"Unknown":testenv;
			extent.setSystemInfo("Environment Name:", testenv);
			
			//extent.setSystemInfo("ServiceURL:", serviceURL);
			extent.setSystemInfo("OS", System.getProperty("os.name"));
			
			//testReportName = testReportName.equals("")?"Test Report":testReportName;
			spartReporter.config().setReportName(testSuiteName);
			
			spartReporter.config().setTheme(Theme.STANDARD);
			
			spartReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
		
		
		} catch (Exception e) {
			throw new Exception("Report Configurations failed");
		}
	}
	
	/**
	 * 
	 * @param testName String
	 * @param desc String
	 * @return ExtentTest
	 * @throws Exception 
	 */
	public static synchronized ExtentTest startTest(String testName, String desc) throws Exception {
		try {
			ExtentTest test = extent.createTest(testName, desc);
			extentTest.set(test);
			extentTestSteps.set(null);
			return test;
		} catch (Exception e) {
			throw new Exception("Test creation failed");
		}
	}

	public static synchronized ExtentTest getTest() {
		return extentTest.get();
	}

	public static synchronized void removeTest() {
		extent.removeTest(extentTest.get());
	}

	public static synchronized LinkedHashMap<String, ExtentTest> createTestStepsMap(String stepName) {
		LinkedHashMap<String, ExtentTest> testSteps = new LinkedHashMap<String, ExtentTest>();
		ExtentTest testStep = extentTest.get().createNode(stepName);
		testSteps.put(stepName, testStep);
		extentTestSteps.set(testSteps);
		return testSteps;
	}

	public synchronized ExtentTest createTestStep(String stepName) {
		if (extentTestSteps.get() == null) {
			createTestStepsMap(stepName);
		}
		if (!extentTestSteps.get().containsKey(stepName)) {
			ExtentTest step = extentTest.get().createNode(stepName);
			extentTestSteps.get().put(stepName, step);
		}
		return extentTestSteps.get().get(stepName);
	}

	/*
	 * If caller method supplies a step name, this method will log info under that
	 * specific step. If step is not there in the steps map, we will create it.
	 */
	public synchronized void logTestStepDetails(Status status, String stepName, String stepDetails) {
		ExtentTest step = createTestStep(stepName);
		if (isJSONValid(stepDetails)) {
			step.log(status, MarkupHelper.createCodeBlock(stepDetails, CodeLanguage.JSON));
		} else {
			step.log(status, stepDetails);
		}
	}

	public synchronized void logTestStepDetails(Status status, String stepName, Throwable t) {
		ExtentTest step = createTestStep(stepName);
		step.fail(t);
	}


	/*
	 * If caller method doesn't provide a step name, we will add log to the most
	 * recent step.
	 * 
	 */
	public synchronized void logTestStepDetails(Status status, String stepDetails) {
		String defaultName="Default";
		
		if (extentTestSteps.get() != null) {
			String stepName = getLastTestStepName();
			if (isJSONValid(stepDetails)) {
				extentTestSteps.get().get(stepName).log(status,
						MarkupHelper.createCodeBlock(stepDetails, CodeLanguage.JSON));
			} else {
				extentTestSteps.get().get(stepName).log(status, stepDetails);
			}
		} else {
			createTestStep(defaultName);
			extentTestSteps.get().get(defaultName).log(status, stepDetails);
			// Instead of throwing this exception we can create a 'Default' step
			//throw new ReportingUtilException("No step defined");
		}
	}

	public synchronized void logAPIDetails(RequestSpecification reqSpec, Response response) {
		// create sub step for request and response
		ExtentTest apiDetails = createTestStep("API Details");
		QueryableRequestSpecification queryable = SpecificationQuerier.query(reqSpec);
		logTestStepDetails(Status.INFO, queryable.getMethod() + ":" + queryable.getURI());
		logTestStepDetails(Status.INFO, queryable.getPathParams().toString());
		logTestStepDetails(Status.INFO, queryable.getHeaders().toString());
		logTestStepDetails(Status.INFO, "Response");
		logTestStepDetails(Status.INFO, response.prettyPrint());

	}

	public synchronized String getLastTestStepName() {
		final long count = extentTestSteps.get().entrySet().stream().count();
		return extentTestSteps.get().entrySet().stream().skip(count - 1).findFirst().get().getKey();
	}

	public synchronized boolean isJSONValid(String test) {
		try {
			new JSONObject(test);
		} catch (JSONException ex) {
			try {
				new JSONArray(test);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}

	public synchronized void endReport() {
		extent.flush();
	}
}