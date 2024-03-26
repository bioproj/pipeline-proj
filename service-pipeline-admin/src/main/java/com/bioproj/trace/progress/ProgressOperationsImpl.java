package com.bioproj.trace.progress;

import com.bioproj.domain.ProcessesProgress;
import com.bioproj.pojo.task.Task;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.domain.WorkflowProgress;
import com.bioproj.service.IWorkflowService;
import com.bioproj.trace.model.TraceProgressData;
import com.bioproj.trace.model.TraceProgressDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProgressOperationsImpl implements ProgressOperations {

    @Autowired
    ProgressStore store;
    @Autowired
    IWorkflowService workflowService;

    @Override
    public void updateProgress(String workflowId, TraceProgressData empty) {
        store.putTraceData(workflowId, empty);
    }

    @Override
    public void aggregateMetrics(String workflowId, List<Task> tasks) {
        final Set executors = new HashSet();
        final List terminated = new ArrayList(tasks.size());

//        for( Task it : tasks ) {
//            executors.add(it.executor);
//            if( it.status.terminal )
//                terminated.add(it);
//        }
        store.updateStats(workflowId, executors, terminated);
    }

    @Override
    public void create(Workflow workflow, TraceProgressData traceProgressData) {

    }

    @Override
    public void updateProgress(Workflow workflow, TraceProgressData traceProgressData) {
        List<TraceProgressDetail> progressDetailList = traceProgressData.getProcesses();
        List<ProcessesProgress> progresses = progressDetailList.stream().map(item -> {
            ProcessesProgress processesProgress = new ProcessesProgress();
            BeanUtils.copyProperties(item, processesProgress);
            return processesProgress;
        }).collect(Collectors.toList());
        workflow.setProcessesProgresses(progresses);
        WorkflowProgress workflowProgress = new WorkflowProgress();
        BeanUtils.copyProperties(traceProgressData,workflowProgress);
        workflow.setWorkflowProgress(workflowProgress);
        workflowService.save(workflow);
        log.info("更新workflow{}!",workflow.getId());

    }


}
