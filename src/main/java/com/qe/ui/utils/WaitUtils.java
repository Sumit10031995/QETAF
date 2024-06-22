package com.qe.ui.utils;
import java.time.Duration;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.qe.ui.commoncore.BaseTest;

public class WaitUtils extends BaseTest {

private static final int waitTime = 20;


	public WebElement visibilityOf(WebElement wb) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime));
		return wait.until(ExpectedConditions.visibilityOf(wb));
	}

	public void inVisibilityOf(WebElement wb) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime));
		wait.until(ExpectedConditions.invisibilityOf(wb));
	}

	public WebElement elementToBeClickable(WebElement wb) {
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime));
		return wait.until(ExpectedConditions.elementToBeClickable(wb));

	}

}
