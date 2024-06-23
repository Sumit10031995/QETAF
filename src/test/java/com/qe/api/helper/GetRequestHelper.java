package com.qe.api.helper;

import com.qe.api.commoncore.BaseTest;
import com.qe.api.constants.ENVConstants;
import com.qe.apicore.impl.APIResponse;
import com.qe.apicore.impl.ApiDriver;

public class GetRequestHelper extends BaseTest{
	
    //End point
	private static final String getAPIEndPoint="posts";

	/**
	 * 
	 * @return
	 * @throws Exception
	 * Do GET request
	 */
	public APIResponse doGetRequest() throws Exception {
	     ApiDriver apiDriver = new ApiDriver(configurator.getEnvironmentParameter(ENVConstants.baseURL), getAPIEndPoint);;
         return apiDriver.GET();
	}
}
