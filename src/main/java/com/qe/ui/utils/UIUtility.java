package com.qe.ui.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class UIUtility extends AlertHandeler {

	private final Actions actions;
	private final WebDriver driver;

	// immetuable object
	public UIUtility(WebDriver driver) {
		super(driver);
		this.actions = new Actions(driver);
		this.driver=driver;
	}

	@Override
	public void moveToElement(WebElement element) {
		actions.moveToElement(element).perform();
	}

	@Override
	public void click(WebElement element) {
		actions.click(element).perform();
	}

	@Override
	public void doubleClick(WebElement element) {
		actions.doubleClick(element).perform();
	}

	@Override
	public void contextClick(WebElement element) {
		actions.contextClick(element).perform();
	}

	@Override
	public void clickAndHold(WebElement element) {
		actions.clickAndHold(element).perform();
	}

	@Override
	public void dragAndDrop(WebElement sourceElement, WebElement targetElement) {
		actions.dragAndDrop(sourceElement, targetElement).perform();
	}
}
