package com.bioproj.trace.progress;

import com.bioproj.trace.model.TraceProgressData;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocalStatsStore implements ProgressStore{
    Map<String, TraceProgressData> traceData = new ConcurrentHashMap<>();
    Map<String, Instant> workflowLastModified = new ConcurrentHashMap<>();

    @Override
    public void putTraceData(String workflowId, TraceProgressData data) {
        traceData.put(workflowId, data);
        workflowLastModified.put(workflowId, Instant.now());
    }

    @Override
    public void updateStats(String workflowId, Set executors, List terminated) {
//        def current = workflowLoadMap.computeIfAbsent(workflowId, CREATE_LOAD_RECORD)
//        synchronized (workflowLoadMap) {
//            for( String it : executorNames )
//                current.addExecutor(it)
//
//            for( Task it : tasks )
//                current.incStats(it)
//        }
    }
}
