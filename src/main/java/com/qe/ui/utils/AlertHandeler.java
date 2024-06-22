package com.qe.ui.utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;

public class AlertHandeler {

	public static void acceptAlert(WebDriver driver) {
		Alert alert = switchToAlert(driver);
		alert.accept();
	}

	public static void dismissAlert(WebDriver driver) {
		Alert alert = switchToAlert(driver);
		alert.dismiss();
	}

	public static String getAlertText(WebDriver driver) {
		Alert alert = switchToAlert(driver);
		return alert.getText();
	}

	private static Alert switchToAlert(WebDriver driver) {
		return driver.switchTo().alert();
	}
}
