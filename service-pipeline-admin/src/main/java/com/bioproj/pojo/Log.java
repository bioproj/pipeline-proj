package com.bioproj.pojo;

import com.bioproj.domain.enums.WorkflowStatus;
import lombok.Data;

@Data
public class Log {
    private String id;
    private String log;
    private WorkflowStatus taskStatus;
    private int attempts;

}
