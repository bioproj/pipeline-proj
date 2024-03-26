package com.bioproj.domain.vo;

import com.bioproj.domain.enums.WorkflowStatus;
import lombok.Data;

@Data
public class WorkflowDto {
    private String id;
    private String name;
    private String topic;
    private WorkflowStatus workflowStatus;
}
