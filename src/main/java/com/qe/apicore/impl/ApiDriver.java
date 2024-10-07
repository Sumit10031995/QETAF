package com.qe.apicore.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.aventstack.extentreports.Status;
import com.qe.api.enums.HttpRestMethod;
import com.qe.api.enums.RestContentType;
import com.qe.commoncore.utils.FileUtil;
import com.qe.commoncore.utils.JavaUtils;
import com.qe.commoncore.utils.JsonUtil;
import com.qe.commoncore.utils.ReportingUtil;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;

/**
 * This class is a wrapper over rest-assured and having functions to hit an API request.
 */
public class ApiDriver {
	private String url;
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
		this.apiHost = apiHost;
		this.endPoint = endPoint;
		this.requestContentType = RestContentType.JSON;
	}

	public ApiDriver(String apiHost, String endPoint, Map<String, Object> headers) {
		this.apiHost = apiHost;
		this.endPoint = endPoint;
		this.requestHeaders = headers;
		this.requestContentType = RestContentType.JSON;
	}

	private RequestSpecification init() {
		JsonPath path=new JsonPath("{}");
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

	public APIResponse POST() throws Exception {
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
		//Catch all but actually created to catch API time-out from client side. 
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
		this.requestBody = "{}";
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
		}else {
			this.requestBody = "{}";	
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
		}else {
			this.requestBody = "{}";	
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
		}else {
			this.requestBody = "{}";	
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
	 * @param apiResponse
	 * @return String
	 *    This method is use to encapsulate all API's request/response details within HTML tags for inclusion in Extent Report.
	 * @throws Exception
	 */
	private String getAPIDetails(HttpRestMethod httpMethod,APIResponse apiResponse) throws Exception {
		System.out.println(buildCurlCommand(httpMethod));
		String apiSpecification = "<pre>"
				+ "Request Configuration" + "<br>"
				+ "̄API:"+ httpMethod+ "--"+ this.apiHost+ this.getendPoint()+"<br>"
				+ JavaUtils.getAsHTML("Headers : ",this.getHeaders().toString())+ "<br>"
				+ "QueryParameters:"+ this.getQueryParams().toString() +"<br>"
				+ "PathParameters:"+ this.getPathParams().toString()
				+JavaUtils.getAsHTML("Request Body : ",JsonUtil.prettyPrint((this.requestBody.toString().contains("<a href="))?this.requestBody.toString().replace("a href=", ""):this.requestBody.toString()))+ "<br>"	
				+ "</pre><pre>Response Details"+ "<br>"
				+ JavaUtils.getAsHTML("<br> Response Code:"+apiResponse.response.statusCode()+ "<br>"+"Response Headers : ",apiResponse.response.headers().toString())+ "<br>"
				+ JavaUtils.getAsHTML("Response Body : ",apiResponse.PrettyPrint().toString())+"</pre>";
		return apiSpecification;
	}

	public String buildCurlCommand(HttpRestMethod httpMethod) {
		StringBuilder curlCommand = new StringBuilder("curl -X " + httpMethod);

		// Build the full URL, including path and query parameters
		StringBuilder fullUrl = new StringBuilder(this.getapiHost());

		// Append the endpoint if provided
		if (endPoint != null && !endPoint.isEmpty()) {
			// Replace path parameters in the endpoint, like /users/{id}/{type}
			for (Map.Entry<String, Object> entry : pathParams.entrySet()) {
				String placeholder = "{" + entry.getKey() + "}";
				endPoint = endPoint.replace(placeholder, entry.getValue().toString());
			}
			fullUrl.append(endPoint);
		}
		// Append query parameters if any
		if (!queryParams.isEmpty()) {
			fullUrl.append("?");
			queryParams.forEach((key, value) -> fullUrl.append(key).append("=").append(value).append("&"));
			fullUrl.setLength(fullUrl.length() - 1);
		}
		// Add the full URL to the curl command
		curlCommand.append(" '").append(fullUrl).append("'");

		// Add headers
		for (Map.Entry<String, Object> header : requestHeaders.entrySet()) {
			curlCommand.append(" -H '").append(header.getKey()).append(": ").append(header.getValue()).append("'");
		}
		curlCommand.append(" -H 'Content-Type: application/json'");
		// Add body data if any
		if (requestBody != null) {
			curlCommand.append(" -d '").append(requestBody.toString()).append("'");
		}
		// Add form parameters if any
		if (!formParams.isEmpty()) {
			formParams.forEach((key, value) -> {
				curlCommand.append(" -F '").append(key).append("=").append(value).append("'");
			});
		}
		// Add multipart data if any
		if (!multiParts.isEmpty()) {
			multiParts.forEach((key, value) -> {
				curlCommand.append(" -F '").append(key).append("=@").append(value).append("'");
			});
		}
		return curlCommand.toString();
	}
  
}
