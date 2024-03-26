package com.bioproj.domain.vo;

import com.bioproj.domain.enums.TaskStatus;
import lombok.Data;

import java.util.List;

@Data
public class WorkflowTaskVo {
    private String sampleName;
    private String workflowName;
    private List<TaskDataVo> taskDataVos;

}
