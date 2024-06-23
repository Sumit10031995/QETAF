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


public class WaitUtils {
    private static ReportingUtil reporter = ReportingUtil.getInstance();
    private static final int waitTime = 20;
    

    public static WebElement visibilityOf(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return element;
        } catch (TimeoutException e) {
        	reporter.logTestStepDetails(Status.FAIL,"Element not visible within the wait time: " + waitTime + " seconds.");
            throw e;
        }
    }

    public static void inVisibilityOf(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
        	reporter.logTestStepDetails(Status.FAIL,"Element not invisible within the wait time: " + waitTime + " seconds.");
            throw e;
        }
    }

    public static WebElement elementToBeClickable(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            return element;
        } catch (TimeoutException e) {
        	reporter.logTestStepDetails(Status.FAIL,"Element not clickable within the wait time: " + waitTime + " seconds.");
            throw e;
        }
    }

}
