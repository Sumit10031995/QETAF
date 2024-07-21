package com.qe.ui.utils;

import org.openqa.selenium.WebDriver;


public class UIUtility {
	public WebDriver driver;
	public XpathUtils xpath;
	public KeyboardActions keyboardAction;
	public MouseActions mouseActions;
	public DropDownActions dropdownActions;
	public AlertHandeler alertHandler;
	public WaitUtils wait;
	
	public UIUtility() {
		
	}
	
	public void initUtrility(WebDriver driver) {
        this.driver= driver;
		this.xpath = new XpathUtils();
		this.dropdownActions=new DropDownActions();
        this.keyboardAction=new KeyboardActions() {};
        this.mouseActions=new MouseActions(driver) {};
        this.alertHandler=new AlertHandeler(driver) {};
        this.wait=new WaitUtils(driver) {};
	}

}
