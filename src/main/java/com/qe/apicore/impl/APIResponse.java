package com.qe.apicore.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.qe.commoncore.utils.JsonUtil;

import io.restassured.response.Response;

public class APIResponse {
	
	public Response response;
	   
	public APIResponse()
	{
		//do nothing
	}
	
    public Integer getCountFromResponseBody(String jsonPath) throws Exception {
    	return JsonUtil.getCount(response.getBody().asString(), jsonPath);
    }
    public Object getValueFromResponseBody(String jsonPath) throws Exception {
    	return JsonUtil.getValue(response.getBody().asString(), jsonPath);
    }
    public List<Object> getValuesFromResponseBody(String jsonPath) throws Exception {
    	return JsonUtil.getValues(response.getBody().asString(), jsonPath);
    }
    public Integer getHttpStatusCode() {
    	return response.getStatusCode();
    }
    public long getTimeTakenInMs() {
    	return response.getTimeIn(TimeUnit.MILLISECONDS);
    }
    public Object getHeadersByKey(String key) {
    	return response.getHeader(key);
    }
    public Map getAllHeaders(){
    	return (Map) response.getHeaders();
    }
    public String getResposneBody() {
    	return response.getBody().asString();
    }
    
    public String PrettyPrint () {
    	return response.prettyPrint();
    }
}
