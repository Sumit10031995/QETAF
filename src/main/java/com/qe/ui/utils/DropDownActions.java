package com.qe.ui.utils;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class DropDownActions {

	private final Select dropdown;

	// immetuable object
	public DropDownActions(WebElement element) {
		this.dropdown = new Select(element);
	}

	public void selectByVisibleText(WebElement dropdownElement, String visibleText) {
		dropdown.selectByVisibleText(visibleText);
	}

	public void selectByValue(WebElement dropdownElement, String value) {
		dropdown.selectByValue(value);
	}

	public void selectByIndex(WebElement dropdownElement, int index) {
		dropdown.selectByIndex(index);
	}

	public WebElement getFirstSelectedOption(WebElement dropdownElement) {
		return dropdown.getFirstSelectedOption();
	}

	public List<WebElement> getAllSelectedOption(WebElement dropdownElement) {
		return dropdown.getAllSelectedOptions();
	}

	public boolean isSelected(WebElement dropdownElement) {
		return dropdownElement.isSelected();
	}

	public boolean isMultiSelect(WebElement dropdownElement) {
		return dropdown.isMultiple();
	}
}
