package com.qe.commoncore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestPlan {
    private String testExecutionKey;
    private TestPlanInfo info;
    private List<TestCaseStatus> tests;
	public String getTestExecutionKey() {
		return testExecutionKey;
	}
	public void setTestExecutionKey(String testExecutionKey) {
		this.testExecutionKey = testExecutionKey;
	}
	public List<TestCaseStatus> getTests() {
		return tests;
	}
	public void setTests(List<TestCaseStatus> tests) {
		this.tests = tests;
	}
	public TestPlanInfo getInfo() {
		return info;
	}
	public void setInfo(TestPlanInfo info) {
		this.info = info;
	}
}
