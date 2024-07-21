package com.qe.ui.utils;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class DropDownActions {
	
	public void selectByVisibleText(WebElement dropdownElement, String visibleText) {
		Select dropdown= new Select(dropdownElement);
		dropdown.selectByVisibleText(visibleText);
	}

	public void selectByValue(WebElement dropdownElement, String value) {
		Select dropdown= new Select(dropdownElement);
		dropdown.selectByValue(value);
	}

	public void selectByIndex(WebElement dropdownElement, int index) {
		Select dropdown= new Select(dropdownElement);
		dropdown.selectByIndex(index);
	}

	public WebElement getFirstSelectedOption(WebElement dropdownElement) {
		Select dropdown= new Select(dropdownElement);
		return dropdown.getFirstSelectedOption();
	}

	public List<WebElement> getAllSelectedOption(WebElement dropdownElement) {
		Select dropdown= new Select(dropdownElement);
		return dropdown.getAllSelectedOptions();
	}

	public boolean isMultiSelect(WebElement dropdownElement) {
		Select dropdown= new Select(dropdownElement);
		return dropdown.isMultiple();
	}
}
