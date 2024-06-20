package com.qe.retry;

import java.time.Duration;
import java.util.NoSuchElementException;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import com.qe.commoncore.Configurator;
import com.qe.commoncore.constants.ContextConstant;

import net.jodah.failsafe.RetryPolicy;

public class Retry implements IRetryAnalyzer {
	private int minRetryCount = 0;
	private int maxRetryCount;
    public static ThreadLocal<Integer> retryCount=ThreadLocal.withInitial(() -> 0);
	/**
	 * @description This method is use to do retry action. 
	 * Retry action depends on RETRY_FAILED_CASE=true/false and MAX_RETET_COUNT field.
	 */
	@Override
	public boolean retry(final ITestResult result) {
		maxRetryCount = Integer.parseInt(Configurator.getInstance().getParameter(ContextConstant.MAX_RETRY_COUNT));
		boolean isRetry = Boolean
				.parseBoolean(Configurator.getInstance().getParameter(ContextConstant.RETRY_FAILED_TESTS));
		if (isRetry && !result.isSuccess()) {
			if (minRetryCount < maxRetryCount) {
				retryCount.set(++minRetryCount);
				return true;
			}
			retryCount.set(0);
		}
		return false;
	}

//	/**
//	 *
//	 * @return retryPolicy object
//	 * @description If user intends to retry a specific set of syntax on a particular exception,
//	 * this method can be utilize.
//	 */
//	public RetryPolicy getRetryPolicy() throws Exception {
//		maxRetryCount = Integer.parseInt(Configurator
//				.retryInputAnalyser(Configurator.getInstance().getParameter(ContextConstant.MAX_RETRY_COUNT)));
//		boolean isRetry = Boolean
//				.parseBoolean(Configurator.getInstance().getParameter(ContextConstant.RETRY_FAILED_TESTS));
//		RetryPolicy<Object> retryPolicy = new RetryPolicy<>().handle(NullPointerException.class)
//				.handle(NoSuchElementException.class).handle(AssertionError.class).handle(AssertionException.class)
//				.handle(ArrayIndexOutOfBoundsException.class);
//
//		if (isRetry) {
//			retryPolicy.withDelay(Duration.ofSeconds(5)).withMaxRetries(maxRetryCount);
//		} else {
//			retryPolicy.withMaxAttempts(1);
//		}
//
//		return retryPolicy;
//	}

}
