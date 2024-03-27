package com.bioproj.service.impl;

import com.bioproj.domain.SysUserDto;
import com.bioproj.domain.enums.WorkflowType;
import com.bioproj.domain.params.WorkflowParams;
import com.bioproj.k8s.ApiSuccessCallback;
import com.bioproj.k8s.Job;
import com.bioproj.k8s.JobVo;
import com.bioproj.domain.k8s.Mounts;
import com.bioproj.k8s.PodVo;
import com.bioproj.live.LiveEventsService;
import com.bioproj.pojo.Repos;
import com.bioproj.pojo.Samples;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.domain.enums.WorkflowStatus;
import com.bioproj.repository.WorkflowRepository;
import com.bioproj.service.*;
import com.bioproj.service.executor.IExecutorsService;
import com.bioproj.service.store.IStoreService;
import com.bioproj.utils.FileUtils;
import com.bioproj.utils.TaskUtil;
import com.google.common.base.Joiner;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1JobList;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
//@Transactional
public class WorkflowServiceImpl implements IWorkflowService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private IProcessService processService;
    @Value("${nextflow.exec}")
    String nextflowExec;
    @Value("${nextflow.tower-endpoint}")
    String towerEndpoint;
    @Value("${workDir}")
    String workDir;
    @Value("${k8sWorkDir}")
    String k8sWorkDir;
    @Value("${nextflow.tower-token}")
    String towerToken;
    @Value("${send.error}")
    String sendError;
    @Value("${userId}")
    Long userId;
    @Value("${groupId}")
    Long groupId;

    @Autowired
    ITaskService taskService;

    @Value("${nfImage}")
    String nfImage;
    @Autowired
    private WorkflowRepository taskAppRepository;
    @Autowired
    IReportsService reportsService;
    private  static  String namespace = "nextflow";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSXXX");
    @Autowired
    LiveEventsService liveEventsService;
    @Autowired
    ISampleService sampleService;

    @Autowired
    IStoreService storeService;
    @Autowired
    IK8sApiService k8sApiService;

    @Autowired
    @Qualifier("dockerExecutorImpl")
    private IExecutorsService executorsService;
    @Override
    public Workflow save(Workflow app) {
        Workflow save = taskAppRepository.save(app);
        return save;
    }

    @Override
    public Workflow findById(String id) {
        return taskAppRepository.findById(id).orElse(null);
    }
    @Override
    public Workflow findByUserIdAndIsDebug(Integer userId,String applicationId,  Boolean debug) {
//        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("isDebug", ExampleMatcher.GenericPropertyMatchers.exact())
//                .withMatcher("userId", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<Workflow> example = Example.of(Workflow.builder().isDebug(debug).applicationId(applicationId).userId(userId).build());
        List<Workflow> tasks = taskAppRepository.findAll(example);
        if(tasks.size()>0)return tasks.get(0);
        return null;
    }
    @Override
    public Workflow update(String id, Workflow appParam) {
        return taskAppRepository.save(appParam);
    }

    @Override
    public void del(String s) {
        taskAppRepository.deleteById(s);
    }
    @Override
    @Async
    public void del(Workflow workflow) {

        BatchV1Api api = k8sApiService.getJobApi();
//        V1JobList v1JobList = k8sApiService.listJobs(api, namespace, Map.of("workflowId", workflow.getId()));
        k8sApiService.delJobList(api, namespace, Map.of("workflowId", workflow.getId()), new ApiSuccessCallback<V1Status>() {
            @Override
            public void onFinish(V1Status var1, int var2, Map<String, List<String>> var3) {
                String findWorkDir = workflow.getWorkDir();
                String findStoragePath = workflow.getStoragePath();
                if(findWorkDir.startsWith(workDir)){
                    FileUtils.remove(findWorkDir);
                }
                if(findStoragePath.startsWith(k8sWorkDir)){
                    FileUtils.remove(findStoragePath);
                }
                taskService.delByWorkflowId(workflow.getId());
                reportsService.delWorkflowId(workflow.getId());


                //        FileUtils.remove(workDir);
                if(workflow.getIsDebug().equals(true)){
                    taskAppRepository.delete(workflow);
                }else {
                    taskAppRepository.delete(workflow);
                }
            }
        });
        k8sApiService.delPodList(k8sApiService.getApi(),namespace,Map.of("workflowId", workflow.getId()));




//        Boolean status = false; //executorsService.delete(task);
//        if(status){
//
//        }

    }
    @Override
    public List<Workflow> listByWorkflowType(WorkflowType sampleQueue) {
        Workflow workflow = new Workflow();
        workflow.setWorkflowType(WorkflowType.SAMPLE_QUEUE);
//        if(!user.getLoginName().equals("admin")){
//            workflow = Workflow.builder()
//                    .userId(user.getId())
//                    .build();
//        }
        return taskAppRepository.findAll(Example.of(workflow));
    }
    @Override
    public List<Workflow> listByWorkflowType(SysUserDto user, WorkflowType sampleQueue) {
        Workflow workflow = new Workflow();
        workflow.setWorkflowType(WorkflowType.SAMPLE_QUEUE);
        if(!user.getLoginName().equals("admin")){
            workflow = Workflow.builder()
                    .userId(user.getId())
                    .build();
        }
        return taskAppRepository.findAll(Example.of(workflow));
    }
    @Override
    public Iterable<Workflow> findAll() {

        return taskAppRepository.findAll();
    }

    @Override
    public Page<Workflow> page(Pageable pageable) {
        return taskAppRepository.findAll(pageable);
    }
    @Override
    public Page<Workflow> page(Pageable pageable, Boolean isDebug) {
//        ExampleMatcher exampleMatcher = ExampleMatcher.
//                matching().
//                withIgnorePaths("source","companyId","state","flag","isok","updateTime","id","result","registerDate")  //忽略属性
//                .withIgnoreCase(true)//忽略大小写
//                .withMatcher("sender", ExampleMatcher.GenericPropertyMatchers.contains());
//
//        Example<Task> example = Example.of(task, exampleMatcher);
        Example<Workflow> example = Example.of(Workflow.builder().isDebug(isDebug).build());
//                ExampleMatcher.matching()
//                        .withIgnorePaths("_class"));
        return taskAppRepository.findAll(example,pageable);
    }

    @Override
    public Page<Workflow> page(SysUserDto user, Pageable pageable, Boolean isDebug) {
        Workflow workflow = new Workflow();
        workflow.setIsDebug(isDebug);
        if(!user.getLoginName().equals("admin")){
            workflow = Workflow.builder()
                    .userId(user.getId())
                    .isDebug(isDebug)
                    .build();
        }
        return taskAppRepository.findAll(Example.of(workflow),pageable);
    }

    public void initArgs(Workflow workflow){
        Path paramPath = Paths.get(workflow.getWorkDir(), "params.json" );
        workflow.setParamPath(paramPath.toString());
        Path config = Paths.get(workflow.getWorkDir(),"nextflow.config");
        workflow.setConfigPath(config.toString());
        Path run = Paths.get(workflow.getWorkDir(), ".command.sh" );
        workflow.setRunPath(run.toString());
        String runName = TaskUtil.getRunName(workflow);
        workflow.setRunName(runName);
        Path env = Paths.get(workflow.getWorkDir(), ".env" );
        workflow.setEnvPath(env.toString());
        if(!k8sWorkDir.equals("")){
            Path storagePath = Paths.get(k8sWorkDir, workflow.getId());
            workflow.setStoragePath(storagePath.toString());
        }

        List<String> args = new ArrayList<>();
        final String[] argsArray = new String[]{
                nextflowExec, "-log", workflow.getLogPath(), "run", workflow.getPipeline(),  "-latest","-params-file",workflow.getParamPath(),
                "--workflowId",workflow.getId(),"-resume"
//                "-with-tower",towerEndpoint,
        };


        args.addAll(Arrays.asList(argsArray));

        if(workflow.getWorkflowType()==null){
            workflow.setWorkflowType(WorkflowType.ONCE_QUEUE);
        }
        if(workflow.getWorkflowType().equals(WorkflowType.SAMPLE_QUEUE)){
            if(workflow.getTopic()==null || "".equals(workflow.getTopic())){
                workflow.setTopic(workflow.getId());
            }
            args.add("--topic");
            args.add(workflow.getTopic());
        }

        if(workflow.getStoragePath()!=null){
            args.add("-w");
            args.add(workflow.getStoragePath());
        }

        args.addAll(Arrays.asList("|","tee",workflow.getCmdLog()));
        workflow.setCommand(args);

        StringBuilder cmd = new StringBuilder();
        cmd.append(Joiner.on("\n\t").join(workflow.getCommand()));
        workflow.setCommandLine(cmd.toString());
    }

    public void initCommandLine(Workflow task){

//        String commandLine =

        StringBuilder env = new StringBuilder();

        env.append("export NXF_UUID="+task.getSessionId()+"\n");
//        env.append("export TOWER_ACCESS_TOKEN="+towerToken+"\n");
        env.append("export NXF_WORKFLOW_ID="+task.getId());
        task.setEnv(env.toString());


    }

    public void prepareFile(Workflow task){
        Path paramPath = Paths.get(task.getParamPath());
        Path config = Paths.get(task.getConfigPath());


        if(task.getParamsFormat()!=null && task.getParamsData()!=null){
            FileUtils.saveFile(paramPath.toFile(),task.getParamsData());
        }else {
            FileUtils.saveFile(paramPath.toFile(),"{}");
        }


        if( task.getConfiguration()!=null){
            FileUtils.saveFile(config.toFile(),task.getConfiguration());
        }else {
            FileUtils.saveFile(config.toFile(),"");
        }

//        FileUtils.saveFile(run.toFile(),task.getCommandLine());
    }
    public void entrypointFile(Workflow task){
        Path run = Paths.get(task.getRunPath() );
//        Path env = Paths.get(task.getEnvPath() );
//        FileUtils.saveFile(env.toFile(),task.getEnv());
        FileUtils.saveFile(run.toFile(),"#!/bin/sh \n"+task.getEnv()+"\n\n"+task.getCommandLine().replace("\n\t"," "));
    }

    public void sendTask2(Workflow task){
        log.info("提交任务{}到消息队列{}",task.getId(),"tasks-queue");
        kafkaTemplate.send("tasks-queue","submit",task);

//        executorsService.submit(task);
    }
    public void sendTask(Workflow workflow){

//        kafkaTemplate.send("tasks-queue","submit",task);

        BatchV1Api api = k8sApiService.getJobApi();
//        k8sApiService.createNamespace(api,namespace);
//
//        k8sApiService.createPersistentVolume(api);
//
//        k8sApiService.createPersistentVolumeClaim(api,namespace);
//        workflow.setPodName("nf-test");
        Map<String,String> volumes= new HashMap<>();
        Map<String,String> env= new HashMap<>();
        List<Mounts> mounts= new ArrayList<>();
        Map<String, String> labels = new HashMap<>();
        labels.put("workflowId",workflow.getId());

        volumes.put("nfdata","nfdata");
        mounts.add(Mounts.builder().name("nfdata").mountPath("/data").subPath("").build());
        mounts.add(Mounts.builder().name("nfdata").mountPath("/etc/hosts").subPath("user/hosts").build());

        env.put("NXF_HOME","/data/user");
        env.put("KUBECONFIG","/data/user/.kube/config");

//        workflow.setCommand(Arrays.asList("echo","111"));
        Integer ttlSecondsAfterFinished = 60;
        if(workflow.getIsDebug()){
            ttlSecondsAfterFinished = 300;
        }

        k8sApiService.createOrReplaceJob(api,namespace, Job.builder()
                .name(workflow.getPodName())
                .labels(labels)
                .image(nfImage)
                .runGroup(groupId)
                .runUser(userId)
                .restartPolicy("Never")
                .command(Arrays.asList("sh",workflow.getRunPath()))
                .backoffLimit(0)
                .workDir(workflow.getWorkDir())
                .workflowId(workflow.getId())
                .env(env)
                .ttlSecondsAfterFinished(ttlSecondsAfterFinished) // 3600  # 设置完成后保留 1 小时
                .mounts(mounts)
                .volumes(volumes)
                .build());
        log.info("提交任务{}到k8s集群",workflow.getId());
        workflow.setWorkflowStatus(WorkflowStatus.RUNNING);
        save(workflow);
        liveEventsService.publishTaskEvent(workflow,"tasksResult");
//        executorsService.submit(task);
    }

    @Override
    public Workflow createWorkflowFromStore(WorkflowParams workflowParam, SysUserDto user) {
        Workflow workflow = new Workflow();
        workflow.setName(workflowParam.getName());
        Repos repos = storeService.findById(workflowParam.getTemplateId());
        Path sourcePath = Paths.get(repos.getCloneUrl());
        if(!sourcePath.toFile().exists()){
            throw new RuntimeException("文件【"+sourcePath+"]不存在！");
        }
        String parentPath = sourcePath.toFile().getParent();
        String userWorkspace = "/data/workspace/"+user.getId()+"/pipeline/"+workflowParam.getName();
        List<String> list = Arrays.asList("main.nf","lib","modules",".gitignore",".nf-core.yml","modules.json","nextflow.config","params.json","README.md","tower.yml");
        FileUtils.copy(parentPath,userWorkspace,list,parentPath+"/");

        workflow.setPipeline(userWorkspace+"/main.nf");
        workflow.setCreateDate(new Date());
        workflow.setUserId(user.getId());
        workflow.setIsDebug(true);
        List<Samples> samples = new ArrayList<>();



        submit(workflow,samples,user);
        return workflow;
    }
    @Override
    public Workflow submit(Workflow workflow, List<Samples> samples , SysUserDto user) {
        String taskApp_Id = UUID.randomUUID().toString();
        workflow.setId(taskApp_Id);
        workflow.setSessionId(taskApp_Id);
        workflow.setUserId(user.getId());
//        if(taskApp)
        if(workflow.getIsDebug()!=null && workflow.getIsDebug()){
            String pipeline = workflow.getPipeline();
            Path path = Paths.get(pipeline);
            workflow.setWorkDir(path.getParent().toString());
        }else {
            String work = workDir+ File.separator+taskApp_Id;
            workflow.setWorkDir(work);
        }

        workflow.setCreateDate(new Date());

        workflow.setCreateDate(new Date());
        String logPath = workflow.getWorkDir()+ File.separator  +"output"+ File.separator+ "nextflow.log";
        workflow.setLogPath(logPath);
        String outputDir = workflow.getWorkDir() + File.separator +"output";
        workflow.setOutputDir(outputDir);
        String cmdLog = workflow.getWorkDir() + File.separator + ".workflow.log";
        workflow.setCmdLog(cmdLog);

        String tracePath = workflow.getWorkDir() + File.separator + "trace.txt";
        workflow.setTracePath(tracePath);
        String timelinePath = workflow.getWorkDir() + File.separator + "timeline.html";
        workflow.setTimelinePath(timelinePath);
        String reportPath = workflow.getWorkDir() + File.separator + "report.html";
        workflow.setReportPath(reportPath);


        if(workflow.getPodName()==null){
            workflow.setPodName("nf-job-"+workflow.getId());
        }


        workflow.setWorkflowStatus(WorkflowStatus.CREATED);
        workflow.setAttempts(1);


        String runName = TaskUtil.getRunName(workflow);
        workflow.setRunName(runName);

        initArgs(workflow);

        initCommandLine(workflow);
        if(!workflow.getIsDebug()){
            prepareFile(workflow);
        }
        entrypointFile(workflow);
        workflow = taskAppRepository.save(workflow);


        SysUserDto finalUser = user;
        Workflow finalWorkflow = workflow;
        samples= samples.stream().map(sample -> {
            sample.setWorkflowId(finalWorkflow.getId());
            sample.setUserId(finalUser.getId());
            return sample;
        }).collect(Collectors.toList());
        sampleService.saveAll(samples);

        if(!workflow.getIsDebug()){
            sendTask(workflow);
        }

//        if(workflow.getIsDebug()){
//            kafkaTemplate.send("tasks-queue","write",workflow);
//        }else {
//            sendTask(workflow);
//        }
//        processService.locaLaunch(app, args);
        return workflow;
    }

    @Override
    public Workflow resume(Workflow workflow, SysUserDto user){
        if(workflow.getSubmitId()!=null && !workflow.getSubmitId().equals("")){
            Boolean status = executorsService.status(workflow.getSubmitId());
            if(status){
                throw new RuntimeException("任务已经运行不能再运行!");
            }
        }
        workflow.setUserId(user.getId());

        if(workflow.getPodName()==null){
            workflow.setPodName("nf-job-"+workflow.getId());
        }


//        TaskApp app = new TaskApp();
//        BeanUtils.copyProperties(findApp, app,"id");
        workflow.setCreateDate(new Date());
        workflow.setFinishDate(null);
//        if(!task.getIsDebug()){
//            if (task.getTaskStatus().equals(TaskStatus.RUNNING) || task.getTaskStatus().equals(TaskStatus.CREATED)) {
//                throw new RuntimeException("当前任务已经在运行了！");
//            }
//            task.setTaskStatus(TaskStatus.RUNNING);
//        }



        workflow.setDateSubmitted(new Date());
        if(workflow.getAttempts()==null){
            workflow.setAttempts(1);
        }else {
            workflow.setAttempts(workflow.getAttempts()+1);
        }
//
//        if(!Paths.get(workflow.getRunPath() ).toFile().exists()){
//
//        }
        entrypointFile(workflow);


//        final String[] args = new String[]{
//                nextflowExec, "-log", task.getLogPath(), "run", task.getPipeline(),  "-latest","-resume",task.getSessionId(),"-params-file",task.getParamPath(),
//                "-with-tower",towerEndpoint,"--taskId",task.getId()
//        };

//        initArgs(task,args);
        initArgs(workflow);
        if(!workflow.getIsDebug()){
            prepareFile(workflow);
        }

        // 删除publish的文件
//        if(task.getIsDebug()){
////            kafkaTemplate.send("tasks-queue","move",task);
//        }else

//        List<Reports> reportsList = reportsService.findByTaskId(task.getId());
//        if (task.getReports()!=null){
//        StringBuilder sb = new StringBuilder();
//        for (Reports item : reportsList){
//            sb.append("mv "+item.getDestination()+" "+item.getDestination()+"."+task.getAttempts()+"\n");
//        }
//        executorsService.runScript(task,sb.toString());
//            List<Reports> reports = task.getReports();
//            for (Reports item : reports){
//                FileUtils.move(item.getDestination(),item.getDestination()+"."+task.getAttempts());
//            }
//        }
//        task = save(task);
//        processService.locaLaunch(task, args);
//        if(!task.getIsDebug()) {
        sendTask(workflow);
//        }

        return workflow;
    }

//    @Override
    public Workflow cancel1(Workflow app) {
//        processService.cancel(app);
        if(app.getSubmitId()!=null && !app.getSubmitId().equals("")){
            Boolean cancelStatue =false;
            try {
                cancelStatue = executorsService.cancel(app.getSubmitId());
            } catch (Exception e) {
                app.setWorkflowStatus(WorkflowStatus.FAILED);
                app.setSubmitId(null);
                app = update(app.getId(), app);
                e.printStackTrace();
            }
            if(cancelStatue){
                app.setWorkflowStatus(WorkflowStatus.FAILED);
                app.setSubmitId(null);
                app = update(app.getId(), app);
            }
            return app;
        } else if (app.getWorkflowStatus().equals(WorkflowStatus.RUNNING)) {
            app.setWorkflowStatus(WorkflowStatus.FAILED);
            app.setSubmitId(null);
            app = update(app.getId(), app);
            return app;
        }
        throw new RuntimeException("运行ID不存在，不能停止！");
    }
    @Override
    public Workflow cancel(final Workflow workflow) {
        return cancel(workflow,false);
    }
    @Override
    public Workflow cancel(final Workflow workflow, final Boolean isForce) {
        BatchV1Api api = k8sApiService.getJobApi();
//        V1JobList v1JobList = k8sApiService.listJobs(api, namespace, Map.of("workflowId", workflow.getId()));
        k8sApiService.delJobList(api, namespace, Map.of("workflowId", workflow.getId()), new ApiSuccessCallback<V1Status>() {
            @Override
            public void onFinish(V1Status var1, int var2, Map<String, List<String>> var3) {
                workflow.setWorkflowStatus(WorkflowStatus.FAILED);
                update(workflow.getId(), workflow);
            }
        });

        if(isForce){
            k8sApiService.delPodList(k8sApiService.getApi(),namespace,Map.of("workflowId", workflow.getId()));
        }
        return workflow;



//        processService.cancel(app);
//        if(app.getSubmitId()!=null && !app.getSubmitId().equals("")){
//            Boolean cancelStatue =false;
//            try {
//                cancelStatue = executorsService.cancel(app.getSubmitId());
//            } catch (Exception e) {
//                app.setWorkflowStatus(WorkflowStatus.FAILED);
//                app.setSubmitId(null);
//                app = update(app.getId(), app);
//                e.printStackTrace();
//            }
//            if(cancelStatue){
//                app.setWorkflowStatus(WorkflowStatus.FAILED);
//                app.setSubmitId(null);
//                app = update(app.getId(), app);
//            }
//            return app;
//        } else if (app.getWorkflowStatus().equals(WorkflowStatus.RUNNING)) {
//            app.setWorkflowStatus(WorkflowStatus.FAILED);
//            app.setSubmitId(null);
//            app = update(app.getId(), app);
//            return app;
//        }
//        throw new RuntimeException("运行ID不存在，不能停止！");
    }


    @Override
    public String openScript(String pathStr){
        Path path = Paths.get(pathStr);
        if(path.toFile().exists()){
            return FileUtils.openFile(path.toFile());
        }
        return "";
    }



    @Override
    public List<JobVo> listJobs(String workflowId){
        BatchV1Api api = k8sApiService.getJobApi();
        V1JobList v1JobList = k8sApiService.listJobs(api, namespace, Map.of("workflowId", workflowId));
        List<JobVo> jobVoList = v1JobList.getItems().stream().map(item -> {
            JobVo jobVo = new JobVo();
            OffsetDateTime creationTimestamp = item.getMetadata().getCreationTimestamp();
            jobVo.setCreationTimestamp(creationTimestamp.format(formatter));
            jobVo.setTaskName(item.getMetadata().getLabels().get("nextflow.io/taskName"));
            jobVo.setProcessName(item.getMetadata().getLabels().get("nextflow.io/processName"));
            jobVo.setName(item.getMetadata().getName());
            List<V1Container> containers = item.getSpec().getTemplate().getSpec().getContainers();
            jobVo.setImages(containers.get(0).getImage());
            Integer ttlSecondsAfterFinished = item.getSpec().getTtlSecondsAfterFinished();
            jobVo.setTtlSecondsAfterFinished(ttlSecondsAfterFinished);

            return jobVo;
        }).collect(Collectors.toList());
        return jobVoList;

    }

    @Override
    public List<PodVo> listPods(String workflowId){
        CoreV1Api api = k8sApiService.getApi();
        V1PodList v1PodList = k8sApiService.listPods(api, namespace, Map.of("workflowId", workflowId));
        List<PodVo> podVoList = v1PodList.getItems().stream().map(item -> {
            PodVo podVo = new PodVo();
            OffsetDateTime creationTimestamp = item.getMetadata().getCreationTimestamp();
            podVo.setCreationTimestamp(creationTimestamp.format(formatter));
            podVo.setTaskName(item.getMetadata().getLabels().get("nextflow.io/taskName"));
            podVo.setProcessName(item.getMetadata().getLabels().get("nextflow.io/processName"));
            podVo.setName(item.getMetadata().getName());
            List<V1Container> containers = item.getSpec().getContainers();
            podVo.setImages(containers.get(0).getImage());
            podVo.setJobName(item.getMetadata().getLabels().get("job-name"));
            podVo.setSessionId(item.getMetadata().getLabels().get("nextflow.io/sessionId"));
            podVo.setRunName(item.getMetadata().getLabels().get("nextflow.io/runName"));

            return podVo;
        }).collect(Collectors.toList());
        return podVoList;

    }


}

