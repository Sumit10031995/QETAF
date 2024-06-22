package com.qe.ui.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class MouseActions {

	public void moveToElement(WebDriver driver, WebElement element) {
		Actions actions = new Actions(driver);
		actions.moveToElement(element).perform();
	}

	public void click(WebDriver driver, WebElement element) {
		Actions actions = new Actions(driver);
		actions.click(element).perform();
	}

	public void doubleClick(WebDriver driver, WebElement element) {
		Actions actions = new Actions(driver);
		actions.doubleClick(element).perform();
	}

	public void contextClick(WebDriver driver, WebElement element) {
		Actions actions = new Actions(driver);
		actions.contextClick(element).perform();
	}

	public void dragAndDrop(WebDriver driver, WebElement sourceElement, WebElement targetElement) {
		Actions actions = new Actions(driver);
		actions.dragAndDrop(sourceElement, targetElement).perform();
	}
}
