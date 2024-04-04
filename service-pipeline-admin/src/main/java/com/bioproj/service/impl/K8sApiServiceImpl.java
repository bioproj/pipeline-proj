package com.bioproj.service.impl;

import com.bioproj.domain.k8s.Env;
import com.bioproj.domain.k8s.Mounts;
import com.bioproj.domain.k8s.Volumes;
import com.bioproj.k8s.*;
import com.bioproj.service.IK8sApiService;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.extended.pager.Pager;
import io.kubernetes.client.openapi.ApiCallback;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

//https://github.com/kubernetes-client/java/blob/master/docs/java-controller-tutorial-rewrite-rs-controller.md

@Service
@Slf4j
public class K8sApiServiceImpl implements IK8sApiService {
//    @Autowired
//    ApiClient apiClient;

    @Autowired(required = false)
    ApiClient client;

    @Override
    public void api() {
        try {
            CoreV1Api api = new CoreV1Api();
            V1PodList list =
                    api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Pod item : list.getItems()) {
                System.out.println(item.getMetadata().getName());
            }

        } catch (ApiException e) {
            throw new RuntimeException(e);
        }


    }
    @Override
    public CoreV1Api getApi(){
        CoreV1Api api = new CoreV1Api();
        return api;
    }
    @Override
    public BatchV1Api getJobApi(){
        BatchV1Api api = new BatchV1Api();
        return api;
    }
    @Override
    public  void yamlExample() {
        try {
            V1Pod pod =
                    new V1PodBuilder()
                            .withNewMetadata()
                            .withName("apod")
                            .endMetadata()
                            .withNewSpec()
                            .addNewContainer()
                            .withName("www")
                            .withImage("nginx")
                            .withNewResources()
                            .withLimits(new HashMap<>())
                            .endResources()
                            .endContainer()
                            .endSpec()
                            .build();
            System.out.println(Yaml.dump(pod));

            V1Service svc =
                    new V1ServiceBuilder()
                            .withNewMetadata()
                            .withName("aservice")
                            .endMetadata()
                            .withNewSpec()
                            .withSessionAffinity("ClientIP")
                            .withType("NodePort")
                            .addNewPort()
                            .withProtocol("TCP")
                            .withName("client")
                            .withPort(8008)
                            .withNodePort(8080)
                            .withTargetPort(new IntOrString(8080))
                            .endPort()
                            .endSpec()
                            .build();
            System.out.println(Yaml.dump(svc));

            // Read yaml configuration file, and deploy it
            ApiClient client = Config.defaultClient();
            Configuration.setDefaultApiClient(client);

            //  See issue #474. Not needed at most cases, but it is needed if you are using war
            //  packging or running this on JUnit.
            Yaml.addModelMap("v1", "Service", V1Service.class);

            // Example yaml file can be found in $REPO_DIR/test-svc.yaml
            File file = new File("test-svc.yaml");
            V1Service yamlSvc = (V1Service) Yaml.load(file);

            // Deployment and StatefulSet is defined in apps/v1, so you should use AppsV1Api instead of
            // CoreV1API
            CoreV1Api api = new CoreV1Api();
            V1Service createResult =
                    api.createNamespacedService("default", yamlSvc, null, null, null, null);

            System.out.println(createResult);

            V1Service deleteResult =
                    api.deleteNamespacedService(
                            yamlSvc.getMetadata().getName(),
                            "default",
                            null,
                            null,
                            null,
                            null,
                            null,
                            new V1DeleteOptions());
            System.out.println(deleteResult);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public V1Pod findPodByName(CoreV1Api api, String namespace, String podName){
        try {
            V1Pod v1Pod = api.readNamespacedPod(podName, namespace, null);
            return v1Pod;
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            return null;
        }

        // 按名称查找 Pod
//        try {
//            V1PodList podList = api.listNamespacedPod(namespace, null,null, null, null, null, null, null, null, null, null);
//            for (V1Pod pod : podList.getItems()) {
//                if (pod.getMetadata().getName().equals(podName)) {
//                    // 找到匹配的 Pod
//                    log.info("Found Pod: " + pod.getMetadata().getName());
//                    return pod;
//                }
//            }
//            return null;
//        } catch (ApiException e) {
//            throw new RuntimeException(e);
//        }
    }
    @Override
    public void delPodByName(CoreV1Api api, String namespace, String podName,ApiCallback<V1Pod> _callback){
        try {
            api.deleteNamespacedPodAsync(podName, namespace, null, null, null, null, null, null,_callback);
//            return v1Pod;
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delServiceByName(CoreV1Api api, String namespace, String podName,ApiCallback<V1Service> _callback){
        try {
             api.deleteNamespacedServiceAsync(podName, namespace, null, null, null, null, null, null,_callback);
//            return service;
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public V1Service findServiceByName(CoreV1Api api, String namespace, String serviceName) {
        try {
            V1Service v1Service = api.readNamespacedService(serviceName, namespace, null);
            return  v1Service;
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            return  null;

        }
        // 按名称查找 Pod
//        try {
//            V1ServiceList v1ServiceList = api.listNamespacedService(namespace, null, null, null, null, null, null, null, null, null, null);
//            for (V1Service service : v1ServiceList.getItems()) {
//                if (service.getMetadata().getName().equals(serviceName)) {
//                    // 找到匹配的 Service
//                    log.info("Found Pod: " + service.getMetadata().getName());
//                    return service;
//                }
//            }
//            return null;
//        } catch (ApiException e) {
//            throw new RuntimeException(e);
//        }

    }




    @Override
    public void createNamespace(CoreV1Api api, String namespace){
        try {
            V1Namespace v1Namespace = new V1NamespaceBuilder()
                    .withNewMetadata()
                    .withName(namespace)
                    .endMetadata()
                    .build();
            api.createNamespace(v1Namespace,null,null,null,null);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void createPersistentVolumeClaim(CoreV1Api api, String namespace){
        try {
            V1PersistentVolumeClaim v1PersistentVolumeClaim = new V1PersistentVolumeClaimBuilder()
                    .withNewMetadata()
                    .withName("")
                    .endMetadata()
                    .withNewSpec()
                    .withAccessModes("ReadWriteMany")
                    .endSpec()
                    .build();
            api.createNamespacedPersistentVolumeClaim(namespace,v1PersistentVolumeClaim,null,null,null,null);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void createPersistentVolume(CoreV1Api api){
        try {
            V1PersistentVolume persistentVolume = new V1PersistentVolumeBuilder()
                    .build();
            api.createPersistentVolume(persistentVolume,null,null,null,null);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createJob(BatchV1Api api, String namespace, Job job){
        List<V1EnvVar> envVarList  = new ArrayList<>();
        List<V1Volume> v1Volumes = new ArrayList<>();
        List<V1VolumeMount> v1VolumeMounts = new ArrayList<>();
        for (Map.Entry<String,String> volumeParams: job.getVolumes().entrySet()){
            V1Volume volume = new V1VolumeBuilder()
                    .withName(volumeParams.getKey())
                    .withPersistentVolumeClaim(new V1PersistentVolumeClaimVolumeSource().claimName(volumeParams.getValue()))
                    .build();
            v1Volumes.add(volume);
        }
        for (Map.Entry<String,String> item: job.getEnv().entrySet()){
            V1EnvVar v1EnvVar = new V1EnvVarBuilder()
                    .withName(item.getKey())
                    .withValue(item.getValue())
                    .build();
            envVarList.add(v1EnvVar);
        }
        for (Mounts item: job.getMounts()){
            V1VolumeMount v1VolumeMount = new V1VolumeMountBuilder()
                    .withName(item.getName())
                    .withMountPath(item.getMountPath())
                    .withSubPath(item.getSubPath())
                    .build();
            v1VolumeMounts.add(v1VolumeMount);
        }
        createJob(api,namespace,job,envVarList,v1VolumeMounts,v1Volumes,job.getLabels());
    }
    @Override
    public void createOrReplaceJob(BatchV1Api api, String namespace, Job job){
        List<V1EnvVar> envVarList  = new ArrayList<>();
        List<V1Volume> v1Volumes = new ArrayList<>();
        List<V1VolumeMount> v1VolumeMounts = new ArrayList<>();
        for (Map.Entry<String,String> volumeParams: job.getVolumes().entrySet()){
            V1Volume volume = new V1VolumeBuilder()
                    .withName(volumeParams.getKey())
                    .withPersistentVolumeClaim(new V1PersistentVolumeClaimVolumeSource().claimName(volumeParams.getValue()))
                    .build();
            v1Volumes.add(volume);
        }
        for (Map.Entry<String,String> item: job.getEnv().entrySet()){
            V1EnvVar v1EnvVar = new V1EnvVarBuilder()
                    .withName(item.getKey())
                    .withValue(item.getValue())
                    .build();
            envVarList.add(v1EnvVar);
        }
        for (Mounts item: job.getMounts()){
            V1VolumeMount v1VolumeMount = new V1VolumeMountBuilder()
                    .withName(item.getName())
                    .withMountPath(item.getMountPath())
                    .withSubPath(item.getSubPath())
                    .build();
            v1VolumeMounts.add(v1VolumeMount);
        }
        createOrReplaceJob(api,namespace,job,envVarList,v1VolumeMounts,v1Volumes,job.getLabels());
    }
    @Override
    public void replaceJob(BatchV1Api api, String namespace, Job job){
        List<V1EnvVar> envVarList  = new ArrayList<>();
        List<V1Volume> v1Volumes = new ArrayList<>();
        List<V1VolumeMount> v1VolumeMounts = new ArrayList<>();
        for (Map.Entry<String,String> volumeParams: job.getVolumes().entrySet()){
            V1Volume volume = new V1VolumeBuilder()
                    .withName(volumeParams.getKey())
                    .withPersistentVolumeClaim(new V1PersistentVolumeClaimVolumeSource().claimName(volumeParams.getValue()))
                    .build();
            v1Volumes.add(volume);
        }
        for (Map.Entry<String,String> item: job.getEnv().entrySet()){
            V1EnvVar v1EnvVar = new V1EnvVarBuilder()
                    .withName(item.getKey())
                    .withValue(item.getValue())
                    .build();
            envVarList.add(v1EnvVar);
        }
        for (Mounts item: job.getMounts()){
            V1VolumeMount v1VolumeMount = new V1VolumeMountBuilder()
                    .withName(item.getName())
                    .withMountPath(item.getMountPath())
                    .withSubPath(item.getSubPath())
                    .build();
            v1VolumeMounts.add(v1VolumeMount);
        }
        replaceJob(api,namespace,job,envVarList,v1VolumeMounts,v1Volumes,job.getLabels());
    }
    @Override
    public void replaceJob(BatchV1Api api, String namespace,
                           Job job,
                           List<V1EnvVar> envVarList,
                           List<V1VolumeMount> v1VolumeMounts,
                           List<V1Volume> v1Volumes,
                           Map<String, String> labels){
        try {
            V1Job v1Job = job(namespace, job, envVarList, v1VolumeMounts, v1Volumes, labels);
            String dump = Yaml.dump(v1Job);
            log.info("\n"+dump);
            api.replaceNamespacedJob(job.getName(),namespace,v1Job,null,null,null,null);
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            throw new RuntimeException(e);
        }
    }


    public V1Job readNamespacedJob(BatchV1Api api,Job job,String namespace){
        try {
            V1Job v1Job1 = api.readNamespacedJob(job.getName(), namespace, null);
            return v1Job1;
        } catch (ApiException e) {
            log.info(e.getResponseBody());
            return null;
        }
    }

    @Override
    @Async
    public void createOrReplaceJob(BatchV1Api api, String namespace,
                                   Job job,
                                   List<V1EnvVar> envVarList,
                                   List<V1VolumeMount> v1VolumeMounts,
                                   List<V1Volume> v1Volumes,
                                   Map<String, String> labels){
        //            V1Job v1Job = job(namespace, job, envVarList, v1VolumeMounts, v1Volumes, labels);
//            String dump = Yaml.dump(v1Job);
//            log.info("\n"+dump);


        V1JobList v1JobList = listJobs(api, namespace, Map.of("workflowId", job.getWorkflowId()));
//        V1Job v1Job1 = readNamespacedJob(api, job, namespace);
        if(v1JobList.getItems().size()!=0){
            delJobList(api, namespace,Map.of("workflowId", job.getWorkflowId()), new ApiCallback<V1Status>() {
                @Override
                public void onFailure(ApiException e, int i, Map<String, List<String>> map) {
                    System.out.println("createOrReplaceJob onFailure");
                }

                @Override
                public void onSuccess(V1Status v1Status, int i, Map<String, List<String>> map) {
                    Boolean flag = true;
                    while(flag){
                        V1JobList v1JobList = listJobs(api, namespace, Map.of("workflowId", job.getWorkflowId()));
                        if(v1JobList.getItems().size()==0){
                            flag =false;
                            createJob(api,namespace,job,envVarList,v1VolumeMounts,v1Volumes,job.getLabels());
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
//
//                    if(v1JobList.getItems().size()!=0){
//                        System.out.println("replaceJob onSuccess");
//                        replaceJob(api,namespace,job,envVarList,v1VolumeMounts,v1Volumes,job.getLabels());
//                    }else {
//                        System.out.println("createJob onSuccess");
//
//                    }

                }

                @Override
                public void onUploadProgress(long l, long l1, boolean b) {
                    System.out.println("createOrReplaceJob onUploadProgress");
                }

                @Override
                public void onDownloadProgress(long l, long l1, boolean b) {
                    System.out.println("createOrReplaceJob onDownloadProgress");
                }
            });
//            String workflowId = job.getWorkflowId();
////            V1JobList v1JobList = listJobs(api, namespace, Map.of("workflowId", workflowId));
////            delJobList(api,namespace,v1JobList);
//
//            // 只删除job
////                String propagationPolicy = "Foreground"; // 设置为 Foreground 可以确保同时删除关联的 Pod
////                api.deleteNamespacedJob(job.getName(), namespace,null,null,null,null,propagationPolicy,null);

        }else {
            createJob(api,namespace,job,envVarList,v1VolumeMounts,v1Volumes,job.getLabels());
        }
    }
    @Override
    public void createJob(BatchV1Api api, String namespace,
                          Job job,
                          List<V1EnvVar> envVarList,
                          List<V1VolumeMount> v1VolumeMounts,
                          List<V1Volume> v1Volumes,
                          Map<String, String> labels){
        try {
            V1Job v1Job = job(namespace, job, envVarList, v1VolumeMounts, v1Volumes, labels);
            String dump = Yaml.dump(v1Job);
            log.info("\n"+dump);
            api.createNamespacedJob(namespace,v1Job,null,null,null,null);
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            throw new RuntimeException(e);
        }
    }


    private V1Job job(String namespace,
                     Job job,
                     List<V1EnvVar> envVarList,
                     List<V1VolumeMount> v1VolumeMounts,
                     List<V1Volume> v1Volumes,
                     Map<String, String> labels){
        if(labels==null){
            labels = new HashMap<>();
        }

//            List<V1EnvVar> envVarList  = new ArrayList<>();
//            List<V1VolumeMount> v1VolumeMounts = new ArrayList<>();
//            List<V1Volume> v1Volumes = new ArrayList<>();

        if(job.getCommand()==null){
            job.setCommand(new ArrayList<>());
        }

        if(job.getRestartPolicy()==null){
            job.setRestartPolicy("Never");
        }
        labels.put("app",job.getName());

        V1ObjectMeta v1ObjectMeta = new V1ObjectMetaBuilder()
                .withName(job.getName())
                .withLabels(labels)
                .withNamespace(namespace).build();
        V1Job v1Job = new V1JobBuilder()
                .withMetadata(v1ObjectMeta)
                .withNewSpec()
                .withBackoffLimit(job.getBackoffLimit())
                .withTtlSecondsAfterFinished(job.getTtlSecondsAfterFinished())

                .withNewTemplate()
                .withMetadata(v1ObjectMeta)
                .withNewSpec()
                .withRestartPolicy(job.getRestartPolicy())
                .withVolumes(v1Volumes)
                .withNewSecurityContext()
                .withRunAsUser(job.getRunUser())
                .withRunAsGroup(job.getRunGroup())
                .endSecurityContext()


                .addNewContainer()
                .withImagePullPolicy("Always")
                .withWorkingDir(job.getWorkDir())

                .withName(job.getName())
                .withImage(job.getImage())
                .withArgs(job.getCommand())
                .withEnv(envVarList)
                .withVolumeMounts(v1VolumeMounts)
                .endContainer()
                .endSpec()
                .endTemplate()

                .endSpec().build();
        return v1Job;
    }


    @Override
    public void createPod(CoreV1Api api, String namespace, Pod podParams,ApiCallback<V1Pod> _callback){
        List<V1EnvVar> envVarList  = new ArrayList<>();
        List<V1Volume> v1Volumes = new ArrayList<>();
        List<V1VolumeMount> v1VolumeMounts = new ArrayList<>();
        if(podParams.getVolumes()!=null){
            for (Volumes volumeParams: podParams.getVolumes()){
                V1Volume volume = new V1VolumeBuilder()
                        .withName(volumeParams.getKey())
                        .withPersistentVolumeClaim(new V1PersistentVolumeClaimVolumeSource().claimName(volumeParams.getValue()))
                        .build();
                v1Volumes.add(volume);
            }
        }

        if(podParams.getEnv()!=null){
            for (Env item: podParams.getEnv()){
                V1EnvVar v1EnvVar = new V1EnvVarBuilder()
                        .withName(item.getKey())
                        .withValue(item.getValue())
                        .build();
                envVarList.add(v1EnvVar);
            }
        }

        if(podParams.getMounts()!=null){
            for (Mounts item: podParams.getMounts()){
                V1VolumeMount v1VolumeMount = new V1VolumeMountBuilder()
                        .withName(item.getName())
                        .withMountPath(item.getMountPath())
                        .withSubPath(item.getSubPath())
                        .withReadOnly(item.getReadOnly())
                        .build();
                v1VolumeMounts.add(v1VolumeMount);
            }
        }

        createPod(api,namespace,podParams,envVarList,v1VolumeMounts,v1Volumes,podParams.getLabels(),podParams.getCommand(),_callback);
    }
//    kubectl port-forward  --address 0.0.0.0 svc/aservice 32000:80
    @Override
    public void createPod(CoreV1Api api, String namespace,
                          Pod podParams,
                          List<V1EnvVar> envVarList,
                          List<V1VolumeMount> v1VolumeMounts,
                          List<V1Volume> v1Volumes,
                          Map<String, String> labels,
                          List<String> cmds,
                          ApiCallback<V1Pod> _callback){
        try {

//            Map<String, String> labels = podParams.getLabels();
//            Map<String,String> map = new HashMap<>();
//            List<V1EnvVar> envVarList  = new ArrayList<>();
            if(labels==null){
                labels = new HashMap<>();
            }
//            List<V1VolumeMount> v1VolumeMounts = new ArrayList<>();
//            List<V1Volume> v1Volumes = new ArrayList<>();



            if(podParams.getRestartPolicy()==null){
                podParams.setRestartPolicy("Always");
            }
            labels.put("app",podParams.getName());
            V1PodFluent.SpecNested<V1PodBuilder> specNested = new V1PodBuilder()
                    .withNewMetadata()
                    .withName(podParams.getName())
                    .withLabels(labels)
                    .endMetadata()
                    .withNewSpec()
                    .withRestartPolicy(podParams.getRestartPolicy())
                    .withNewSecurityContext()
                    .withRunAsUser(podParams.getRunUser())
                    .withRunAsGroup(podParams.getRunGroup())
                    .endSecurityContext()
                    .addNewContainer()
                    .withImagePullPolicy("Always")
                    .withName(podParams.getName())
                    .withImage(podParams.getImage())
                    .withCommand(podParams.getCommand())
                    .withEnv(envVarList)
                    .withVolumeMounts(v1VolumeMounts)
                    .endContainer()
                    .withVolumes(v1Volumes);

            if(podParams.getRunUser()!=null && podParams.getRunGroup()!=null){
                specNested= specNested
                        .withNewSecurityContext()
                        .withRunAsUser(podParams.getRunUser())
                        .withRunAsGroup(podParams.getRunGroup())
                        .endSecurityContext();
            }
            V1Pod pod = specNested.endSpec()
                            .build();
            String dump = Yaml.dump(pod);
            log.info("\n"+dump);
            api.createNamespacedPodAsync(namespace, pod, null, null, null, null, _callback);

        } catch (ApiException e) {
            log.error(e.getResponseBody());
            throw new RuntimeException(e);
        }
    }
    @Override
    public void createService(CoreV1Api api, String namespace,KService kService,ApiCallback<V1Service> _callback){
        try {

            Map<String,String> selectorMap = new HashMap<>();
            selectorMap.put("app",kService.getName());
            V1Service svc =
                    new V1ServiceBuilder()
                            .withNewMetadata()
                            .withName(kService.getName())
                            .endMetadata()
                            .withNewSpec()
                            .withSelector(selectorMap)
                            .withSessionAffinity("ClientIP")
                            .withType("NodePort")
                            .addNewPort()
                            .withProtocol("TCP")
                            .withName("client")
                            .withPort(kService.getPort())
                            .withNodePort(kService.getNodePort())
                            .withTargetPort(new IntOrString(kService.getTargetPort()))
                            .endPort()
                            .endSpec()
                            .build();
            String svcYaml = Yaml.dump(svc);
            log.info(svcYaml);
            api.createNamespacedServiceAsync(namespace, svc, null, null, null, null,_callback);
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            throw new RuntimeException(e);
        }
    }



    public void listPod(CoreV1Api api,String namespace){
        try {
            V1PodList v1PodList = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null, null);
            for (V1Pod item : v1PodList.getItems()) {
                System.out.println(item.getMetadata().getName());
            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public  Pager<V1Pod, V1PodList> page(CoreV1Api api,String namespace) {
/*
        ApiClient client = Config.defaultClient();
        OkHttpClient httpClient =
                client.getHttpClient().newBuilder().readTimeout(60, TimeUnit.SECONDS).build();
        client.setHttpClient(httpClient);
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();
*/
        int i = 0;
        Pager<V1Pod, V1PodList> pager =
                new Pager<>(
                        (Pager.PagerParams param) -> {
                            try {
                                return api.listNamespacedPodCall(
                                        namespace,
                                        null,
                                        null,
                                        param.getContinueToken(),
                                        null,
                                        null,
                                        param.getLimit(),
                                        null,
                                        null,
                                        1,
                                        null,
                                        null);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        },
                        client,
                        10,
                        V1PodList.class);


        for (V1Pod v1Pod : pager) {
            System.out.println(v1Pod.getMetadata().getName());
        }
        return pager;

    }


    @Override
    public List<V1Status> delJobList(BatchV1Api api,String namespace,V1JobList v1JobList){
        List<V1Status> v1StatusList = new ArrayList<>();
        try {
            for (V1Job job : v1JobList.getItems()){
                String propagationPolicy = "Foreground"; // 设置为 Foreground 可以确保同时删除关联的 Pod
//                V1DeleteOptions v1DeleteOptions = new V1DeleteOptions().propagationPolicy(propagationPolicy);
                V1Status v1Status = api.deleteNamespacedJob(job.getMetadata().getName(), namespace, null, null, null, null, propagationPolicy, null);
                v1StatusList.add(v1Status);
            }
            return v1StatusList;
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public V1PodList listPods(CoreV1Api api, String namespace, Map<String, String> labelSelectorMap){
        try {
//            String labelSelector = "app=my-app,env=production";
//            Map<String,String> labelSelectorMap = new HashMap<>();
//            labelSelectorMap.put("nextflow.io/sessionId","uuid-e3c75e4c-5f33-45d9-8f7f-8ad7067a7cfb");

            List<String> labelList = new ArrayList<>();
            for (Map.Entry<String, String> entry : labelSelectorMap.entrySet()) {
                labelList.add(entry.getKey()+"="+entry.getValue());
            }
            String labelSelector = String.join(",", labelList);


            V1PodList v1PodList = api.listNamespacedPod(namespace, null, null, null, null, labelSelector, null, null, null, null, null);
            return v1PodList;
//            for (V1Job item : v1JobList.getItems()) {
//                System.out.println(item.getMetadata().getName());
//            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public V1JobList listJobs(BatchV1Api api, String namespace,Map<String,String> labelSelectorMap){
        try {
//            String labelSelector = "app=my-app,env=production";
//            Map<String,String> labelSelectorMap = new HashMap<>();
//            labelSelectorMap.put("nextflow.io/sessionId","uuid-e3c75e4c-5f33-45d9-8f7f-8ad7067a7cfb");

            List<String> labelList = new ArrayList<>();
            for (Map.Entry<String, String> entry : labelSelectorMap.entrySet()) {
                labelList.add(entry.getKey()+"="+entry.getValue());
            }
            String labelSelector = String.join(",", labelList);


            V1JobList v1JobList = api.listNamespacedJob(namespace, null, null, null, null, labelSelector, null, null, null, null, null);
            return v1JobList;
//            for (V1Job item : v1JobList.getItems()) {
//                System.out.println(item.getMetadata().getName());
//            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void delJobList(BatchV1Api api, String namespace, Map<String, String> labelSelectorMap, ApiSuccessCallback<V1Status> _callback){
        delJobList(api, namespace, labelSelectorMap, new ApiCallback<V1Status>() {
            @Override
            public void onFailure(ApiException e, int i, Map<String, List<String>> map) {
                _callback.onFinish(null,i,map);
            }

            @Override
            public void onSuccess(V1Status v1Status, int i, Map<String, List<String>> map) {
                _callback.onFinish(v1Status,i,map);
//                Boolean flag = true;
//                while(flag){
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    V1JobList v1JobList = listJobs(api, namespace, labelSelectorMap);
//                    if(v1JobList.getItems().size()==0){
//                        flag =false;
//                        _callback.onSuccess(v1Status,i,map);
//                    }
//                    delJobList(api,namespace,labelSelectorMap);
//                }
            }

            @Override
            public void onUploadProgress(long l, long l1, boolean b) {

            }

            @Override
            public void onDownloadProgress(long l, long l1, boolean b) {

            }
        });

    }
    @Override
    public void delJobList(BatchV1Api api, String namespace, Map<String, String> labelSelectorMap, ApiCallback<V1Status> _callback){
        try {
            List<String> labelList = new ArrayList<>();
            for (Map.Entry<String, String> entry : labelSelectorMap.entrySet()) {
                labelList.add(entry.getKey()+"="+entry.getValue());
            }
            String propagationPolicy = "Foreground"; // 设置为 Foreground 可以确保同时删除关联的 Pod
            String labelSelector = String.join(",", labelList);
            api.deleteCollectionNamespacedJobAsync(namespace, null, null, null, null, null, labelSelector, null, null, propagationPolicy, null, null, null, null,  _callback);

        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

    }



    @Override
    public V1Status delJobList(BatchV1Api api, String namespace, Map<String, String> labelSelectorMap){
        try {
            List<String> labelList = new ArrayList<>();
            for (Map.Entry<String, String> entry : labelSelectorMap.entrySet()) {
                labelList.add(entry.getKey()+"="+entry.getValue());
            }
            String propagationPolicy = "Foreground"; // 设置为 Foreground 可以确保同时删除关联的 Pod
            String labelSelector = String.join(",", labelList);
            V1Status v1Status = api.deleteCollectionNamespacedJob(namespace, null, null, null, null, null, labelSelector, null, null, propagationPolicy, null, null, null, null);
            return v1Status;
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void delPodList(CoreV1Api api, String namespace, Map<String, String> labelSelectorMap, ApiCallback<V1Status> _callback){
        try {
            List<String> labelList = new ArrayList<>();
            for (Map.Entry<String, String> entry : labelSelectorMap.entrySet()) {
                labelList.add(entry.getKey()+"="+entry.getValue());
            }
//            String propagationPolicy = "Foreground"; // 设置为 Foreground 可以确保同时删除关联的 Pod
            String labelSelector = String.join(",", labelList);
            api.deleteCollectionNamespacedPodAsync(namespace, null, null, null, null, null, labelSelector, null, null, null,null, null, null, null,  _callback);

        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public V1Status delPodList(CoreV1Api api, String namespace, Map<String, String> labelSelectorMap){
        try {
            List<String> labelList = new ArrayList<>();
            for (Map.Entry<String, String> entry : labelSelectorMap.entrySet()) {
                labelList.add(entry.getKey()+"="+entry.getValue());
            }
            String labelSelector = String.join(",", labelList);
            V1Status v1Status = api.deleteCollectionNamespacedPod(namespace, null, null, null, null, null, labelSelector, null, null, null, null, null, null, null);
            return v1Status;
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

    }

}
