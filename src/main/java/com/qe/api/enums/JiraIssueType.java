package com.qe.api.enums;

public class JiraIssueType {

	public enum IssueType {
		BUG("Bug"), 
		TESTEXECUTION("Test Execution"), 
		TESTPLAN("Test Plan"), 
		TEST("Test");

		private final String issueType;

		IssueType(String issueType) {
			this.issueType = issueType;
		}

		public String get() {
			return this.issueType.toString();
		}
	}
}
