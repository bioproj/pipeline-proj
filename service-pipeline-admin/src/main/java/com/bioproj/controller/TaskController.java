package com.bioproj.controller;

import com.bioproj.pojo.BaseResponse;
import com.bioproj.domain.vo.WorkflowTaskVo;
import com.bioproj.pojo.task.Task;
import com.bioproj.service.ITaskService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@Slf4j
@Api(tags={"任务列表"})
public class TaskController {

    @Autowired
    ITaskService taskService;

    @GetMapping("/workflowTag/{workflowId}/{tag}")
    public BaseResponse findByWorkflowTag(@PathVariable("workflowId") String  workflowId,@PathVariable("tag") String  tag){
        Task task = taskService.findByWorkflowAndTaskTag(workflowId,tag);
        return BaseResponse.ok(task);
    }

    @GetMapping("/tag/{tag}")
    public BaseResponse findByTag(@PathVariable("tag") String  tag){

        List<WorkflowTaskVo> task = taskService.findByTaskTag(tag);
        return BaseResponse.ok(task);
    }
}

