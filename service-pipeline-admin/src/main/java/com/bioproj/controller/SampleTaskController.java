package com.bioproj.controller;

import com.bioproj.domain.BaseResponse;
import com.bioproj.domain.vo.K8sAppVo;
import com.bioproj.domain.vo.WorkflowTaskVo;
import com.bioproj.pojo.task.SampleTask;
import com.bioproj.service.ISampleTaskService;
import com.mbiolance.cloud.auth.common.SysUserInfoContext;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.R;
import com.mbiolance.cloud.auth.domain.dto.SysUserDto;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/sampleTasks")
@Slf4j
@Api(tags={"样本任务列表"})
public class SampleTaskController {
    @Autowired
    ISampleTaskService sampleTaskService;

    @GetMapping("/tag/{tag}")
    public BaseResponse findByTag(@PathVariable("tag") String  tag){

        List<WorkflowTaskVo> task = sampleTaskService.findByTag(tag);
        return BaseResponse.ok(task);
    }
    @GetMapping("/findTagAndWorkflowId")
    public BaseResponse findByTagAndWorkflowId(@RequestParam("tag") String  tag,
                                               @RequestParam("workflowId") String  workflowId){
        WorkflowTaskVo  workflowTaskVo= sampleTaskService.findByTagAndWorkflowId(tag,workflowId);
//        List<WorkflowTaskVo> task = sampleTaskService.findByTag(tag);
        return BaseResponse.ok(workflowTaskVo);
    }


    @GetMapping("/pageBy/{workflowId}")
    public R<PageModel<SampleTask>> pageBy(@PathVariable("workflowId") String workflowId,Integer number, Integer size){
        PageModel<SampleTask>  sampleTaskList = sampleTaskService.pageBy(workflowId,number, size, null);
        return  R.ok(sampleTaskList);
    }

}
