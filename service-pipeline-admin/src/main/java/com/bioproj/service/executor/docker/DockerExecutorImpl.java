package com.bioproj.service.executor.docker;

import com.bioproj.pojo.task.Workflow;
import com.bioproj.domain.enums.WorkflowStatus;
import com.bioproj.service.executor.IExecutorsService;
import com.bioproj.utils.FileUtils;
import com.bioproj.utils.ServiceUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public class DockerExecutorImpl implements IExecutorsService {
    @Value("${nextflow.tower-token}")
    String towerToken;
    @Value("${userId}")
    String userId;
    @Value("${userHome}")
    String userHome;
    @Value("${workDir}")
    String workDir;

   @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;
//    private static DockerClientConfig getConfig(){
//        DockerClientConfig custom = DefaultDockerClientConfig.createDefaultConfigBuilder()
//                .withDockerHost("tcp://docker.somewhere.tld:2376")
//                .withDockerTlsVerify(true)
//                .withDockerCertPath("/home/user/.docker")
//                .withRegistryUsername(registryUser)
//                .withRegistryPassword(registryPass)
//                .withRegistryEmail(registryMail)
//                .withRegistryUrl(registryUrl)
//                .build();
//        return custom;
//    }

    private static DockerClient getDockerHttpClient(){
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        return dockerClient;
    }



    @Override
    public boolean ping() {
        return false;
    }

    @Override
    @Async
    public void writeFile(Workflow task) {
        try {
            if(userHome.equals("")){
                userHome = System.getProperty("user.home");
            }
            File tempFile = File.createTempFile(task.getId(), ".txt");
            FileUtils.saveFile(tempFile,"#!/bin/sh \n"+task.getEnv()+"\n\n"+task.getCommandLine().replace("\n\t"," "));
            String parent = tempFile.getParent();

            List<Bind> binds = new ArrayList<>();
            binds.addAll(Arrays.asList(Bind.parse("/etc/group:/etc/group"),
                    Bind.parse("/etc/passwd:/etc/passwd"),
                    Bind.parse(parent+":"+parent),
                    Bind.parse(task.getWorkDir() + ":" + task.getWorkDir()),
                    Bind.parse(userHome + "/.nextflow:" + userHome + "/.nextflow"),
                    Bind.parse(userHome + "/.kube:" + userHome + "/.kube"),
                    Bind.parse("/etc/hosts:/etc/hosts")));


            String pipeline = task.getPipeline();
            Path pipelinePath = Paths.get(pipeline);
            if(pipeline.startsWith("/") && pipelinePath.toFile().exists()){
                Path parentPath = pipelinePath.getParent();
                if(!parentPath.toString().equals(task.getWorkDir())){
                    binds.add(Bind.parse(parentPath.toString()+":"+parentPath.toString()));
                }
            }

//        String commandLine = task.getCommandLine();
//        String[] split = commandLine.split("\n\t");

            DockerClient dockerClient = getDockerHttpClient();
            HostConfig hostConfig = HostConfig
                    .newHostConfig()
                    .withAutoRemove(true);
            CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd("wybioinfo/nextflow")
                    .withUser(userId)
    //                .withAttachStdout(true)
                    .withCmd("cp",tempFile.toString(),task.getRunPath())
                    .withName(task.getId())
                    .withHostConfig(hostConfig)
                    .withBinds(binds)
                    .withEnv("TOWER_WORKFLOW_ID=" + task.getId(),
                            "NXF_UUID=" + task.getSessionId(),
                            "TOWER_ACCESS_TOKEN=" + towerToken)
                    .withWorkingDir(task.getWorkDir());
            log.info("cp {} to {}",tempFile.toString(),task.getRunPath());
            CreateContainerResponse container = createContainerCmd.exec();
            task.setSubmitId(container.getId());
//        task.setTaskStatus(TaskStatus.CREATED);
//        kafkaTemplate.send("tasks-result",task.getId(),task);

            dockerClient.startContainerCmd(container.getId()).exec();
//            task.setTaskStatus(TaskStatus.RUNNING);
//            kafkaTemplate.send("tasks-result","submit",task);


//        sb.append("export NXF_UUID="+task.getSessionId()+"\n");
//        sb.append("export TOWER_ACCESS_TOKEN="+towerToken+"\n");
//        sb.append("export TOWER_WORKFLOW_ID="+task.getId()+"\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



    @Override
    @Async
    public void submit(Workflow task) {
        if(userHome.equals("")){
            userHome = System.getProperty("user.home");
        }


        List<Bind> binds = new ArrayList<>();
        binds.addAll(Arrays.asList(Bind.parse("/etc/group:/etc/group"),
                Bind.parse("/etc/passwd:/etc/passwd"),
                Bind.parse("/data:/data"),
                Bind.parse(task.getWorkDir() + ":" + task.getWorkDir()),
                Bind.parse(userHome + "/.nextflow:" + userHome + "/.nextflow"),
                Bind.parse(userHome + "/.kube:" + userHome + "/.kube"),
                Bind.parse(userHome + "/.minikube:" + userHome + "/.minikube"),
                Bind.parse("/etc/hosts:/etc/hosts")));



        String pipeline = task.getPipeline();
        Path pipelinePath = Paths.get(pipeline);
        if(pipeline.startsWith("/") && pipelinePath.toFile().exists()){
            Path parentPath = pipelinePath.getParent();
            if(!parentPath.toString().equals(task.getWorkDir())){
                binds.add(Bind.parse(parentPath.toString()+":"+parentPath.toString()));
            }
        }

//        String commandLine = task.getCommandLine();
//        String[] split = commandLine.split("\n\t");

        DockerClient dockerClient = getDockerHttpClient();
        HostConfig hostConfig = HostConfig
                .newHostConfig()
                .withAutoRemove(true);
        CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd("wybioinfo/nextflow:23.11.0")
                .withUser(userId)
//                .withAttachStdout(true)
                .withCmd("sh",task.getRunPath())
                .withName(task.getId())
                .withHostConfig(hostConfig)
                .withBinds(binds)
                .withEnv("NXF_WORKFLOW_ID=" + task.getId(),
                        "NXF_UUID=" + task.getSessionId())
                .withWorkingDir(task.getWorkDir());
        CreateContainerResponse container = createContainerCmd.exec();
        task.setSubmitId(container.getId());
//        task.setTaskStatus(TaskStatus.CREATED);
//        kafkaTemplate.send("tasks-result",task.getId(),task);

        dockerClient.startContainerCmd(container.getId()).exec();
        task.setWorkflowStatus(WorkflowStatus.RUNNING);
        kafkaTemplate.send("tasks-result","submit",task);


//        sb.append("export NXF_UUID="+task.getSessionId()+"\n");
//        sb.append("export TOWER_ACCESS_TOKEN="+towerToken+"\n");
//        sb.append("export TOWER_WORKFLOW_ID="+task.getId()+"\n");

    }

    @Override
    public List<Workflow> jobs() {
        DockerClient dockerClient = getDockerHttpClient();
        List<Container> containers = dockerClient.listContainersCmd().exec();

        return null;
    }

    @Override
    public Boolean cancel(String id) {
        DockerClient dockerClient = getDockerHttpClient();
        dockerClient.stopContainerCmd(id).exec();
        return true;
    }

    @Override
    public Boolean status(String id) {
        DockerClient dockerClient = getDockerHttpClient();
        List<Container> containers = dockerClient.listContainersCmd().exec();
        Set<String> ids = ServiceUtil.fetchProperty(containers, Container::getId);
        if (ids.contains(id)) return true;
        return false;
    }
    @Override
    public Boolean runScript(Workflow task, String file){
        // Docker 拉取镜像wybioinfo/nextflow，挂杂task对象的workDir和storagePath，执行删除操作
        try {
            File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".txt");
            FileUtils.saveFile(tempFile,"#!/bin/sh \n"+file);
            String parent = tempFile.getParent();

            DockerClient dockerClient = getDockerHttpClient();
            List<Bind> binds = new ArrayList<>();
            binds.addAll(Arrays.asList(Bind.parse("/etc/group:/etc/group"),
                    Bind.parse("/etc/passwd:/etc/passwd"),
                    Bind.parse(parent+":"+parent),
                    Bind.parse("/data:/data"),
                    Bind.parse(task.getWorkDir() + ":" + task.getWorkDir())

            ));
            if(task.getStoragePath()!=null){
                binds.add(Bind.parse(task.getStoragePath() + ":" + task.getStoragePath()));
            }

//            List<String> split = Arrays.asList("ls");

            HostConfig hostConfig = HostConfig
                    .newHostConfig()
                    .withAutoRemove(true);
            CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd("wybioinfo/nextflow")
                    .withUser(userId)
                    .withCmd("sh",tempFile.toString())
    //                .withName(task.getId()+"-rm")
                    .withHostConfig(hostConfig)
                    .withBinds(binds);
//                .withWorkingDir(task.getWorkDir());
            CreateContainerResponse container = createContainerCmd.exec();
            dockerClient.startContainerCmd(container.getId()).exec();
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Boolean delete(Workflow task){
        // Docker 拉取镜像wybioinfo/nextflow，挂杂task对象的workDir和storagePath，执行删除操作
        DockerClient dockerClient = getDockerHttpClient();
            List<Bind> binds = new ArrayList<>();
        binds.addAll(Arrays.asList(Bind.parse("/etc/group:/etc/group"),
                Bind.parse("/etc/passwd:/etc/passwd"),
                Bind.parse("/data:/data"),
                Bind.parse(task.getWorkDir() + ":" + task.getWorkDir()),
                Bind.parse(task.getStoragePath() + ":" + task.getStoragePath())
        ));

        List<String> split = Arrays.asList("ls");

        HostConfig hostConfig = HostConfig
                .newHostConfig()
                .withAutoRemove(true);
        CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd("wybioinfo/nextflow")
                .withUser(userId)
                .withCmd(split)
                .withName(task.getId()+"-rm")
                .withHostConfig(hostConfig)
                .withBinds(binds)
                .withWorkingDir(task.getWorkDir());
        CreateContainerResponse container = createContainerCmd.exec();
        dockerClient.startContainerCmd(container.getId()).exec();
        return true;
    }

    @Override
    @Async
    public Boolean stop(Workflow task) {

        boolean flag = true; // 要判断的值，初始为false

        while (flag) {
            // 执行轮询的逻辑
            // 在这里可以进行某些操作，例如检查值是否为true
            flag = status(task.getSubmitId());
            if (!flag) {
                break; // 如果值为true，则退出循环
            }
            try {
                Thread.sleep(1000); // 等待1秒后继续下一次轮询
            } catch (InterruptedException e) {
                // 处理中断异常
            }
        }
        task.setSubmitId(null);
        kafkaTemplate.send("tasks-result","stop",task);
        return true;
    }
}
