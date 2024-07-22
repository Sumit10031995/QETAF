package com.qe.commoncore.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qe.commoncore.constants.EmailIntegration;

//Future use
public class EmailConfig {
	private static final Logger log = LoggerFactory.getLogger(EmailConfig.class);
	private static Properties properties;
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

	public String getSendMailHost() {
		return properties.getProperty(EmailIntegration.SEND_EMAIL_HOST);
	}

	public Integer getSendMailPort() {
		return Integer.parseInt(properties.getProperty(EmailIntegration.SEND_EMAIL_PORT));
	}
	
	public String getReadMailHost() {
		return properties.getProperty(EmailIntegration.READ_EMAIL_HOST);
	}

	public Integer getReadMailPort() {
		return Integer.parseInt(properties.getProperty(EmailIntegration.READ_EMAIL_PORT));
	}

	public String getFROMDetails() {
		return properties.getProperty(EmailIntegration.SEND_EMAIL_FROM);
	}

	public List<String> getTODetails() {
		return Arrays.asList(properties.getProperty(EmailIntegration.SEND_EMAIL_TO).split(","));
	}

	public String getPassword() {
		return properties.getProperty(EmailIntegration.SEND_PASSWORD);
	}

	public String getEMAILSocketFactory() {
		return properties.getProperty(EmailIntegration.SEND_EMAIL_SOCKET_FACTORY);
	}

	public String getSMTPAuth() {
		return properties.getProperty(EmailIntegration.SEND_SMTP_AUTH);
	}
	
	public Integer getSMTPPort() {
		return Integer.parseInt(properties.getProperty(EmailIntegration.SEND_SMTP_PORT));
	}
	
	public String getStarttls() {
		return properties.getProperty(EmailIntegration.READ_STARTTLS_ENABLE);
	}
	
	public String getSSLTrust() {
		return properties.getProperty(EmailIntegration.READ_SSL_TRUST);
	}

	public Session getSendMailSession() {
		Properties props = new Properties();
		props.put(EmailIntegration.SEND_EMAIL_HOST, getSendMailHost());
		props.put(EmailIntegration.SEND_EMAIL_PORT, getSendMailPort());
		props.put(EmailIntegration.SEND_EMAIL_SOCKET_FACTORY, getEMAILSocketFactory());
		props.put(EmailIntegration.SEND_SMTP_AUTH, getSMTPAuth());
		props.put(EmailIntegration.SEND_SMTP_PORT, getSMTPPort());

		return Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(getFROMDetails(), getPassword());
			}
		});
	}
	
	public Session getReadMailSession() {
		Properties props = new Properties();
		props.put(EmailIntegration.READ_EMAIL_HOST, getReadMailHost());
		props.put(EmailIntegration.READ_EMAIL_PORT, getReadMailPort());
		props.put(EmailIntegration.READ_STARTTLS_ENABLE, getStarttls());
		props.put(EmailIntegration.READ_SSL_TRUST, getSSLTrust());

		return Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(getFROMDetails(), getPassword());
			}
		});
	}
}
