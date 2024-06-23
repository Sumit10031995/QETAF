package com.qe.ui.utils;

import org.openqa.selenium.By;

public class XpathsUtils {
	
	public static By generateXPathEqualsForVisibleText(String tagName, String searchText) {
		String xpath = "//" + tagName + "[text()='" + searchText + "']";
		System.out.println(xpath);
		return By.xpath(xpath);
	}

	public static By generateXPathContainsForVisibleText(String tagName, String searchText) {
		String xpath = "//" + tagName + "[contains(text(), '" + searchText + "')]";
		System.out.println(xpath);
		return By.xpath(xpath);
	}

	public static By generateXPathStartsWithForVisibleText(String tagName, String searchText) {
		String xpath = "//" + tagName + "[starts-with(text(), '" + searchText + "')]";
		System.out.println(xpath);
		return By.xpath(xpath);
	}

	public static By generateXPathEqualsForAttribute(String tagName, String attributeName, String searchText) {
		String xpath = "//" + tagName + "[@" + attributeName + "='" + searchText + "']";
		System.out.println(xpath);
		return By.xpath(xpath);
	}

	public static By generateXPathContainsForAttribute(String tagName, String attribute, String searchText) {
		String xpath = "//" + tagName + "[contains(@" + attribute + ", '" + searchText + "')]";
		System.out.println(xpath);
		return By.xpath(xpath);
	}

	public static By generateXPathStartsWithForAttribute(String tagName, String attribute, String searchText) {
		String xpath = "//" + tagName + "[starts-with(@" + attribute + ", '" + searchText + "')]";
		System.out.println(xpath);
		return By.xpath(xpath);
	}

}
