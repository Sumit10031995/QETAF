package com.qe.commoncore.constants;

public class ContextConstant {

	private ContextConstant() {
	}

	// Environment Name
	public static final String ENV_NAME = "Env_Name";

	public static final String SERVER_PROTOCOL = "http";
	public static final String SERVER_PROTOCOL_SLASHES = "://";
	public static final String DATE_FORMAT = "dateFormat";
	public static final String TIMEOUT = "timeout";
	public static final String DEFAULT_UI_DATE_FORMAT = "yyyy-MM-dd";

	public static final String LOG_LEVEL = "logInfoLevel";
	
	// reporting
	public static final String REPORT_LOCATION = "Report_Loc";
	public static final String LOG_LOCATION = "Log_Loc";

    // retry mechanism
	public static final String INCLUDE_RETRY_REPORT = "includeRetryReport";
	public static final String DEEP_REPORTING = "DEEP_REPORTING";

	//API TIMEOUT
	public static final String API_TIMEOUT_MS = "API_TIMEOUT_MS";

	//max retry count
	public static final String MAX_RETRY_COUNT = "MAX_RETRY_COUNT";

	//retry failed case
	public static final String RETRY_FAILED_TESTS = "RETRY_FAILED_TESTS";

	//retry failed case
	public static final String REPORT_RETRY_TESTS = "REPORT_RETRY_TESTS";
	
	//jira components
	public static final String JIRA_COMPONENTS = "JIRA_COMPONENTS";
	
	//trigger mail
	public static final String TRIGGER_MAIL = "TRIGGER_MAIL";
	
	//mail send to
	public static final String MAIL_TO = "MAIL_TO";
	
	//browser send to
	public static final String BROWSER = "BROWSER";

}