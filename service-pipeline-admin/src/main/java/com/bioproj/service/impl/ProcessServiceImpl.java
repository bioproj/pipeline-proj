package com.bioproj.service.impl;

import cn.hutool.json.JSONObject;
import com.bioproj.pojo.Application;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.pojo.Workflows;
import com.bioproj.domain.enums.WorkflowStatus;
import com.bioproj.service.IProcessService;
import com.bioproj.service.IWorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;


@Service
@Slf4j
public class ProcessServiceImpl implements IProcessService {




    @Autowired
    @Lazy
    private IWorkflowService taskAppService;

    @Resource(name = "restTemplate")
    private RestTemplate restTemplate;

    @Value("${fluxServerUrl}")
    String fluxServerUrl;
    @Value("${nextflow.tower-token}")
    String towerToken;


    @Autowired
    private  ThreadPoolTaskExecutor executorService;
    public String getRunName(Workflows workflows){
        return "workflow-"+workflows.getId()+"-"+workflows.getAttempts();
    }

    public String getRunName(Application app){
        return "App-"+app.getId()+"-"+app.getAttempts();
    }



    @Override
    @Async("taskExecutor")
    public void launchAndResume(Workflows workflows, Integer resume) {
        try {
            String runName = getRunName(workflows);
            Path workPath = Paths.get(workflows.getWorkDir(),workflows.getId());
            if(!workPath.toFile().exists()){
                Files.createDirectories(workPath);
            }
            String cmdLog = workPath+ File.separator+".workflow.log";
            String logPath = workflows.getOutputDir()+File.separator+"nextflow.log";
            String[] args  = null;
            if (resume == 1) {
                args  = new String[]{
                        "/home/wangyang/bin/nf","-log",logPath,"run",workflows.getPipeline(),"-name",runName,"-profile",workflows.getProfiles(),"-resume"
                };
            }else if (resume == 2) {
                args  = new String[]{
                        "/home/wangyang/bin/nf","-log",logPath,"run",workflows.getPipeline(),"-name",runName,"-profile",workflows.getProfiles()
                };
            }else if (resume == 3){
                args  = new String[]{
                        "/usr/bin/kill","-9",String.valueOf(workflows.getPid())
                };
            }
            OutputStream logStream=null;
            try {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.directory(workPath.toFile());
                processBuilder.command(args);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                long pid = process.exitValue();
                if (resume == 1 || resume == 2) {
                    log.info(workPath.toString()+", pid:"+pid);
                    log.info("{}: saving workflow pid...",pid);
                    workflows.setPid(pid);
//                    workflowService.save(workflows);
                    log.info("{}: waiting for workflow to finish...",pid);
                }
                if(resume == 3){
                    log.info("kill OK  {}: ",pid);
                }
                logStream = new FileOutputStream(cmdLog);
                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))){
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info(line);
                        String msg = Thread.currentThread().getName()+": "+line+"\n";
                        logStream.write(msg.getBytes());
                    }
                }
                process.waitFor();
                int exit = process.exitValue();
                if (resume == 1 || resume == 2) {
                    if(exit==0){
                        workflows.setStatus("completed");
                        log.info("{}: workflow completed",pid);
//                        workflowService.save(workflows);
                    }else {
                        workflows.setStatus("failed");
                        log.info("{}: workflow failed",pid);
//                        workflowService.save(workflows);
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if(logStream!=null){
                    try {
                        logStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    @Override
//    public void launch(TaskApp app) {
//        String runName = getRunName(app);
//        final String[] args = new String[]{
//                "nf", "-log", app.getLogPath(), "run", app.getPipeline(), "-name", runName, "-latest"
//        };
//        String commandLine = Joiner.on("\n\t").join(Arrays.asList(args));
//        app.setCommandLine(commandLine);
//
//        launch(app, args);
//    }

//    @Override
//    @Async("taskExecutor")
//    public void resume(TaskApp app) {
//        String runName = getRunName(app);
//        app.setRunName(runName);
//        final String[] args = new String[]{
//                "nf", "-log", app.getLogPath(), "run", app.getPipeline(), "-name", app.getRunName(), "-latest","-resume",app.getSessionId()
//        };
//        String commandLine = Joiner.on("\n\t").join(Arrays.asList(args));
//        app.setCommandLine(commandLine);
//        launch(app, args);
//    }
    @Async("taskExecutor")
    @Override
    public void locaLaunch(Workflow app, String[] args) {
        try {

            Path workPath = Paths.get(app.getWorkDir());
            if (!workPath.toFile().exists()) {
                Files.createDirectories(workPath);
            }
            Path outputDir = Paths.get(app.getOutputDir());
            if(!outputDir.toFile().exists()){
                Files.createDirectories(outputDir);
            }


            OutputStream logStream = null;
            try {
                logStream = new FileOutputStream(app.getCmdLog());


                ProcessBuilder processBuilder = new ProcessBuilder();
                Map<String, String> environment = processBuilder.environment();

                environment.put("NXF_UUID",app.getSessionId());
                environment.put("TOWER_ACCESS_TOKEN",towerToken);
                environment.put("TOWER_WORKFLOW_ID",app.getId());

                processBuilder.directory(workPath.toFile());
                processBuilder.command(args);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                long pid = process.pid();
                log.info("{}: 任内开始运行！", app.getApplicationId());
                log.info(workPath + ", pid:" + pid);
                log.info("{}: 保存APP PID...", pid);
                app.setPid(pid);
                app.setWorkflowStatus(WorkflowStatus.RUNNING);
                taskAppService.save(app);
                log.info("{}: 等待工作流完成...", pid);
                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info(line);
                        String msg = Thread.currentThread().getName() + ": " + line + "\n";
                        logStream.write(msg.getBytes());
                    }
                }
                process.waitFor();
                int exit = process.exitValue();
                app.setFinishDate(new Date());
                app.setPid(null);
                if (exit == 0) {
                    app.setWorkflowStatus(WorkflowStatus.SUCCESS);
                    log.info("{}: 任务 运行成功!", pid);
                    taskAppService.save(app);
                } else {
                    app.setWorkflowStatus(WorkflowStatus.FAILED);
                    log.info("{}: 任务 运行失败！", pid);
                    taskAppService.save(app);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (logStream != null) {
                    try {
                        logStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fluxLaunch(Workflow app) {
        String workDir = app.getWorkDir();
        JSONObject jsonObject = new JSONObject();
        JSONObject data = null;
       // String url = fluxServerUrl + "/v1/jobs/submit?exclusive=false&is_launcher=false&command=nextflow run "+workDir +"/main.nf";
        String url = fluxServerUrl + "/v1/jobs/submit?exclusive=false&is_launcher=false&command=nextflow run /mnt/main.nf";
        // 创建一个URL对象
        URL urlData = null;
        try {
            urlData = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // 使用URLEncoder类对URL中的空格进行编码
        String encodedUrl = null;
        try {
            encodedUrl = URLEncoder.encode(urlData.toString(), "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 将编码后的URL作为字符串使用
        System.out.println(encodedUrl);
        data = restTemplate.postForObject(url, jsonObject, JSONObject.class);
        System.out.println("111");
        String dataId = data.get("id").toString();
        Workflow byId = taskAppService.findById(app.getId());
    }
    public static String generateContent(String content) {
        // 将 content 进行 url 编码
        String ans;
        try {
            ans = URLEncoder.encode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // 处理异常情况
            throw new RuntimeException(e);
        }

        return ans;
    }

//    @Override
//    @Async("taskExecutor")
//    public void resume(TaskApp app) {
//        try {
//            String runName = getRunName(app);
//            Path workPath = Paths.get(app.getWorkDir(), app.getId());
//            if (!workPath.toFile().exists()) {
//                Files.createDirectories(workPath);
//            }
//            String cmdLog = workPath + File.separator + ".workflow.log";
//            String logPath = app.getOutputDir() + File.separator + "nextflow.log";
//            String[] args = new String[]{
//                    "/home/wangyang/bin/nf", "-log", logPath, "run", app.getPipeline(), "-name", runName, "-profile", app.getProfiles(),"-latest","-resume"
//            };
//            OutputStream logStream = null;
//            try {
//                ProcessBuilder processBuilder = new ProcessBuilder();
//                processBuilder.directory(workPath.toFile());
//                processBuilder.command(args);
//                processBuilder.redirectErrorStream(true);
//                Process process = processBuilder.start();
//                long pid = process.pid();
//                log.info(workPath + ", pid:" + pid);
//                log.info("{}: 保存APP PID...", pid);
//                app.setPid(pid);
//                taskAppService.save(app);
//                log.info("{}: 等待工作流完成...", pid);
//                logStream = new FileOutputStream(cmdLog);
//                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        log.info(line);
//                        String msg = Thread.currentThread().getName() + ": " + line + "\n";
//                        logStream.write(msg.getBytes());
//                    }
//                }
//                process.waitFor();
//                int exit = process.exitValue();
//                if (exit == 0) {
//                    app.setTaskStatus(TaskStatus.SUCCESS);
//
//                    log.info("{}: 任务 运行成功!", pid);
//                    taskAppService.save(app);
//                } else {
//                    app.setTaskStatus(TaskStatus.FAILED);
//
//                    log.info("{}: 任务 运行失败！", pid);
//                    taskAppService.save(app);
//                }
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            } finally {
//                if (logStream != null) {
//                    try {
//                        logStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    @Async("taskExecutor")
    public void cancel(Workflow app) {
        if(app.getPid()!=null){
            String[] args  = new String[]{
                    "/usr/bin/kill", String.valueOf(app.getPid())
            };
            OutputStream logStream = null;
            try {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command(args);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                long pid = process.pid();
                log.info("{}，{}: Kill",app.getRunName(),app.getPid());
//                app.setPid(pid);
                taskAppService.save(app);
                logStream = new FileOutputStream(app.getCmdLog());
                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info(line);
                        String msg = Thread.currentThread().getName() + ": " + line + "\n";
                        logStream.write(msg.getBytes());
                    }
                }
                process.waitFor();
                app.setPid(null);
                taskAppService.save(app);
                int exit = process.exitValue();
                if (exit == 0) {
//                    app.setTaskStatus(TaskStatus.SUCCESS);
                    log.info("{}: 任务 取消成功!", pid);

                } else {
//                    app.setTaskStatus(TaskStatus.FAILED);
                    log.info("{}: 任务 取消 失败！", pid);
//                    taskAppService.save(app);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (logStream != null) {
                    try {
                        logStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
