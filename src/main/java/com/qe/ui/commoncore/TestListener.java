package com.qe.ui.commoncore;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.Status;

public class TestListener implements ITestListener {

	private static final String screenshotsFilePath = "test-report/screenshots/" + System.currentTimeMillis() + ".png";

	@Override
	public void onTestFailure(ITestResult result) {
		try {
			File srcFile = ((TakesScreenshot) BaseTest.getDriver()).getScreenshotAs(OutputType.FILE);
			File destFile = new File(screenshotsFilePath);
			FileUtils.copyFile(srcFile, destFile);
			BaseTest.reporter.extentTest.get().addScreenCaptureFromPath(destFile.getAbsolutePath());
			AssertionError assertionError = new AssertionError(result.getThrowable().getMessage(),result.getThrowable());
			result.setThrowable(assertionError);
			BaseTest.reporter.logTestStepDetails(Status.FAIL, result.getThrowable().getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
