package com.bioproj.domain.vo;

import com.bioproj.domain.enums.WorkflowStatus;
import lombok.Data;

@Data
public class WorkflowResult {
    private String id;
    private WorkflowStatus workflowStatus;
    private String workDir;
    private String outputDir;
}
