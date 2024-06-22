package com.qe.api.commoncore;

import com.qe.commoncore.constants.ContextConstant;

/**
 * 
 * Test case default values
 *
 */
public class ExecutionContext {
	
	public ExecutionContext() {
		super();
	}
	
	private String normalizeURL(String url){
		if(url!=null && !url.endsWith("/"))
			url=url+"/";
		if (url!=null && !url.startsWith(ContextConstant.SERVER_PROTOCOL))
			url = ContextConstant.SERVER_PROTOCOL+ContextConstant.SERVER_PROTOCOL_SLASHES+ url;
		return url; 
	}

}
