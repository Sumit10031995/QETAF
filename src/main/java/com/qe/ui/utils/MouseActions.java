package com.qe.ui.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public abstract class MouseActions {

	private final Actions actions;
	private final WebDriver driver;

	// immetuable object
	public MouseActions(WebDriver driver) {
		this.actions = new Actions(driver);
		this.driver=driver;
	}

	public void moveToElement(WebElement element) {
		actions.moveToElement(element).perform();
	}

	public void click(WebElement element) {
		actions.click(element).perform();
	}

	public void doubleClick(WebElement element) {
		actions.doubleClick(element).perform();
	}

	public void contextClick(WebElement element) {
		actions.contextClick(element).perform();
	}

	public void clickAndHold(WebElement element) {
		actions.clickAndHold(element).perform();
	}

	public void dragAndDrop(WebElement sourceElement, WebElement targetElement) {
		actions.dragAndDrop(sourceElement, targetElement).perform();
	}
	


}
