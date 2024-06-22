package com.qe.api.tests;

import org.json.JSONObject;
import org.testng.annotations.Test;

import com.qe.api.commoncore.BaseTest;
import com.qe.api.constants.ENVConstants;
import com.qe.apicore.impl.APIResponse;
import com.qe.apicore.impl.ApiDriver;
import com.qe.commoncore.utils.CSVUtils;

import static io.restassured.RestAssured.*;

import java.util.Arrays;
import java.util.Map;

import io.restassured.path.json.JsonPath;
import io.restassured.response.*;

public class POSTAPITesting extends BaseTest{

	private static final String postAPIEndPoint="posts";
	private static final String testDataFileName="testData.csv";
	// https://jsonplaceholder.typicode.com/posts
	@Test(groups = {"GET","API"})
	public void doPostRequest() throws Exception {
        String testDataID="1";
		Map<String, String> testData=CSVUtils.readDataFromCSVFile(testDataFileName, "ID" , Arrays.asList(testDataID)).get(0);

		reporter.createTestStep("Do POST API call and verify HTTP response code");
		ApiDriver apiDriver = new ApiDriver(configurator.getEnvironmentParameter(ENVConstants.baseURL),
				postAPIEndPoint);
		JSONObject requestBody = new JSONObject();
		requestBody.put("title", testData.get("title"));
		requestBody.put("body", testData.get("body"));
		requestBody.put("userId", Integer.parseInt(testData.get("userid")));
		apiDriver.setRequestBody(requestBody);
		APIResponse response=apiDriver.POST();
		
		assertion.assertEquals(response.getHttpStatusCode(), 201, "Validate HTTP status code", false);
		assertion.assertNotNull(response.getResposneBody(), "Response shouldn't be null", false);
		System.out.println(response.getValueFromResponseBody("id"));

		//////////////////////////////////////////////////////////////////////////////////////////////////////
		baseURI = "https://jsonplaceholder.typicode.com/";
		Response responses = given().body(requestBody).post("posts").andReturn();
		JsonPath jpath = new JsonPath(responses.getBody().asString());
		System.out.println(jpath.get("id").toString());

	}
}
