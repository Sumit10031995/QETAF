package com.qe.commoncore.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TestCaseResult {
    private Integer id;
    private String key;
    private String latestStatus;
}
