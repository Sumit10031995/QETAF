package com.qe.commoncore.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qe.commoncore.constants.XrayIntegration;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

public class XrayConfig {
    private static final Logger log =
            LoggerFactory.getLogger(XrayConfig.class);

    private Properties properties;

    public XrayConfig() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("xray.properties")) {
            properties.load(input);
        } catch (IOException io) {
            log.error("Error occurred while reading rtaf properties file: {} ", io.getMessage());
        }
    }

    public String getUserName() {
        return properties.getProperty(XrayIntegration.XRAY_SUUSERNAME);
    }

    public String getXrayPass() {
        return properties.getProperty(XrayIntegration.XRAY_GRANTKEY);
    }

    public String getXRAYBaseURL() {
        return properties.getProperty(XrayIntegration.XRAY_BASE_URL);
    }

    public String getXRAYUpdatePathURI() {
        return properties.getProperty(XrayIntegration.XRAY_TEST_PLAN_IMPORT_URL);
    }

    public String getTestExecutionUpdateURI(){ return properties.getProperty(XrayIntegration.XRAY_TEST_EXECUTION_IMPORT_URL); }

    public String getXrayGetURI(String testPlan) {
        return MessageFormat.format(properties.getProperty(XrayIntegration.XRAY_TEST_PLAN_GET_URL), testPlan);
    }

    public String getXRAYUpdatePathURL() {
        return properties.getProperty(XrayIntegration.XRAY_BASE_URL) + properties.getProperty(XrayIntegration.XRAY_TEST_PLAN_IMPORT_URL);
    }

    public String getXRAYGetURl(String testPlan) {
        return properties.getProperty(XrayIntegration.XRAY_BASE_URL) + MessageFormat.format(properties.getProperty(XrayIntegration.XRAY_TEST_PLAN_GET_URL), testPlan);
    }
    public String getJiraID() {
        return properties.getProperty(XrayIntegration.JIRA_ID);
    }
    public String getJiraProjectKey() {
        return properties.getProperty(XrayIntegration.JIRA_PROJECT_KEY);
    }

}
