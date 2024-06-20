package com.qe.commoncore;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

import com.qe.commoncore.constants.ContextConstant;
import com.qe.commoncore.utils.FileUtil;


public class Configurator {
	private static Configurator instance;
	private static final String TIME_STAMP = new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date());
	private Map<String, String> parameterMap;
	private Map<String, String> testEnvironmentMap;
	private Map<Long, ExecutionContext> executionContextMap;
	// private static final Logger logger = Logger.getLogger(Configurator.class);

	/**
	 * Constructor for Configurator
	 *
	 */
	private Configurator() {
		try {
			parameterMap = new HashMap<String, String>();
			testEnvironmentMap = new HashMap<String, String>();
			executionContextMap = new Hashtable<Long, ExecutionContext>();
		} catch (Exception e) {
			String message = "Configurator Create Failed";
			System.out.println(message);
		}
		System.out.println("Configurator created successfully");
	}

	/**
	 * Creates an instance of Configurator
	 *
	 * @return instance: Configurator
	 */
	public static synchronized Configurator getInstance() {
		if (instance == null) {
			instance = new Configurator();
			System.out.println("Configurator object initialized");
		}
		return instance;
	}

	/**
	 * Initializes global parameterMap using testng context.
	 *
	 * @param params:Map
	 * @throws Throwable
	 */
	public void initializeParameters(Map<String, String> params) throws Throwable {
		try {
			setOptionalParameter(params);
			setEnvironmentParameters(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a parameter value based on key
	 *
	 * @param key:String
	 * @return value of key: String
	 */
	public String getParameter(String key) {
		if (parameterMap.containsKey(key))
			return parameterMap.get(key);
		else
			return "";
	}

	/**
	 * Get environment parameter value based on the key.
	 *
	 * @param key: key in map
	 * @return String : value of key
	 */
	public String getEnvironmentParameter(String key) {
		return testEnvironmentMap.containsKey(key) ? testEnvironmentMap.get(key) : null;
	}

	public Map<Long, ExecutionContext> getExecutionContextMap() {
		return executionContextMap;
	}

	/**
	 * This function should be called at the beginning of the session execution to
	 * set some optional parameters
	 *
	 * @author a0b0lgm
	 * @param params : Map
	 */
	private void setOptionalParameter(Map<String, String> params) {

		// Env name, default is stage
		parameterMap.put(ContextConstant.ENV_NAME,
				params.containsKey(ContextConstant.ENV_NAME) ? params.get(ContextConstant.ENV_NAME).toLowerCase()
						: "stage");

		parameterMap.put(ContextConstant.DATE_FORMAT,
				params.containsKey(ContextConstant.DATE_FORMAT) ? params.get(ContextConstant.DATE_FORMAT) : "");
		parameterMap.put(ContextConstant.TIMEOUT,
				params.containsKey(ContextConstant.TIMEOUT) ? params.get(ContextConstant.TIMEOUT) : "30");
		parameterMap.put(ContextConstant.DEEP_REPORTING,
				params.containsKey(ContextConstant.DEEP_REPORTING) ? params.get(ContextConstant.DEEP_REPORTING) : "false");
		parameterMap.put(ContextConstant.LOG_LEVEL,
				params.containsKey(ContextConstant.LOG_LEVEL) ? params.get(ContextConstant.LOG_LEVEL) : "True");
		// add api timeout in mili second
		parameterMap.put(ContextConstant.API_TIMEOUT_MS,
				params.containsKey(ContextConstant.API_TIMEOUT_MS) ? params.get(ContextConstant.API_TIMEOUT_MS)
						: "180000");
		// add xl report
		parameterMap.put(ContextConstant.CREATE_FAILURE_XLREPORT,
				params.containsKey(ContextConstant.CREATE_FAILURE_XLREPORT) ? params.get(ContextConstant.CREATE_FAILURE_XLREPORT)
						: "false");

		if(params.containsKey(ContextConstant.MAX_RETRY_COUNT) && params.containsKey(ContextConstant.RETRY_FAILED_TESTS)) {
			// set is retry field
			parameterMap.put(ContextConstant.MAX_RETRY_COUNT,retryInputAnalyser(params.get(ContextConstant.MAX_RETRY_COUNT)));
			// add max retry count
			parameterMap.put(ContextConstant.RETRY_FAILED_TESTS, params.get(ContextConstant.RETRY_FAILED_TESTS));
		}else {
			parameterMap.put(ContextConstant.MAX_RETRY_COUNT,"0");
			parameterMap.put(ContextConstant.RETRY_FAILED_TESTS, "false");
		}
		
		// log REPORT_RETRY_TESTS tests to extent report
			parameterMap.put(ContextConstant.REPORT_RETRY_TESTS,
						params.containsKey(ContextConstant.REPORT_RETRY_TESTS) ? params.get(ContextConstant.REPORT_RETRY_TESTS)
								: "false");
		// jira components
			parameterMap.put(ContextConstant.JIRA_COMPONENTS,
						params.containsKey(ContextConstant.JIRA_COMPONENTS) ? params.get(ContextConstant.JIRA_COMPONENTS)
											: "WMUS CP 1,WMUS CP 2");	
			
		// trigger mail
			parameterMap.put(ContextConstant.TRIGGER_MAIL,
						params.containsKey(ContextConstant.TRIGGER_MAIL) ? params.get(ContextConstant.TRIGGER_MAIL)
										: "false");
		// mail to
		String mailIDs=verifyMailIDs((params.get(ContextConstant.MAIL_TO)==null)?null:params.get(ContextConstant.MAIL_TO));
		parameterMap.put(ContextConstant.MAIL_TO,
				(mailIDs!=null)? 
						(mailIDs.replaceAll("\\s", "").equals("") ? null : mailIDs)
						: null);
			
			
		// Add rest of the custom parameters in map
		for (Entry<String, String> e : params.entrySet()) {
			if (!parameterMap.containsKey(e.getKey())) {
				parameterMap.put(e.getKey().toString(), e.getValue().toString());
			}
		}
	}

	/**
	 * This function should be called at the beginning of the session execution to
	 * set up the environment parameter.
	 *
	 * @param params : Map
	 * @throws Throwable
	 */
	public void setEnvironmentParameters(Map<String, String> params) throws Throwable {

		String envFileName = parameterMap.get(ContextConstant.ENV_NAME).trim().toLowerCase() + "_env.properties";

		Properties properties = new Properties();
		properties.load(new FileInputStream(FileUtil.getFile("env/" + envFileName)));

		testEnvironmentMap.putAll(properties.entrySet()
				.stream()
				.collect(Collectors.toMap(e -> e.getKey().toString(),
						e -> e.getValue().toString())));
	}

	public static String retryInputAnalyser(String integer) {
		int retryCount = 1;
		int maxRetryCount = 3;
		if (integer.matches("\\d+")) {
			try {
				if (Integer.parseInt(integer) <= 0) {
					throw new Exception("Invalid Input");
				}else if (Integer.parseInt(integer) <= maxRetryCount) {
					return String.valueOf(integer);
				} else {
					retryCount = maxRetryCount;
					System.out.println(
							"Limiting Retries To '" + maxRetryCount + "' Due To Exceeding The Maximum Retry Count");
				}
			} catch (Exception e) {
				retryCount = retryCount;
				System.out.println("Setting 'MAX_RETRY_COUNT' to " + retryCount
						+ ", as values other than integer in the range of  1 to 3 are unexpected inputs");
			}
		} else {
			retryCount = retryCount;
			System.out.println("Setting 'MAX_RETRY_COUNT' to " + retryCount
					+ ", as values other than integer in the range of  1 to 3 are unexpected inputs");
		}
		return String.valueOf(retryCount);
	}
	
	public List<String> getJiraComponents() {
		return Arrays.asList(parameterMap.get(ContextConstant.JIRA_COMPONENTS).split(","));
	}

	
	private String verifyMailIDs(String mails) {
  	    List<String> validMailIDs= new ArrayList<>();
		List<String> invalidMailIDs = new ArrayList<>();
      if(mails!=null) {
  		List<String> mailIDs = Arrays.asList(mails.split(","));
		for (String mailID : mailIDs) {
			if (!mailID.toLowerCase().endsWith("@malinator.com")) {
				invalidMailIDs.add(mailID);
			}else {
				validMailIDs.add(mailID);
			}
		}
		if (invalidMailIDs.size() > 0)
			System.out.println("Invalid mail IDs:-" + invalidMailIDs);
      }
		return String.join(",", validMailIDs);
	}
}