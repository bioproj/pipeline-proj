package com.bioproj.controller;

import com.bioproj.domain.PageModel;
import com.bioproj.domain.R;
import com.bioproj.domain.SysUserDto;
import com.bioproj.domain.enums.K8sStatus;
import com.bioproj.domain.enums.WorkflowType;
import com.bioproj.domain.vo.K8sAppVo;
import com.bioproj.domain.vo.WorkflowDto;
import com.bioproj.k8s.JobVo;
import com.bioproj.k8s.PodVo;
import com.bioproj.pojo.*;
import com.bioproj.domain.params.WorkflowParams;

import com.bioproj.domain.vo.TaskVo;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.pojo.vo.WorkflowVo;
import com.bioproj.repository.AppRepository;
import com.bioproj.service.*;
import com.bioproj.pojo.BaseResponse;
import com.bioproj.utils.FileUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/workflow")
@Slf4j
@Api(tags={"应用任务"})
public class WorkflowController {


    @Autowired
    private IWorkflowService taskAppService;



    @Autowired
    private AppRepository appRepository;


    @Autowired
    IReportsService reportsService;

    @Autowired
    ITaskService taskService;

    @Autowired
    ISampleService sampleService;

    @Autowired
    IK8sAppService k8sAppService;



    @PostMapping("")
    @ApiOperation("修改")
    public BaseResponse update(@RequestBody Workflow appParam){
//        String id = appParam.getId();
//        if (id == null) {
//            return BaseResponse.error("ID为空！");
//        }
//        Task byId = taskAppService.findById(id);
//        if (byId == null) {
//            return BaseResponse.error("未查询到数据！");
//        }
//        Task app = taskAppService.update(id,appParam);
//        Path workPath = Paths.get(app.getWorkDir(),app.getId());
//        if(app.getParamsFormat()!=null && app.getParamsData()!=null){
//            Path paramPath = Paths.get(workPath.toString(), "params." + app.getParamsFormat());
//            FileUtils.saveFile(paramPath.toFile(),app.getParamsData());
//        }
//        return BaseResponse.ok(app);
        return null;
    }

    @ApiOperation("重试不创建新任务")
    @PostMapping("/{id}/retry")
    public BaseResponse retry(@PathVariable("id") String id, @RequestBody WorkflowParams workflowParams){
//        Workflow workflow = new Workflow();

        SysUserDto user = new SysUserDto();//SysUserInfoContext.getUser();

        Workflow workflow = taskAppService.findById(id);
        workflow.setParamsData(workflowParams.getParamsData());
        workflow.setConfiguration(workflowParams.getConfiguration());
        workflow.setWorkflowType(workflowParams.getWorkflowType());
        workflow.setTopic(workflowParams.getTopic());
        workflow.setResultDir(workflowParams.getResultDir());
//        workflow.setIsDeleteHistory(workflowParams.getIsDeleteHistory());
//        workflow.setSamples(task.getSamples());

        sampleService.delByWorkflowId(workflow.getId());


//        SysUserDto finalUser = user;
        List<Samples> samplesList = workflowParams.getSamples().stream().map(item -> {
            Samples samples = new Samples();
//            samples.setWorkflowId(workflow.getId());
//            samples.setUserId(finalUser.getId());
            BeanUtils.copyProperties(item, samples);
            samples.setWorkflowId(workflow.getId());
            samples.setUserId(user.getId());
            return samples;
        }).collect(Collectors.toList());

//        List<Samples> samples = workflowParams.getSamples();
//        SysUserDto finalUser = user;
//        samples= samples.stream().map(sample -> {
//            sample.setWorkflowId(workflow.getId());
//            sample.setUserId(finalUser.getId());
//            return sample;
//        }).collect(Collectors.toList());

        sampleService.saveAll(samplesList);



        try {
            taskAppService.resume(workflow, user);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.error("任务已经运行！");
        }
        return BaseResponse.ok("success!");
    }
    @ApiOperation("重试并创建新任务")
    @PostMapping("/{id}/resume")
    public BaseResponse resume(@PathVariable("id") String id,@RequestBody Workflow task){
        SysUserDto user = new SysUserDto();//SysUserInfoContext.getUser();

        Workflow findApp = taskAppService.findById(id);
        findApp.setParamsData(task.getParamsData());
        findApp.setConfiguration(task.getConfiguration());
        taskAppService.resume(findApp, user);
        return BaseResponse.ok("success!");
    }


    @ApiOperation("停用")
    @GetMapping("/{id}/cancel")
    public BaseResponse cancel(@PathVariable("id") String id){
        Workflow app = taskAppService.findById(id);
        try {
            taskAppService.cancel(app);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.error("11运行ID不存在，不能停止！");
        }
        return BaseResponse.ok("success!");
    }

    @ApiOperation("列表")
    @GetMapping("list")
    public BaseResponse list(){
        Iterable<Workflow> all = taskAppService.findAll();
        return BaseResponse.ok(all);
    }
    @ApiOperation("分页")
    @GetMapping("page")
    public R<PageModel<Workflow>>  page(@RequestParam(defaultValue = "1", required = false) Integer number
            , @RequestParam(defaultValue = "20", required = false) Integer size){
        SysUserDto user = new SysUserDto();//SysUserInfoContext.getUser();
        number -= 1;
        number = number <= 0 ? 0 : number;
        size = (size <= 0) ? 10 : size;
        PageRequest pageRequest = PageRequest.of(number, size, Sort.by(Sort.Order.desc("createDate")));
        Page<Workflow> page = taskAppService.page(user,pageRequest,false);
        List<Workflow> content = page.getContent();

        PageModel<Workflow> pageModel = PageModel.<Workflow>builder()
                .count((int) page.getTotalElements())
                .content(content)
                .number(number+1)
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .build();

        return R.ok(pageModel);
    }
    @ApiOperation("分页")
    @GetMapping("pageDebug")
    public R<PageModel<Workflow>>  pageDebug(@RequestParam(defaultValue = "1", required = false) Integer number
            , @RequestParam(defaultValue = "20", required = false) Integer size){
        SysUserDto user = new SysUserDto();//SysUserInfoContext.getUser();
        number -= 1;
        number = number <= 0 ? 0 : number;
        size = (size <= 0) ? 10 : size;
        Page<Workflow> page = taskAppService.page(user,PageRequest.of(number,size, Sort.by(Sort.Order.desc("createDate"))),true);
        List<Workflow> content = page.getContent();

        PageModel<Workflow> pageModel = PageModel.<Workflow>builder()
                .count((int) page.getTotalElements())
                .content(content)
                .number(number+1)
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .build();

        return R.ok(pageModel);
    }
    @ApiOperation("单查")
    @GetMapping("/{id}")
    public BaseResponse id(@PathVariable("id")String id){
        SysUserDto user = new SysUserDto();//SysUserInfoContext.getUser();

        Workflow workflow = taskAppService.findById(id);
        WorkflowVo workflowVo = new WorkflowVo();
        BeanUtils.copyProperties(workflow, workflowVo);

        String pipeline = workflow.getPipeline();
        if(pipeline!=null && pipeline.startsWith("/") && workflow.getIsDebug()){
            String configPath = workflow.getConfigPath();
            String paramPath = workflow.getParamPath();

            Path config = Paths.get(configPath);
            if(config.toFile().exists()){
                workflowVo.setConfiguration(FileUtils.openFile(config.toFile()));
            }

            Path params = Paths.get(paramPath);
            if(params.toFile().exists()){
                workflowVo.setParamsData(FileUtils.openFile(params.toFile()));
            }
        }

        workflowVo.setScript(taskAppService.openScript(workflow.getPipeline()));
        Page<Reports> reports = reportsService.pageByWorkflowId(workflow.getId(), PageRequest.of(0, 100));
        Page<TaskVo> tasks = taskService.pageByWorkflowId(workflow.getId(), PageRequest.of(0, 100));
        List<Samples> samples = sampleService.findByWorkflowId(workflow.getId());

//        List<Task> taskProcesses = taskProcessService.findByTaskId(task.getId());
//        taskVo.setTaskProcesses(taskProcesses);
        workflowVo.setTasks(tasks);
        workflowVo.setReports(reports);
        workflowVo.setSamples(samples);
        List<K8sAppVo> k8sAppVos = k8sAppService.listByUserId(user.getId(), K8sStatus.RUNNING);
        workflowVo.setK8sAppVo(k8sAppVos);
        return BaseResponse.ok(workflowVo);
    }
    @GetMapping("/{id}/log")
    public BaseResponse WorkflowLogHandler(@PathVariable("id") String id){
        Workflow taskApp = taskAppService.findById(id);
        Path logFile = Paths.get(taskApp.getLogPath());
        String logStr = "";
        if(logFile.toFile().exists()){
            logStr = FileUtils.openFile(logFile.toFile());
        }
        Log log = new Log();
        log.setLog(logStr);
        log.setTaskStatus(taskApp.getWorkflowStatus
                ());
//        log.setAttempts(workflows.getAttempts());
//        log.setStatus(workflows.getStatus());
//        log.setId(workflows.getId());
        return BaseResponse.ok(log);
    }
    @GetMapping("/{id}/cmdLog")
    public BaseResponse WorkflowCmdLogHandler(@PathVariable("id") String id){
        Workflow taskApp = taskAppService.findById(id);
        Path logFile = Paths.get(taskApp.getCmdLog());
        String logStr = "";
        if(logFile.toFile().exists()){
            logStr = FileUtils.openFile(logFile.toFile());
        }
        Log log = new Log();
        log.setLog(logStr);
        log.setTaskStatus(taskApp.getWorkflowStatus());
//        log.setAttempts(workflows.getAttempts());
//        log.setStatus(workflows.getStatus());
//        log.setId(workflows.getId());
        return BaseResponse.ok(log);
    }
    @GetMapping("/{id}/trace")
    public BaseResponse trace(@PathVariable("id") String id){
        Workflow taskApp = taskAppService.findById(id);
        Path file = Paths.get(taskApp.getTracePath());
        String content = "";
        if(file.toFile().exists()){
            content = FileUtils.openFile(file.toFile());
        }
        return BaseResponse.ok(content);
    }


    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable("id")String id){
        Workflow task = taskAppService.findById(id);
        taskAppService.del(task);
        return BaseResponse.ok("删除成功！");
    }


//    @ApiOperation("查找样本")
//    @GetMapping("/samples/{id}")
//    public BaseResponse samples(@PathVariable("id")String id){
//        Workflow task = taskAppService.findById(id);
//        if (task==null){
//            throw new RuntimeException("任务不存在");
//        }
//        List<Sample> samples = task.getSamples();
//        List<SampleVo> sampleVos = samples.stream().map(sample -> {
//            SampleVo sampleVo = new SampleVo();
//            BeanUtils.copyProperties(sample, sampleVo);
//            sampleVo.setTaskId(task.getId());
//            return sampleVo;
//        }).collect(Collectors.toList());
//        return BaseResponse.ok(sampleVos);
//    }

    @ApiOperation("listJobs")
    @GetMapping("/listJobs/{id}")
    public BaseResponse listJobs(@PathVariable("id")String workflowId){
        List<JobVo> jobVos = taskAppService.listJobs(workflowId);
        return BaseResponse.ok(jobVos);
    }
    @ApiOperation("listPods")
    @GetMapping("/listPods/{id}")
    public BaseResponse listPods(@PathVariable("id")String workflowId){
        List<PodVo> podVos = taskAppService.listPods(workflowId);
        return BaseResponse.ok(podVos);
    }

    @ApiOperation("listPodsJobs")
    @GetMapping("/listPodsJobs/{id}")
    public BaseResponse listPodsJobs(@PathVariable("id")String workflowId){
        List<PodVo> podVos = taskAppService.listPods(workflowId);
        List<JobVo> jobVos = taskAppService.listJobs(workflowId);

        return BaseResponse.ok(Map.of("pod",podVos,"job",jobVos));
    }


    @ApiOperation("新增")
    @PutMapping
    public BaseResponse add(@RequestBody Workflow workflow){
        workflow.setId(null);
        taskAppService.save(workflow);
        return BaseResponse.ok("新增成功!");
    }

    @ApiOperation("从商店模板创建工作流")
    @PostMapping("/createWorkflowFromStore")
    public BaseResponse createWorkflowFromStore(@RequestBody WorkflowParams workflowParam){
        SysUserDto user = new SysUserDto();// SysUserInfoContext.getUser();
//        Repos repos = storeService.findById(templateId);
//
//        workflow.setId(null);
//        taskAppService.save(workflow);
        Workflow workflow = taskAppService.createWorkflowFromStore(workflowParam,user);
        return BaseResponse.ok("新增成功!");
    }

    @ApiOperation("获取样本队列的工作流")
    @GetMapping("/listSampleQueueWorkflow")
    public BaseResponse listSampleQueueWorkflow(){
        SysUserDto user = new SysUserDto();//SysUserInfoContext.getUser();
//        List<Workflow> workflowList = taskAppService.listByWorkflowType(user, WorkflowType.SAMPLE_QUEUE);
        List<Workflow> workflowList = taskAppService.listByWorkflowType( WorkflowType.SAMPLE_QUEUE);
        List<WorkflowDto> workflowDtos = workflowList.stream().map(item -> {
            WorkflowDto workflowDto = new WorkflowDto();
            BeanUtils.copyProperties(item, workflowDto);
            return workflowDto;
        }).collect(Collectors.toList());
        return BaseResponse.ok(workflowDtos);
    }
}
