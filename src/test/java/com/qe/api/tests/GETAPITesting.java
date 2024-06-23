package com.qe.api.tests;
import org.testng.annotations.Test;

import com.qe.api.commoncore.BaseTest;
import com.qe.api.helper.GetRequestHelper;
import com.qe.apicore.impl.APIResponse;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class GETAPITesting extends BaseTest{
	GetRequestHelper getRequestHelper=new GetRequestHelper();
	
	//url=https://jsonplaceholder.typicode.com/posts
	@Test(groups = {"GET","API"})
	public void getAPITesting() throws Exception {
	 reporter.createTestStep("Do GET API call and verify HTTP response code");
     APIResponse apiResponse = getRequestHelper.doGetRequest();
     assertion.assertEquals(apiResponse.getHttpStatusCode(),200, "Validate HTTP status code", false);
     assertion.assertNotNull(apiResponse.getResposneBody(), "Response shouldn't be null", false);
     System.out.println(apiResponse.getValueFromResponseBody("findAll { it.id == 1 }"));
     
     //////////////////////////////////////////////////////////////////////////////////////////////////////
     RestAssured.baseURI="https://jsonplaceholder.typicode.com/";
     Response response=RestAssured.given().get("posts").andReturn();
     JsonPath jpath = new JsonPath(response.asString());
     Object obj=jpath.get("findAll{it.id==1}");
     System.out.println(obj);
     
	}
}
