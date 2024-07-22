package com.qe.commoncore.constants;

public class EmailIntegration {
	private EmailIntegration() {
		
	}
	//send mail prop
	public static final String SEND_EMAIL_HOST="mail.smtp.host";
	public static final String SEND_EMAIL_PORT="mail.smtp.socketFactory.port";
	public static final String SEND_SMTP_PORT="mail.smtp.port";
	public static final String SEND_EMAIL_SOCKET_FACTORY="mail.smtp.socketFactory.class";
	public static final String SEND_SMTP_AUTH="mail.smtp.auth";
	public static final String SEND_PASSWORD="mail.email.password";
	public static final String SEND_EMAIL_FROM="mail.email.from";
	public static final String SEND_EMAIL_TO="mail.email.to";
	
	//read mail prop
	public static final String READ_EMAIL_HOST="mail.imap.host";
	public static final String READ_EMAIL_PORT="mail.imap.port";
	public static final String READ_STARTTLS_ENABLE="mail.imap.starttls.enable";
	public static final String READ_SSL_TRUST="mail.imap.ssl.trust";
}