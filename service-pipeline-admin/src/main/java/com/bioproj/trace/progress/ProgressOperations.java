package com.bioproj.trace.progress;

import com.bioproj.pojo.task.Task;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.trace.model.TraceProgressData;

import java.util.List;

public interface ProgressOperations {
    void updateProgress(Workflow workflow, TraceProgressData traceProgressData);
    void updateProgress(String id, TraceProgressData empty);

    void aggregateMetrics(String workflowId, List<Task> tasks);


    void create(Workflow workflow, TraceProgressData traceProgressData);
}
