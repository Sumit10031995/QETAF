package com.qe.ui.utils;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.aventstack.extentreports.Status;
import com.qe.commoncore.utils.ReportingUtil;

public class WaitUtils{
	private static final ReportingUtil reporter = ReportingUtil.getInstance();
	private static final int waitTime = 20;
	private final WebDriverWait wait;
	private final WebDriver driver;

	// immetuable object
	public WaitUtils(WebDriver driver) {
		this.driver=driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
	}

	public WebElement visibilityOf(By locator) {
		try {
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
			return element;
		} catch (TimeoutException e) {
			reporter.logTestStepDetails(Status.FAIL,
					"Element not visible within the wait time: " + waitTime + " seconds.");
			throw e;
		}
	}

	public void inVisibilityOf(By locator) {
		try {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
		} catch (TimeoutException e) {
			reporter.logTestStepDetails(Status.FAIL,
					"Element not invisible within the wait time: " + waitTime + " seconds.");
			throw e;
		}
	}

	public WebElement elementToBeClickable(By locator) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
		try {
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
			return element;
		} catch (TimeoutException e) {
			reporter.logTestStepDetails(Status.FAIL,
					"Element not clickable within the wait time: " + waitTime + " seconds.");
			throw e;
		}
	}
}
