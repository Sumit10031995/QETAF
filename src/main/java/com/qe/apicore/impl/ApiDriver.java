package com.qe.apicore.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.aventstack.extentreports.Status;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.qe.api.commoncore.Configurator;
import com.qe.api.enums.HttpRestMethod;
import com.qe.api.enums.RestContentType;
import com.qe.commoncore.constants.ContextConstant;
import com.qe.commoncore.utils.FileUtil;
import com.qe.commoncore.utils.JavaUtils;
import com.qe.commoncore.utils.JsonUtil;
import com.qe.commoncore.utils.ReportingUtil;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;


/**
 * This class is a wrapper over rest-assured and having functions to hit an API request.
 */
public class ApiDriver {
	
	private HttpRestMethod httpRestMethod;
	private RequestSpecification requestSpecification;
	private String apiHost;
	private RestContentType requestContentType;
	private String endPoint;
	private Object requestBody;
	private Map<String, Object> queryParams = new HashMap<>();
	private Map<String, Object> pathParams = new HashMap<>();
	private Map<String, Object> requestHeaders = new HashMap<>();
	private Map<String, Object> formParams = new HashMap<>();
	private Map<String, Object> multiParts = new HashMap<>();
    ReportingUtil reporter = ReportingUtil.getInstance();


	public ApiDriver(String apiHost, String endPoint) {
		//this.setConfigTimeOut();
		this.apiHost = apiHost;
		this.endPoint = endPoint;
		this.requestContentType = RestContentType.JSON;
	}

	public ApiDriver(String apiHost, String endPoint, Map<String, Object> headers) {
		//this.setConfigTimeOut();
		this.apiHost = apiHost;
		this.endPoint = endPoint;
		this.requestHeaders = headers;
		this.requestContentType = RestContentType.JSON;
	}

	private RequestSpecification init() {
		this.requestSpecification = RestAssured.given();
		this.requestSpecification.baseUri(apiHost);
		this.endPoint = EndPointManager.reformatEndPoint(this.endPoint);
		if (this.requestContentType != null && !this.requestContentType.equals("")) {
			requestSpecification.contentType(this.requestContentType.getContentType());
		}
		if (!this.pathParams.isEmpty()) {
			requestSpecification.pathParams(this.pathParams);
		}
		if (!this.formParams.isEmpty()) {
			requestSpecification.formParams(this.formParams);
		}
		if (!this.queryParams.isEmpty()) {
			requestSpecification.queryParams(this.queryParams);
		}
		requestSpecification.relaxedHTTPSValidation();
		return requestSpecification;
	}

	public APIResponse POST() {
		APIResponse apiResponse = new APIResponse();
		RequestSpecification requestSpecification = init();
		requestSpecification.headers(requestHeaders);
		if (this.requestBody != null && !this.requestBody.toString().equals("")) {
			requestSpecification.body(this.requestBody);
		}
		else {
			this.requestBody = "{}";	
		}
		if (!this.multiParts.isEmpty()) {
			for (Map.Entry<String, Object> entry : this.multiParts.entrySet()) {				
				if(!entry.getKey().equalsIgnoreCase("attachment")) 
					requestSpecification.multiPart(entry.getKey(), entry.getValue());
				else {
					 File input = FileUtil.getFile((String)entry.getValue());
					requestSpecification.multiPart(entry.getKey(),input );
				}

			}
		}
		if (reporter.extentTestSteps.get() == null) {
			reporter.createTestStep("Post : "+this.endPoint);
		}
		
		try {
			if (this.endPoint != null && !this.endPoint.equals("")) {
				apiResponse.response = requestSpecification.post(this.endPoint);
			} else {
				apiResponse.response = requestSpecification.post();
			}
			reporter.logTestStepDetails(Status.INFO, getAPIDetails(HttpRestMethod.POST,apiResponse));
		}
		catch (Exception e) {
			reporter.logTestStepDetails(Status.FAIL,
					"POST Request is having some issue :- " + e.getMessage().toString());
			throw e;
		}
		return apiResponse;
	}

	public APIResponse GET() throws Exception {
		APIResponse apiResponse = new APIResponse();
		RequestSpecification requestSpecification = init();
		requestSpecification.headers(requestHeaders);
		if (reporter.extentTestSteps.get() == null) {
			reporter.createTestStep("GET : "+this.endPoint);
		}
		try {
			if (this.endPoint != null && !this.endPoint.equals("")) {
				apiResponse.response = requestSpecification.get(endPoint);
			} else {
				reporter.logTestStepDetails(Status.FAIL, "Invalid endPoint");
				throw new Exception("endPoint value invalid");
			}
			reporter.logTestStepDetails(Status.INFO, getAPIDetails(HttpRestMethod.GET,apiResponse));
		} catch (Exception e) {
			reporter.logTestStepDetails(Status.FAIL,
					"GET Request is having some issue :- " + e.getMessage().toString());
			throw e;
		}
		return apiResponse;
	}

	public APIResponse PUT() throws Exception {
		APIResponse apiResponse = new APIResponse();
		RequestSpecification requestSpecification = init();
		requestSpecification.headers(requestHeaders);
		if (this.requestBody != null && !this.requestBody.equals("")) {
			requestSpecification.body(this.requestBody);
		}
		if (reporter.extentTestSteps.get() == null) {
			reporter.createTestStep("PUT:"+this.endPoint);
		}
		try {
			if (this.endPoint != null && !this.endPoint.equals("")) {
				apiResponse.response = requestSpecification.put(endPoint);
			} else {
				reporter.logTestStepDetails(Status.FAIL, "Invalid endPoint");
				throw new Exception("endPoint value invalid");
			}
			reporter.logTestStepDetails(Status.INFO, getAPIDetails(HttpRestMethod.PUT,apiResponse));
		} catch (Exception e) {
			reporter.logTestStepDetails(Status.FAIL,
					"PUT Request is having some issue :- " + e.getMessage().toString());
			throw e;
		}
		return apiResponse;
	}

	public APIResponse DELETE() throws Exception {
		APIResponse apiResponse = new APIResponse();
		RequestSpecification requestSpecification = init();
		requestSpecification.headers(requestHeaders);
		if (reporter.extentTestSteps.get() == null) {
			reporter.createTestStep("DELETE:"+this.endPoint);
		}
		try {
			if (this.endPoint != null && !this.endPoint.equals("")) {
				apiResponse.response = requestSpecification.delete(endPoint);
			} else {
				reporter.logTestStepDetails(Status.FAIL, "Invalid endPoint");
				throw new Exception("endPoint value invalid");
			}
			reporter.logTestStepDetails(Status.INFO, getAPIDetails(HttpRestMethod.DELETE,apiResponse));
		} catch (Exception e) {
			reporter.logTestStepDetails(Status.FAIL,
					"DELETE Request is having some issue :- " + e.getMessage().toString());
			throw e;
		}
		return apiResponse;
	}
	
	/**
	 * Delete API method with request body
	 * @return APIResponse
	 * @throws Exception 
	 */
	public APIResponse DELETEWITHBODY() throws Exception {
		APIResponse apiResponse = new APIResponse();
		RequestSpecification requestSpecification = init();
		requestSpecification.headers(requestHeaders);
		if (this.requestBody != null && !this.requestBody.equals("")) {
			requestSpecification.body(this.requestBody);
		}
		if (reporter.extentTestSteps.get() == null) {
			reporter.createTestStep("DELETE:"+this.endPoint);
		}
		try {
			if (this.endPoint != null && !this.endPoint.equals("")) {
				apiResponse.response = requestSpecification.delete(endPoint);
			} else {
				reporter.logTestStepDetails(Status.FAIL, "Invalid endPoint");
				reporter.logTestStepDetails(Status.INFO, apiResponse.toString());
				throw new Exception("endPoint value invalid");
			}
			reporter.logTestStepDetails(Status.INFO, getAPIDetails(HttpRestMethod.DELETE,apiResponse));
		} catch (Exception e) {
			reporter.logTestStepDetails(Status.FAIL,
					"DELETEWITHBODY Request is having some issue :- " + e.getMessage().toString());
			throw e;
		}
		return apiResponse;
	}

	
	/**
	 * PATCH API method with request body
	 * @return APIResponse
	 * @throws Exception 
	 */
	public APIResponse PATCH() throws Exception {
		APIResponse apiResponse = new APIResponse();
		RequestSpecification requestSpecification = init();
		requestSpecification.headers(requestHeaders);
		if (this.requestBody != null && !this.requestBody.equals("")) {
			requestSpecification.body(this.requestBody);
		}
		if (reporter.extentTestSteps.get() == null) {
			reporter.createTestStep("PATCH:"+this.endPoint);
		}
		
		try {
			if (this.endPoint != null && !this.endPoint.equals("")) {
				apiResponse.response = requestSpecification.patch(endPoint);
			} else {
				reporter.logTestStepDetails(Status.FAIL, "Invalid endPoint");
				throw new Exception("endPoint value invalid");
			}
			reporter.logTestStepDetails(Status.INFO, getAPIDetails(HttpRestMethod.PATCH,apiResponse));
		} catch (Exception e) {
			reporter.logTestStepDetails(Status.FAIL,
					"PATCH Request is having some issue :- " + e.getMessage().toString());
			throw e;
		}
		return apiResponse;
	}
	
	public HttpRestMethod getHttpRestMethod() {
		return httpRestMethod;
	}

	public void setHttpRestMethod(HttpRestMethod httpRestMethod) {
		this.httpRestMethod = httpRestMethod;
	}

	public RequestSpecification getRequestSpecification() {
		return requestSpecification;
	}

	public void setRequestSpecification(RequestSpecification requestSpecification) {
		this.requestSpecification = requestSpecification;
	}

	public String getapiHost() {
		return apiHost;
	}

	public void setapiHost(String apiHost) {
		this.apiHost = apiHost;
	}

	public RestContentType getRequestContentType() {
		return requestContentType;
	}

	public void setRequestContentType(RestContentType requestContentType) {
		this.requestContentType = requestContentType;
	}

	public String getendPoint() {
		return endPoint;
	}

	public void setendPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public String getRequestBody() {
		return requestBody.toString();
	}

	public void setRequestBody(Object requestBody) {
		this.requestBody = requestBody;
	}

	public Map<String, Object> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(Map<String, Object> queryParams) {
		this.queryParams = queryParams;
	}

	public Map<String, Object> getPathParams() {
		return pathParams;
	}

	public void setPathParams(Map<String, Object> pathParams) {
		this.pathParams = pathParams;
	}

	public Map<String, Object> getFormParams() {
		return formParams;
	}

	public void setFormParams(Map<String, Object> formParams) {
		this.formParams = formParams;
	}

	public Map<String, Object> getMultiParts() {
		return multiParts;
	}

	public void setMultiParts(Map<String, Object> multiParts) {
		this.multiParts = multiParts;
	}

	public Map<String, Object> getHeaders() {
		return requestHeaders;
	}

	public void setHeaders(Map<String, Object> headers) {
		this.requestHeaders = headers;
	}
	
	
	
	/**
	 * This method is required to configure RestAssured Timeout 
	 */
	private void setConfigTimeOut() {
		int timeOut = Integer.valueOf(Configurator.getInstance().getParameter(ContextConstant.API_TIMEOUT_MS));
		HttpClientConfig httpClientConfig = HttpClientConfig.httpClientConfig().setParam("http.socket.timeout", timeOut)
				.setParam("http.connection.timeout", timeOut);
		RestAssured.config = RestAssuredConfig.config().httpClient(httpClientConfig);
	}
	
	/**
	 * 
	 * @param url
	 * @param header
	 * @param queryParam
	 * @param pathParam
	 * @param requestBody
	 * @param apiResponse
	 * @return String
	 *    This method is use to encapsulate all API's request/response details within HTML tags for inclusion in Extent Report.
	 */
	private String getAPIDetails(HttpRestMethod httpMethod,APIResponse apiResponse) {
		//print API URL
		System.out.println("̄API:"+ httpMethod+ "--"+ this.apiHost+ this.getendPoint());
		//Print Request body
		if(!httpMethod.toString().equals("GET")) {
			System.out.println("Request Body :");
			System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(this.requestBody.toString())));
			System.out.println("Response Body :");
		}
		// If Deep Reporting value is true, print request and response details to ExtentReport(Request URL,Request Header,Request Body,Response Header,Response Body). Otherwise, only add request details to the ExtentReport(Request URL,Request Header).
		boolean isDeepReporting=Boolean.parseBoolean(Configurator.getInstance().getParameter(ContextConstant.DEEP_REPORTING));
		String apiSpecification = "<pre>"
				+ "Request Configuration" + "<br>"
				+ "̄API:"+ httpMethod+ "--"+ this.apiHost+ this.getendPoint()+"<br>"
				+ JavaUtils.getAsHTML("Headers : ",this.getHeaders().toString())+ "<br>"
				+ "QueryParameters:"+ this.getQueryParams().toString() +"<br>"
				+ "PathParameters:"+ this.getPathParams().toString();
		if(isDeepReporting) {
			apiSpecification=apiSpecification
					+JavaUtils.getAsHTML("Request Body : ",(this.requestBody==null)?"{}":JsonUtil.prettyPrint((this.requestBody.toString().contains("<a href="))?this.requestBody.toString().replace("a href=", ""):this.requestBody.toString()))+ "<br>"	
					+ "</pre><pre>Response Details"+ "<br>"
					+ JavaUtils.getAsHTML("Response Headers : ",apiResponse.response.headers().toString())+ "<br>"
					+ JavaUtils.getAsHTML("Response Body : ",apiResponse.PrettyPrint().toString());
		}
		apiSpecification=apiSpecification+"</pre>";
		// Print response body in IDE console if deep reporting is false.
		if(!isDeepReporting) {
			apiResponse.PrettyPrint().toString();
		}
		return apiSpecification;
	}
  
}
