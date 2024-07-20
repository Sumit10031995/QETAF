package com.qe.api.tests;
import java.util.Arrays;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.qe.api.commoncore.BaseTest;
import com.qe.api.helper.GetRequestHelper;
import com.qe.apicore.impl.APIResponse;
import com.qe.commoncore.annotations.Jira;
import com.qe.commoncore.constants.ContextConstant;
import com.qe.commoncore.utils.TestDataUtil;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class GETAPITesting extends BaseTest{
	
	GetRequestHelper getRequestHelper=new GetRequestHelper();
	  @DataProvider(name = "test")
	    public Object [][] fff() throws Exception{
		  int dataProviderLimit=Integer.parseInt(configurator.getParameter(ContextConstant.DATA_PROVIDER_LIMIT));

		  Object [][] obj=  TestDataUtil.readDataFromCSV_File("src/test/resources/testData.csv","scenario",Arrays.asList("scenario1","scenario3"));
	        Object[][] copiedArray = new Object[dataProviderLimit][obj[0].length];

	        try {
	        	 for (int i = 1; i <= dataProviderLimit; i++) {
	 	        	for (int j = 0; j < obj[i].length; j++) {
	 	        		copiedArray[i-1][j] =obj[i][j];
	 		        }
	 	        }
	        }catch(Exception e) {
	        	copiedArray = new Object[obj.length-1][obj[0].length];
	        for (int i = 1; i < obj.length; i++) {
	        	for (int j = 0; j < obj[i].length; j++) {
	        		copiedArray[i-1][j] =obj[i][j];
		        }
	        }
	        }

		  return copiedArray;
	    }
	  
	  
	  
	//url=https://jsonplaceholder.typicode.com/posts
	@Jira(jiraTestKey = "TEST-123")
	@Test(groups = {"GET","API"}, dataProvider ="test" )
	public void getAPITesting(String one,String two,String three,String four,String five) throws Exception {
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
	
	@Test(groups = {"GET","API"})
	public void getAPITestin() throws Exception {
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
