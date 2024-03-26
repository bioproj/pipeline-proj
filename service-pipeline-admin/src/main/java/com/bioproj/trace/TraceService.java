package com.bioproj.trace;

import com.bioproj.pojo.task.Task;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.trace.model.*;

import java.util.List;

public interface TraceService {

    @Deprecated
    Workflow processWorkflowTrace(TraceWorkflowRequest request);

    @Deprecated
    List<Task> processTaskTrace(TraceTaskRequest request);

    @Deprecated void keepAlive(String workflowId);

    Workflow handleFlowBegin(TraceBeginRequest request);
    Workflow handleFlowComplete(TraceCompleteRequest request);
    void handleTaskTrace(Workflow workflow, TraceProgressData progress, List<TraceTaskData> traceTaskData);
    void handleHeartbeat(String workflowId, TraceProgressData progress);
}
