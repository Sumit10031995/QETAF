package com.qe.retry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import com.qe.api.commoncore.Configurator;
import com.qe.commoncore.constants.ContextConstant;
import com.qe.commoncore.utils.ReportingUtil;

public class RetryListener implements IAnnotationTransformer {
    @Override
    public void transform(ITestAnnotation testannotation, Class testClass, Constructor testConstructor, Method testMethod) {
            testannotation.setRetryAnalyzer(Retry.class);
    }
    
    /**
     * 
     * @param methodName
     * @return boolean value
     * @description  This method return a boolean value 'TRUE' if retry count is grater than zero, else return value 'FALSE'.
     * In this method a check has been incorporated into the retry action. This check ensures the removal of previously added
     * extent report details from the extent report object if the method is re-executed due to the retry policy.
     */
	public static synchronized boolean isRetrying(String identifierKey) {
		boolean isReportRetryTestEnabled=Boolean.parseBoolean(Configurator.getInstance().getParameter(ContextConstant.REPORT_RETRY_TESTS));
		if (Retry.retryCount.get()>0) { 
			if(ReportingUtil.methodVsTest.containsKey(identifierKey) && !isReportRetryTestEnabled) {
				ReportingUtil.extent.removeTest(ReportingUtil.methodVsTest.get(identifierKey));
			 }
			return true;
		} else {
			return false;
		}
	}
	
}