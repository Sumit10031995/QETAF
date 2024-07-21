package com.qe.ui.utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;

public abstract class AlertHandeler {

	private Alert alert;
	private final WebDriver driver;


	// immetuable object
	AlertHandeler(WebDriver driver) {
        this.driver= driver;
	}

	public void acceptAlert(WebDriver driver) {
		this.alert = driver.switchTo().alert();
		alert.accept();
	}

	public void dismissAlert(WebDriver driver) {
		this.alert = driver.switchTo().alert();
		alert.dismiss();
	}

	public String getAlertText(WebDriver driver) {
		this.alert = driver.switchTo().alert();
		return alert.getText();
	}

	public void sendKeysToAlert(String keysToSend) {
		this.alert = driver.switchTo().alert();
		alert.sendKeys(keysToSend);
	}

}
