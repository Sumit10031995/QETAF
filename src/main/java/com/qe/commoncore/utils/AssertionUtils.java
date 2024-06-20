package com.qe.commoncore.utils;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import com.aventstack.extentreports.Status;

public class AssertionUtils {
	ReportingUtil reporter;
	private static ThreadLocal<SoftAssert> softAssertThreadLocal = new ThreadLocal<>();

	public AssertionUtils(ReportingUtil report) {
		reporter = report;
	}

	private SoftAssert getSoftAssert() {
		SoftAssert softAssert = softAssertThreadLocal.get();
		if (softAssert == null) {
			softAssert = new SoftAssert();
			softAssertThreadLocal.set(softAssert);
		}
		return softAssert;
	}

	public void assertAll() {
		getSoftAssert().assertAll();
	}

	public void removeSoftAssert() {
		softAssertThreadLocal.remove();
	}

	/**
	 * Returns true if actual matches expected, otherwise false. Uses TestNG assert
	 * internally.
	 * 
	 * @param actual
	 * @param expected
	 * @param message
	 * @param isSoftAssert
	 * @return
	 * @throws Exception
	 */
	public boolean assertEquals(Object actual, Object expected, String message, boolean isSoftAssert) throws Exception {
		boolean isEqual = false;
		try {
			if (!actual.getClass().getName().equals(expected.getClass().getName())) {
				reporter.logTestStepDetails(Status.FAIL,
						"assertEquals" + " : " + message + "<br>" + "Expected class name: "
								+ expected.getClass().getName() + "<br>Actual class name: "
								+ actual.getClass().getName());
				throw new Exception("Run Time Class Mismatch");
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		try {
			if (isSoftAssert) {
				getSoftAssert().assertEquals(actual, expected, message);
				if (actual.equals(expected)) {
					reporter.logTestStepDetails(Status.PASS,
							"assertEquals" + " : " + message + "<br>Expected: " + expected + "<br>Actual: " + actual);
					isEqual = true;
				} else {
					reporter.logTestStepDetails(Status.FAIL,
							"assertEquals" + " : " + message + "<br>Expected: " + expected + "<br>Actual: " + actual);
				}
			} else {
				Assert.assertEquals(actual, expected, message);
				reporter.logTestStepDetails(Status.PASS,
						"assertEquals" + " : " + message + "<br>Expected: " + expected + "<br>Actual: " + actual);
				isEqual = true;
			}

		} catch (AssertionError e) {
			reporter.logTestStepDetails(Status.FAIL,
					"assertEquals" + " : " + message + "<br>Expected: " + expected + "<br>Actual: " + actual);
			throw e;
		}
		return isEqual;
	}

	/**
	 * 
	 * @param actual
	 * @param expected
	 * @param message
	 * @param isSoftAssert
	 * @return
	 * @throws Exception
	 */
	public boolean assertNotEquals(Object actual, Object expected, String message, boolean isSoftAssert)
			throws Exception {
		boolean isEqual = false;
		try {
			if (!actual.getClass().getName().equals(expected.getClass().getName())) {
				reporter.logTestStepDetails(Status.FAIL,
						"assertNotEquals" + " : " + message + "<br>Expected class name: "
								+ expected.getClass().getName() + "<br>Actual class name: "
								+ actual.getClass().getName());
				throw new Exception("Run Time Class Mismatch");
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		try {
			if (isSoftAssert) {
				getSoftAssert().assertNotEquals(actual, expected, message);
				if (!actual.equals(expected)) {
					reporter.logTestStepDetails(Status.PASS, "assertNotEquals" + " : " + message + "<br>Not Expected: "
							+ expected + "<br>Actual: " + actual);
					isEqual = true;
				} else {
					reporter.logTestStepDetails(Status.FAIL, "assertNotEquals" + " : " + message + "<br>Not Expected: "
							+ expected + "<br>Actual: " + actual);
				}
			} else {
				Assert.assertNotEquals(actual, expected, message);
				reporter.logTestStepDetails(Status.PASS, "assertNotEquals" + " : " + message + "<br>Not Expected: "
						+ expected + "<br>Actual: " + actual);
				isEqual = true;
			}

		} catch (AssertionError e) {
			reporter.logTestStepDetails(Status.FAIL,
					"assertNotEquals" + " : " + message + "<br>Not Expected: " + expected + "<br>Actual: " + actual);
			throw e;
		}
		return isEqual;
	}

	/**
	 * 
	 * @param condition
	 * @param values
	 * @param message
	 * @param isSoftAssert
	 * @return
	 * @throws Exception
	 */
	public boolean assertTrue(boolean condition, String message, boolean isSoftAssert) throws Exception {
		boolean isEqual = false;
		try {
			if (isSoftAssert) {
				getSoftAssert().assertTrue(condition, message);
				if (condition) {
					reporter.logTestStepDetails(Status.PASS, "assertTrue : " + message + "<br>Condition: " + condition);
					isEqual = true;
				} else {
					reporter.logTestStepDetails(Status.FAIL, "assertTrue : " + message + "<br>Condition: " + condition);
				}
			} else {
				Assert.assertTrue(condition, message);
				reporter.logTestStepDetails(Status.PASS, "assertTrue : " + message + "<br>Condition: " + condition);
				isEqual = true;
			}

		} catch (AssertionError e) {
			reporter.logTestStepDetails(Status.FAIL, "assertTrue : " + message + "<br>Condition: " + condition);
			throw e;
		}
		return isEqual;
	}

	/**
	 * 
	 * @param response
	 * @param value
	 * @param message
	 * @param isSoftAssert
	 * @return
	 * @throws Exception
	 */

	public boolean assertContains(String response, String value, String message, boolean isSoftAssert)
			throws Exception {
		boolean isEqual = false;
		try {
			if (isSoftAssert) {
				getSoftAssert().assertTrue(response.contains(value), message);
				if (response.contains(value)) {
					reporter.logTestStepDetails(Status.PASS,
							"assertContains : " + message + "<br>Expected value: " + value + "<br>In : " + response);
					isEqual = true;
				} else {
					reporter.logTestStepDetails(Status.FAIL,
							"assertContains : " + message + "<br>Expected value: " + value + "<br>In : " + response);
				}
			} else {
				Assert.assertTrue(response.contains(value), message);
				reporter.logTestStepDetails(Status.PASS,
						"assertContains : " + message + "<br>Expected value: " + value + "<br>In : " + response);
				isEqual = true;
			}

		} catch (AssertionError e) {
			reporter.logTestStepDetails(Status.FAIL,
					"assertContains : " + message + "<br>Expected value: " + value + "<br>In : " + response);
			throw e;
		}
		return isEqual;
	}

	/**
	 * 
	 * @param condition
	 * @param values
	 * @param message
	 * @param isSoftAssert
	 * @return
	 * @throws Exception
	 */
	public boolean assertFalse(boolean condition, String values, String message, boolean isSoftAssert)
			throws Exception {
		boolean isEqual = false;
		try {
			if (isSoftAssert) {
				getSoftAssert().assertFalse(condition, message);
				if (!condition) {
					reporter.logTestStepDetails(Status.PASS,
							"assertFalse : " + message + "<br>Condition : " + condition);
					isEqual = true;
				} else {
					reporter.logTestStepDetails(Status.FAIL,
							"assertFalse : " + message + "<br>Condition : " + condition);
				}
			} else {
				Assert.assertFalse(condition, message);
				reporter.logTestStepDetails(Status.PASS, "assertFalse : " + message + "<br>Condition : " + condition);
				isEqual = true;
			}

		} catch (AssertionError e) {
			reporter.logTestStepDetails(Status.FAIL, "assertFalse : " + message + "<br>Condition : " + condition);
			throw e;
		}
		return isEqual;
	}

	/**
	 * 
	 * @param actual
	 * @param message
	 * @param isSoftAssert
	 * @return
	 * @throws Exception
	 */
	public boolean assertNull(Object actual, String message, boolean isSoftAssert) throws Exception {
		boolean isEqual = false;
		try {
			if (isSoftAssert) {
				getSoftAssert().assertNull(actual, message);
				if (actual == null) {
					reporter.logTestStepDetails(Status.PASS, "assertNull : " + message + "<br>Actual: " + actual);
					isEqual = true;
				} else {
					reporter.logTestStepDetails(Status.FAIL, "assertNull : " + message + "<br>Actual: " + actual);
				}
			} else {
				Assert.assertNull(actual, message);
				reporter.logTestStepDetails(Status.PASS, "assertNull : " + message + "<br>Actual: " + actual);
				isEqual = true;
			}
		} catch (AssertionError e) {
			reporter.logTestStepDetails(Status.FAIL, "assertNull : " + message + "<br>Actual: " + actual);
			throw e;
		}
		return isEqual;
	}

	/**
	 * 
	 * @param object
	 * @param message
	 * @param isSoftAssert
	 * @return
	 * @throws Exception
	 */
	public boolean assertNotNull(Object actual, String message, boolean isSoftAssert) throws Exception {
		boolean isEqual = false;
		try {
			if (isSoftAssert) {
				getSoftAssert().assertNotNull(actual, message);
				if (actual != null) {
					reporter.logTestStepDetails(Status.PASS, "assertNull : " + message + "<br>Actual: " + actual);
					isEqual = true;
				} else {
					reporter.logTestStepDetails(Status.FAIL, "assertNotNull : " + message + "<br>Actual: " + actual);
				}
			} else {
				Assert.assertNotNull(actual, message);
				reporter.logTestStepDetails(Status.PASS, "assertNull : " + message + "<br>Actual: " + actual);
				isEqual = true;
			}

		} catch (AssertionError e) {
			reporter.logTestStepDetails(Status.FAIL, "assertNotNull : " + message + "<br>Actual: " + actual);
			throw e;
		}
		return isEqual;
	}

}