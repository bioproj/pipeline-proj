package com.bioproj.trace.progress;

import com.bioproj.trace.model.TraceProgressData;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


public interface ProgressStore {
    void putTraceData(String workflowId, TraceProgressData data);

    void updateStats(String workflowId, Set executors, List terminated);
}
