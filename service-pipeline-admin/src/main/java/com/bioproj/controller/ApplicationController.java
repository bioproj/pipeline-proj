package com.bioproj.controller;

import com.bioproj.domain.vo.ApplicationVo;
import com.bioproj.pojo.Application;
import com.bioproj.domain.params.WorkflowParams;
import com.bioproj.pojo.Samples;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.pojo.Repos;
import com.bioproj.pojo.vo.ApplicationExVo;
import com.bioproj.rpc.ApplicationFeignService;
import com.bioproj.service.*;
import com.bioproj.service.executor.IExecutorsService;
import com.bioproj.domain.BaseResponse;
import com.bioproj.service.store.IStoreService;
import com.bioproj.utils.FileUtils;
import com.mbiolance.cloud.auth.common.SysUserInfoContext;
import com.mbiolance.cloud.auth.common.SystemRuntimeException;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.R;
import com.mbiolance.cloud.auth.domain.dto.SysUserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/application")
@Slf4j
@Api(tags={"应用"})
public class ApplicationController implements ApplicationFeignService {


    @Autowired
    private IApplicationService appService;

    @Autowired
    private IProcessService processService;

    @Autowired
    IStoreService wareHouseService;

    @Autowired
    IWorkflowService workflowService;


    @Autowired
    @Qualifier("slurmExecutorImpl")
    IExecutorsService executorsService;
    @GetMapping("/install/{repoId}")
    @ApiOperation("安装应用")
    public BaseResponse install(@PathVariable("repoId") String repoId){
        SysUserDto user = SysUserInfoContext.getUser();
        Repos repos = wareHouseService.findById(repoId);
        Application application = appService.findByUserAndRepoId(user,repos.getId());
        if(application!=null){
            throw new SystemRuntimeException("用户["+user.getLoginName()+"]已经安装应用["+repos.getCloneUrl()+"]！");
        }
        application = new Application();
        application.setRepoId(repos.getId());
        application.setPipeline(repos.getCloneUrl());
        application.setName(repos.getName());
        application.setDateCreated(new Date());
        application.setUserId(user.getId());
        application.setWorkflowType(repos.getWorkflowType());
        appService.save(application);

        return BaseResponse.ok(application);
    }

    @PutMapping("/createTestApp")
    @ApiOperation("新增")
    public BaseResponse createTestApp(@RequestBody Application app){
        app.setId(null);
        app.setDateCreated(new Date());
//        app.setStatus("nascent");
        app.setAttempts(1);
        appService.save(app);
        return BaseResponse.ok(app);
    }

    @PostMapping("")
    @ApiOperation("修改")
    public BaseResponse update(@RequestBody Application appParam){
        String id = appParam.getId();
        if (id == null) {
            return BaseResponse.error("ID为空！");
        }
        Application byId = appService.findById(id);
        if (byId == null) {
            return BaseResponse.error("未查询到数据！");
        }
        Application app = appService.update(id,appParam);
        Path workPath = Paths.get(app.getWorkDir(),app.getId());
//        if(app.getParamsFormat()!=null && app.getParamsData()!=null){
//            Path paramPath = Paths.get(workPath.toString(), "params." + app.getParamsFormat());
//            FileUtils.saveFile(paramPath.toFile(),app.getParamsData());
//        }
        return BaseResponse.ok(app);
    }

    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable("id")String id){
        Application application = appService.findById(id);
        appService.del(application);
        return BaseResponse.ok("删除成功！");
    }
    @ApiOperation("单查")
    @GetMapping("/{id}")
    public BaseResponse id(@PathVariable("id")String id){
        Application application = appService.findById(id);
        ApplicationExVo applicationVo = new ApplicationExVo();
        BeanUtils.copyProperties(application, applicationVo);
        String pipeline = application.getPipeline();
        if(pipeline!=null && pipeline.startsWith("/")){
            Path path = Paths.get(pipeline);
            if(path.toFile().exists()){
                Path config = Paths.get(path.getParent().toString(), "nextflow.config");
                if(config.toFile().exists()){
                    applicationVo.setConfiguration(FileUtils.openFile(config.toFile()));
                }

                Path params = Paths.get(path.getParent().toString(), "params.json");
                if(params.toFile().exists()){
                    applicationVo.setParamsData(FileUtils.openFile(params.toFile()));
                }
            }
        }
        return BaseResponse.ok(applicationVo);
    }

    @ApiOperation("列表")
    @GetMapping("list")
    public BaseResponse list(){
        List<Application> all = appService.findAll();
        return BaseResponse.ok(all);
    }
    @GetMapping("page")
    public R<PageModel<ApplicationVo>> page(Integer number, Integer size) {
        SysUserDto user = SysUserInfoContext.getUser();
        PageModel<ApplicationVo> pageModel = appService.page(user,number, size, null);
        return R.ok(pageModel);
    }
//    @ApiOperation("分页")
//    @GetMapping("page")
//    public R<PageModel<Application>>  page(@RequestParam(defaultValue = "1", required = false) Integer number
//            ,@RequestParam(defaultValue = "20", required = false) Integer size){
//        SysUserDto user = SysUserInfoContext.getUser();
//        number -= 1;
//        number = number <= 0 ? 0 : number;
//        size = (size <= 0) ? 10 : size;
//        Page<Application> page = appService.page(user,PageRequest.of(number,size, Sort.by(Sort.Order.desc("dateCreated"))));
//        List<Application> content = page.getContent();
//
//        PageModel<Application> pageModel = PageModel.<Application>builder()
//                .count((int) page.getTotalElements())
//                .content(content)
//                .number(number+1)
//                .size(page.getSize())
//                .totalPages(page.getTotalPages())
//                .build();
//
//        return R.ok(pageModel);
//    }


    @Override
    public BaseResponse launch(@PathVariable("id") String id, @RequestBody WorkflowParams workflowParams){

        SysUserDto user = SysUserInfoContext.getUser();


        Workflow workflow = new Workflow();
        BeanUtils.copyProperties(workflowParams, workflow);
        Application app = appService.findById(id);
        if(workflow.getIsDebug()){
            Workflow findTask = workflowService.findByUserIdAndIsDebug(user.getId(),app.getId(), true);
            if(findTask!=null){
                throw new SystemRuntimeException("用户["+user.getLoginName()+"]已经创建了应用["+app.getPipeline()+"]的debug任务！");
            }

        }


        BeanUtils.copyProperties(app,workflow,"id" ,"name");
        workflow.setPipelineName(app.getName());
        workflow.setApplicationId(app.getId());

        if(workflow.getIsDebug()==null){
            workflow.setIsDebug(false);
        }


        List<Samples> samplesList = workflowParams.getSamples().stream().map(item -> {
            Samples samples = new Samples();
            BeanUtils.copyProperties(item, samples);
            return samples;
        }).collect(Collectors.toList());

        workflowService.submit(workflow,samplesList, user);
        return BaseResponse.ok("success!");
    }

//    @KafkaListener(topics = "tasks-queue", groupId = "test1")
//    public void listenGroupFoo(String message) {
//        System.out.println("Received Message in group foo: " + message);
//    }



}
