package com.qe.ui.utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;

public abstract class AlertHandeler extends WaitUtils {

	private Alert alert;
	private final WebDriver driver;


	// immetuable object
	AlertHandeler(WebDriver driver) {
		super(driver);
        this.driver= driver;
	}

	@Override
	public void acceptAlert(WebDriver driver) {
		this.alert = driver.switchTo().alert();
		alert.accept();
	}

	@Override
	public void dismissAlert(WebDriver driver) {
		this.alert = driver.switchTo().alert();
		alert.dismiss();
	}

	@Override
	public String getAlertText(WebDriver driver) {
		this.alert = driver.switchTo().alert();
		return alert.getText();
	}

	@Override
	public void sendKeysToAlert(String keysToSend) {
		this.alert = driver.switchTo().alert();
		alert.sendKeys(keysToSend);
	}

}
