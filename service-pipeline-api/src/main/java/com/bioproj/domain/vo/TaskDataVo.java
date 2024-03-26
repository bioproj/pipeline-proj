package com.bioproj.domain.vo;

import com.bioproj.domain.enums.TaskStatus;
import lombok.Data;

@Data
public class TaskDataVo {
    private String name;
    private String process;
    private String submit;
    private String start;
    private TaskStatus status;
}
