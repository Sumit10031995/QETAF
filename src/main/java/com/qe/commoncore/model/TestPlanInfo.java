package com.qe.commoncore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TestPlanInfo {
    private String user;
    private String testPlanKey;
    private String summary;
}
