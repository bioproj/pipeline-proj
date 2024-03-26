package com.bioproj.service;

import com.bioproj.domain.vo.WorkflowTaskVo;
import com.bioproj.pojo.task.Task;
import com.bioproj.pojo.task.TaskData;
import com.bioproj.domain.vo.TaskVo;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.trace.model.TraceTaskData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ITaskService {




//    List<Task> findByTaskId(String id);

    Task save(Task dbTask);


    List<Task> findByWorkflowId(String workflowId);

    List<Task> delByWorkflowId(String workflowId);

    Task findById(String taskId);

    Task save(Workflow workflow, Task task, TaskData taskData);
    Task save(Workflow workflow, TraceTaskData traceTaskData);

    List<WorkflowTaskVo> findByTaskTag(String tag);

    Task findByWorkflowAndTaskTag(String workflowId, String tag);

    Page<TaskVo> pageByWorkflowId(String id, PageRequest of);



}
