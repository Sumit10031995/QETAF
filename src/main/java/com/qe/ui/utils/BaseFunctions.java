package com.qe.ui.utils;

import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public interface BaseFunctions {

	/**
	 * 
	 * @param locator
	 * @return This method use to wait until element to be visible on web-page
	 */
	public WebElement visibilityOf(By locator);

	/**
	 * 
	 * @param locator
	 * @param locator This method use to wait until element to be in-visible on
	 * web-page
	 */
	public void inVisibilityOf(By locator);

	/**
	 * 
	 * @param locator
	 * @return This method use to wait until element to be clickable on web-page
	 */
	public WebElement elementToBeClickable(By locator);

	/**
	 * 
	 * @param driver
	 * @param element This method use to move to a specific element on web-page
	 */
	public void moveToElement( WebElement element);

	/**
	 * 
	 * @param driver
	 * @param element This method use to click on specific element on web-page
	 */
	public void click( WebElement element);

	/**
	 * 
	 * @param driver
	 * @param element 
	 * This method use to double click on specific element on
	 * web-page
	 */
	public void doubleClick( WebElement element);

	/**
	 * 
	 * @param driver
	 * @param element 
	 * This method use to right click on specific element on web-page
	 */
	public void contextClick( WebElement element);

	/**
	 * 
	 * @param driver
	 * @param element This method use to do click and hold action on web-page
	 */
	public void clickAndHold( WebElement element);

	/**
	 * 
	 * @param driver
	 * @param sourceElement
	 * @param targetElement 
	 * This method use to drag and drop a specific element on
	 * web-page
	 */
	public void dragAndDrop( WebElement sourceElement, WebElement targetElement);
	
	/**
	 * @param driver
	 * This method use to do accept an action on alert window
	 */
	public void acceptAlert(WebDriver driver);
	
	/**
	 * @param driver
	 * This method use to do reject an action on alert window
	 */
	public void dismissAlert(WebDriver driver);
	
	/**
	 * @param driver
	 * This method use to do fetch alert text
	 */
	public String getAlertText(WebDriver driver);
	
	/**
	 * @param driver
	 * This method use to do send keys to alert
	 */
	public void sendKeysToAlert(String keysToSend);

}
