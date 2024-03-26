package com.bioproj.service;

import com.bioproj.k8s.ApiSuccessCallback;
import com.bioproj.k8s.Job;
import com.bioproj.k8s.KService;
import com.bioproj.k8s.Pod;

import io.kubernetes.client.extended.pager.Pager;
import io.kubernetes.client.openapi.ApiCallback;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;

import java.util.List;
import java.util.Map;

public interface IK8sApiService {
    void api();

    CoreV1Api getApi();

    BatchV1Api getJobApi();

    void yamlExample();

    V1Pod findPodByName(CoreV1Api api, String namespace, String podName);

    void delPodByName(CoreV1Api api, String namespace, String podName,ApiCallback<V1Pod> _callback);

    void delServiceByName(CoreV1Api api, String namespace, String podName,ApiCallback<V1Service> _callback);

    V1Service findServiceByName(CoreV1Api api, String namespace, String podName);

    void createPod(CoreV1Api api, String namespace, Pod podParams,ApiCallback<V1Pod> _callback);

    void createNamespace(CoreV1Api api, String namespace);

    void createPersistentVolumeClaim(CoreV1Api api, String namespace);

    void createPersistentVolume(CoreV1Api api);

    void createJob(BatchV1Api api, String namespace, Job job);

    void createOrReplaceJob(BatchV1Api api, String namespace, Job job);

    void replaceJob(BatchV1Api api, String namespace, Job job);

    void replaceJob(BatchV1Api api, String namespace,
                    Job job,
                    List<V1EnvVar> envVarList,
                    List<V1VolumeMount> v1VolumeMounts,
                    List<V1Volume> v1Volumes,
                    Map<String, String> labels);

    void delJobList(BatchV1Api api, String namespace, Map<String, String> labelSelectorMap, ApiSuccessCallback<V1Status> _callback);

    void delJobList(BatchV1Api api, String namespace, Map<String, String> labelSelectorMap, ApiCallback<V1Status> _callback);

    void createOrReplaceJob(BatchV1Api api, String namespace,
                            Job job,
                            List<V1EnvVar> envVarList,
                            List<V1VolumeMount> v1VolumeMounts,
                            List<V1Volume> v1Volumes,
                            Map<String, String> labels);

    void createJob(BatchV1Api api, String namespace,
                   Job job,
                   List<V1EnvVar> envVarList,
                   List<V1VolumeMount> v1VolumeMounts,
                   List<V1Volume> v1Volumes,
                   Map<String, String> labels);

    //    kubectl port-forward  --address 0.0.0.0 svc/aservice 32000:80
    void createPod(CoreV1Api api, String namespace,
                   Pod podParams,
                   List<V1EnvVar> envVarList,
                   List<V1VolumeMount> v1VolumeMounts,
                   List<V1Volume> v1Volumes,
                   Map<String, String> labels,
                   List<String> cmds,
                   ApiCallback<V1Pod> _callback);

    void createService(CoreV1Api api, String namespace, KService kService,ApiCallback<V1Service> _callback);

    V1PodList listPods(CoreV1Api api, String namespace, Map<String, String> labelSelectorMap);

    V1JobList listJobs(BatchV1Api api, String namespace, Map<String,String> labelSelectorMap);

    Pager<V1Pod, V1PodList> page(CoreV1Api api, String namespace);

    List<V1Status> delJobList(BatchV1Api api, String namespace, V1JobList v1JobList);

    V1Status delJobList(BatchV1Api api, String namespace, Map<String, String> labelSelectorMap);

    void delPodList(CoreV1Api api, String namespace, Map<String, String> labelSelectorMap, ApiCallback<V1Status> _callback);

    V1Status delPodList(CoreV1Api api, String namespace, Map<String, String> labelSelectorMap);
}
