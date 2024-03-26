package com.bioproj.trace.model;

import com.bioproj.pojo.task.Workflow;
import lombok.Data;

@Data
public class TraceBeginRequest {
    Workflow workflow;
}
