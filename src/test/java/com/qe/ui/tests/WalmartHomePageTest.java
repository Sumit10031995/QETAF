package com.qe.ui.tests;

import org.testng.annotations.Test;

import com.qe.ui.constants.WalmartHomePageConstants;
import com.qe.ui.page.WalmartHomePageElements;
import com.qe.ui.utils.UIUtility;
import com.qe.ui.utils.WaitUtils;
import com.qe.ui.utils.XpathsUtils;

public class WalmartHomePageTest extends WalmartHomePageElements {
	
	@Test(groups = { "HomePage", "UI" })
	public void homePageValidation() throws Exception {		
		uiUtility.visibilityOf(XpathsUtils.generateXPathEqualsForVisibleText(WalmartHomePageConstants.dealsText,
				WalmartHomePageConstants.dealsText));
		reporter.createTestStep("Validate Walmart Home Page Elements");
		assertion.assertEquals(getDeals().getText(), WalmartHomePageConstants.dealsText,
				"Validate Text '" + WalmartHomePageConstants.dealsText + "'", true);
		
		assertion.assertEquals(getGroceryAndEssentials().getText(), WalmartHomePageConstants.groceryAndEssentialsText,
				"Validate Text '" + WalmartHomePageConstants.groceryAndEssentialsText + "'", true);
		
		assertion.assertEquals(fourthJulyPrep().getText(), WalmartHomePageConstants.fourthJulyPrepText,
				"Validate Text '" + WalmartHomePageConstants.fourthJulyPrepText + "'", true);
		
		assertion.assertEquals(prideAlways().getText(), WalmartHomePageConstants.prideAlwaysText,
				"Validate Text '" + WalmartHomePageConstants.prideAlwaysText + "'", true);
		
		assertion.assertEquals(fashion().getText(), WalmartHomePageConstants.fashionText,
				"Validate Text '" + WalmartHomePageConstants.fashionText + "'", true);
		
		assertion.assertEquals(home().getText(), WalmartHomePageConstants.HomeText,
				"Validate Text '" + WalmartHomePageConstants.HomeText + "'", true);
		
		assertion.assertEquals(patioAndGarden().getText(), WalmartHomePageConstants.patioAndGardenText,
				"Validate Text '" + WalmartHomePageConstants.patioAndGardenText + "'", true);
		
		assertion.assertEquals(electronics().getText(), WalmartHomePageConstants.electronicsText,
				"Validate Text '" + WalmartHomePageConstants.electronicsText + "'", true);
		
		assertion.assertEquals(schoolAndOfficeSupplies().getText(), WalmartHomePageConstants.schoolAndOfficeSupplyText,
				"Validate Text '" + WalmartHomePageConstants.schoolAndOfficeSupplyText + "'", true);
		
		assertion.assertEquals(registry().getText(), WalmartHomePageConstants.registryText,
				"Validate Text '" + WalmartHomePageConstants.registryText + "'", true);
		
		assertion.assertEquals(onDebit().getText(), WalmartHomePageConstants.oneDebitText,
				"Validate Text '" + WalmartHomePageConstants.oneDebitText + "'", true);
		
		assertion.assertEquals(walmartPlusWeek().getText(), WalmartHomePageConstants.WalmartWeekText,
				"Validate Text '" + WalmartHomePageConstants.WalmartWeekText + "'", true);
		
		assertion.assertAll();
	}
}
