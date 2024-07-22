package com.qe.commoncore.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qe.commoncore.constants.EmailIntegration;


//Future use
public class EmailConfig {
	private static final Logger log = LoggerFactory.getLogger(EmailConfig.class);
	private Properties properties;
	private static EmailConfig confif;

	/**
	 * Singleton
	 */

	/**
	 * Private constructor
	 */
	private EmailConfig() {
		properties = new Properties();
		try (InputStream input = getClass().getClassLoader().getResourceAsStream("emailConfig.properties")) {
			properties.load(input);
		} catch (IOException io) {
			log.error("Error occurred while reading 'emailConfig.properties' properties file: {} ", io.getMessage());
		}
	}

	/**
	 * initializes EmailConfig object if it was not created earlier
	 */
	public static synchronized EmailConfig getInstance() {
		if (confif == null) {
			confif = new EmailConfig();
		}
		return confif;
	}

	public String getHost() {
		return properties.getProperty(EmailIntegration.EMAIL_HOST);
	}

	public Integer getPort() {
		return Integer.parseInt(properties.getProperty(EmailIntegration.EMAIL_PORT));
	}

	public String getFROMDetails() {
		return properties.getProperty(EmailIntegration.EMAIL_FROM);
	}

	public List<String> getTODetails() {
		return Arrays.asList(properties.getProperty(EmailIntegration.EMAIL_TO).split(","));
	}
	
	public String getPassword() {
		return properties.getProperty(EmailIntegration.PASSWORD);
	}
}
