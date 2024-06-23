package com.qe.api.tests;

import org.testng.annotations.Test;

import com.qe.api.commoncore.BaseTest;
import com.qe.api.helper.POSTRequestHelper;
import com.qe.apicore.impl.APIResponse;

public class POSTAPITesting extends BaseTest{
	POSTRequestHelper postRequestHelper=new POSTRequestHelper();
	
	// https://jsonplaceholder.typicode.com/posts
	@Test(groups = {"GET","API"})
	public void doPostRequest() throws Exception {
        String testDataID="1";

		reporter.createTestStep("Do POST API call and verify HTTP response code");
		APIResponse response = postRequestHelper.doPOSTRequest(testDataID);
		assertion.assertEquals(response.getHttpStatusCode(), 201, "Validate HTTP status code", false);
		assertion.assertNotNull(response.getResposneBody(), "Response shouldn't be null", false);
		System.out.println(response.getValueFromResponseBody("id"));

//		//////////////////////////////////////////////////////////////////////////////////////////////////////
//		baseURI = "https://jsonplaceholder.typicode.com/";
//		Response responses = given().body(requestBody).post("posts").andReturn();
//		JsonPath jpath = new JsonPath(responses.getBody().asString());
//		System.out.println(jpath.get("id").toString());

	}
}
