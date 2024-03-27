package com.bioproj.service;

import com.bioproj.domain.SysUserDto;
import com.bioproj.domain.enums.WorkflowType;
import com.bioproj.k8s.JobVo;
import com.bioproj.k8s.PodVo;
import com.bioproj.pojo.Samples;
import com.bioproj.pojo.task.Workflow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.bioproj.domain.params.WorkflowParams;
import java.util.List;

public interface IWorkflowService {

    Workflow save(Workflow workflows);

    Workflow findById(String id);

    Workflow findByUserIdAndIsDebug(Integer userId,String applicationId, Boolean debug);

    Workflow update(String id, Workflow workflowsParam);

    void del(String s);

    void del(Workflow task);

    Iterable<Workflow> findAll();

    Page<Workflow> page(Pageable pageable);

    Page<Workflow> page(Pageable pageable, Boolean isDebug);
    Page<Workflow> page(SysUserDto user,Pageable pageable, Boolean isDebug);

    Workflow submit(Workflow task, List<Samples> samples , SysUserDto user);

    Workflow resume(Workflow task, SysUserDto user);

    Workflow cancel(Workflow app);

    Workflow cancel(Workflow workflow, Boolean isForce);

    String openScript(String pathStr);

    List<JobVo> listJobs(String workflowId);

    List<PodVo> listPods(String workflowId);

    Workflow createWorkflowFromStore(WorkflowParams workflowParam, SysUserDto user);

    List<Workflow> listByWorkflowType(WorkflowType sampleQueue);

    List<Workflow> listByWorkflowType(SysUserDto user, WorkflowType sampleQueue);
}
