package com.qe.api.tests;
import org.testng.annotations.Test;
import com.qe.api.constants.ENVConstants;
import com.qe.apicore.impl.APIResponse;
import com.qe.apicore.impl.ApiDriver;
import com.qe.commoncore.BaseTest;
import io.restassured.response.*;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

public class GETAPITesting extends BaseTest{
	
	//url=https://jsonplaceholder.typicode.com/posts
	private static final String getAPIEndPoint="posts";
	@Test(groups = {"GET"})
	public void getAPITesting() throws Exception {
     APIResponse apiDriver = new ApiDriver(configurator.getEnvironmentParameter(ENVConstants.baseURL), getAPIEndPoint).GET();
     assertion.assertEquals(apiDriver.getHttpStatusCode(),200, "Validate HTTP status code", false);
     assertion.assertNotNull(apiDriver.getResposneBody(), "Response shouldn't be null", false);
     System.out.println(apiDriver.getValueFromResponseBody("findAll { it.id == 1 }"));
     
     //////////////////////////////////////////////////////////////////////////////////////////////////////
     RestAssured.baseURI="https://jsonplaceholder.typicode.com/";
     Response response=RestAssured.given().get("posts").andReturn();
     JsonPath jpath = new JsonPath(response.asString());
     Object obj=jpath.get("findAll{it.id==1}");
     System.out.println(obj);
     
	}
}
