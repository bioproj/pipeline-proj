package com.bioproj.service;

import com.bioproj.domain.vo.K8sAppVo;
import com.bioproj.domain.vo.WorkflowTaskVo;
import com.bioproj.pojo.task.SampleTask;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.trace.model.TraceTaskData;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.dto.SysUserDto;
import com.mbiolance.cloud.auth.domain.vo.QueryCriteriaVo;

import java.util.List;

public interface ISampleTaskService {
    SampleTask save(Workflow workflow, TraceTaskData traceTaskData);

    List<WorkflowTaskVo> findByTag(String tag);

    List<SampleTask> findByWorkflowId(String workflowId);
    PageModel<SampleTask> pageBy(String workflowId, Integer number, Integer size, List<QueryCriteriaVo> criteriaVos);

    WorkflowTaskVo findByTagAndWorkflowId(String tag, String workflowId);
}
