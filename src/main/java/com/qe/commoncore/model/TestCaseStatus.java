package com.qe.commoncore.model;

import com.qe.api.enums.TestStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestCaseStatus {
    private String testKey;

    private TestStatus status;
    
    private String testMethod;
    
    private String methodDescription;

    private String comment;
    
    private String xmlSuiteName;


	
}
