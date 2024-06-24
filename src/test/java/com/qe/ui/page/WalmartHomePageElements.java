package com.qe.ui.page;

import org.openqa.selenium.WebElement;

import com.qe.ui.commoncore.BaseTest;
import com.qe.ui.constants.WalmartCommonTags;
import com.qe.ui.constants.WalmartHomePageConstants;
import com.qe.ui.utils.UIUtility;
import com.qe.ui.utils.XpathsUtils;

public class WalmartHomePageElements extends BaseTest {
	
	public WebElement getDeals() {
		return (getDriver().findElement(XpathsUtils.generateXPathEqualsForVisibleText(WalmartCommonTags.aTag,
				WalmartHomePageConstants.dealsText)));
	}

	public WebElement getGroceryAndEssentials() {
		return (getDriver().findElement(XpathsUtils.generateXPathEqualsForVisibleText(WalmartCommonTags.aTag,
				WalmartHomePageConstants.groceryAndEssentialsText)));
	}

	public WebElement fourthJulyPrep() {
		return (getDriver().findElement(XpathsUtils.generateXPathEqualsForVisibleText(WalmartCommonTags.aTag,
				WalmartHomePageConstants.fourthJulyPrepText)));
	}

	public WebElement prideAlways() {
		return (getDriver().findElement(XpathsUtils.generateXPathEqualsForVisibleText(WalmartCommonTags.aTag,
				WalmartHomePageConstants.prideAlwaysText)));
	}

	public WebElement fashion() {
		return (getDriver().findElement(XpathsUtils.generateXPathEqualsForVisibleText(WalmartCommonTags.aTag,
				WalmartHomePageConstants.fashionText)));
	}

	public WebElement home() {
		return (getDriver().findElement(XpathsUtils.generateXPathEqualsForVisibleText(WalmartCommonTags.aTag,
				WalmartHomePageConstants.HomeText)));
	}

	public WebElement patioAndGarden() {
		return (getDriver().findElement(XpathsUtils.generateXPathEqualsForVisibleText(WalmartCommonTags.aTag,
				WalmartHomePageConstants.patioAndGardenText)));
	}

	public WebElement electronics() {
		return (getDriver().findElement(XpathsUtils.generateXPathEqualsForVisibleText(WalmartCommonTags.aTag,
				WalmartHomePageConstants.electronicsText)));
	}

	public WebElement schoolAndOfficeSupplies() {
		return (getDriver().findElement(XpathsUtils.generateXPathEqualsForVisibleText(WalmartCommonTags.aTag,
				WalmartHomePageConstants.schoolAndOfficeSupplyText)));
	}

	public WebElement registry() {
		return (getDriver().findElement(XpathsUtils.generateXPathEqualsForVisibleText(WalmartCommonTags.aTag,
				WalmartHomePageConstants.registryText)));
	}

	public WebElement onDebit() {
		return (getDriver().findElement(XpathsUtils.generateXPathEqualsForVisibleText(WalmartCommonTags.aTag,
				WalmartHomePageConstants.oneDebitText)));
	}

	public WebElement walmartPlusWeek() {
		return (getDriver().findElement(XpathsUtils.generateXPathEqualsForVisibleText(WalmartCommonTags.aTag,
				WalmartHomePageConstants.WalmartWeekText)));
	}

}
