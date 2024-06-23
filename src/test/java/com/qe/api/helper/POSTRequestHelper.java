package com.qe.api.helper;

import java.util.Arrays;
import java.util.Map;

import org.json.JSONObject;

import com.qe.api.commoncore.BaseTest;
import com.qe.api.constants.ENVConstants;
import com.qe.apicore.impl.APIResponse;
import com.qe.apicore.impl.ApiDriver;
import com.qe.commoncore.utils.CSVUtils;

public class POSTRequestHelper extends BaseTest{
	//End point
	private static final String postAPIEndPoint="posts";
	//Test data File path
	private static final String testDataFileName="testData.csv";
	

	/**
	 * 
	 * @param testID
	 * @return
	 * @throws Exception
	 * do POST request
	 */
	public APIResponse doPOSTRequest(String testID) throws Exception {
		ApiDriver apiDriver = new ApiDriver(configurator.getEnvironmentParameter(ENVConstants.baseURL),
				postAPIEndPoint);
		apiDriver.setRequestBody(getPOSTRequestBody(testID));
		return apiDriver.POST();
	}
	
	/**
	 * 
	 * @param testID
	 * @return
	 * @throws Exception
	 * create request body
	 */
	private JSONObject getPOSTRequestBody(String testID) throws Exception {
		Map<String, String> testData=CSVUtils.readDataFromCSVFile(testDataFileName, "ID" , Arrays.asList(testID)).get(0);
		JSONObject requestBody = new JSONObject();
		requestBody.put("title", testData.get("title"));
		requestBody.put("body", testData.get("body"));
		requestBody.put("userId", Integer.parseInt(testData.get("userid")));
		return requestBody;
	}
}
