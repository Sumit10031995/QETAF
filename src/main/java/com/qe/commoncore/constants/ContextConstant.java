package com.qe.commoncore.constants;

public class ContextConstant {

	private ContextConstant() {
	}

	// Environment Name
	public static final String ENV_NAME = "Env_Name";

	// API Hosts
//	public static final String RequisitionService_API_Host = "RequisitionService_API_Host";
//	public static final String QALService_API_Host = "QALService_API_Host";
//	public static final String DB2_dbUrl = "DB2_dbUrl";
//	public static final String DB2_dbUserName = "DB2_dbUserName";
//	public static final String DB2_dbGrantKey = "DB2_dbGrantkey";

	//	public static final String DB2_dbUserName = "DB2_dbUserName";
	public static final String DB2_dbPassword = "DB2_dbPassword";

	public static final String SERVER_PROTOCOL = "http";
	public static final String SERVER_PROTOCOL_SLASHES = "://";
	public static final String DATE_FORMAT = "dateFormat";
	public static final String TIMEOUT = "timeout";
	public static final String DEFAULT_UI_DATE_FORMAT = "yyyy-MM-dd";

	public static final String LOG_LEVEL = "logInfoLevel";
	public static final String PUBLISH_RESULTS_TO_XRAY = "Publish_Results_To_XRAY";

	// reporting
	public static final String REPORT_LOCATION = "Report_Loc";
	public static final String LOG_LOCATION = "Log_Loc";

	// Run mode if Develop then extent report will be overwritten every time. Useful
	// while developing tests
	public static final String RUN_MODE = "runMode";

	// For future use
	public static final String INCLUDE_RETRY_REPORT = "includeRetryReport";
	public static final String DEEP_REPORTING = "DEEP_REPORTING";

	// XRAY Integration
	public static final String JIRA_TESTEXECUTION_KEY = "JIRA_TestExecution_Key";
	public static final String JIRA_TESTPLAN_KEY = "JIRA_TestPlan_Key";
	public static final String JIRA_USERNAME = "JIRA_UserName";

	// HC Constants
//	public static final String HiringCenter_API_Host = "HiringCnter_API_Host";

	//URLs
	public static final String SECURITY_SVC_API_HOST="https://recruitment-securityservices.rpservices.stage.k8s.walmart.net";

	//endpoints
	public static final String VAULT_SECRETS_ENDPOINT = "/getVaultData";

	//endpoints
	public static final String VAULT_SECRETS = "Vault_Secrets";

	//API TIMEOUT
	public static final String API_TIMEOUT_MS = "API_TIMEOUT_MS";

	//XL Report
	public static final String CREATE_FAILURE_XLREPORT = "CREATE_FAILURE_XLREPORT";

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

}