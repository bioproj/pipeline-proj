package com.bioproj.service.impl;

import com.bioproj.domain.enums.K8sStatus;
import com.bioproj.domain.vo.K8sAppVo;
import com.bioproj.k8s.KService;
import com.bioproj.k8s.MapK8sProperties;
import com.bioproj.domain.k8s.Mounts;
import com.bioproj.k8s.Pod;
import com.bioproj.pojo.*;
import com.bioproj.repository.K8sAppRepository;
import com.bioproj.service.IK8sApiService;
import com.bioproj.service.IK8sAppService;
import com.bioproj.service.IPortsService;
import com.bioproj.service.images.IImagesService;
import com.bioproj.utils.ServiceUtil;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.R;
import com.mbiolance.cloud.auth.domain.dto.SysUserDto;
import com.mbiolance.cloud.auth.domain.vo.QueryCriteriaVo;
import com.mbiolance.cloud.auth.rpc.SysUserFeignService;
import io.kubernetes.client.openapi.ApiCallback;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j

public class K8sAppServiceImpl implements IK8sAppService {

    @Autowired
    K8sAppRepository k8sAppRepository;
    @Autowired
    IPortsService portsService;
    @Autowired
    IK8sApiService k8sApiService;

//    @Value("${userId}")
//    String uid;
    @Value("${userId}")
    Long userId;
    @Value("${groupId}")
    Long groupId;
    @Autowired
    MapK8sProperties mapK8sProperties;


    @Autowired
    IImagesService imagesService;


    @Autowired
    SysUserFeignService sysUserFeignService;

    private  static String namespace = "nextflow";


    @Override
    public PageModel<K8sAppVo> page(Integer number, Integer size, List<QueryCriteriaVo> criteriaVos) {
        number -= 1;
        number = number <= 0 ? 0 : number;
        size = size <= 0 ? 10 : size;
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(number, size, Sort.by(order));
        Page<K8sApp> page = k8sAppRepository.findAll(pageRequest);
        List<K8sApp> content = page.getContent();
        List<Integer> userIds = ServiceUtil.fetchListProperty(content, K8sApp::getUserId);

        R<List<SysUserDto>> userList = sysUserFeignService.getByIds(userIds);
        Map<Integer, SysUserDto> userDtoMap = ServiceUtil.convertToMap(userList.getData(), SysUserDto::getId);
        List<K8sAppVo> k8sAppVos = content.stream().map(item -> {
            K8sAppVo k8sAppVo = new K8sAppVo();
            if (userDtoMap.containsKey(item.getUserId())) {
                SysUserDto sysUserDto = userDtoMap.get(item.getUserId());
                k8sAppVo.setNickname(sysUserDto.getNickName());
            }
            BeanUtils.copyProperties(item, k8sAppVo);
            return k8sAppVo;
        }).collect(Collectors.toList());

        return PageModel.<K8sAppVo>builder()
                .count((int) page.getTotalElements())
                .content(k8sAppVos)
                .number(number + 1)
                .size(size)
                .build();
    }
    @Override
    public List<K8sAppVo> listByUserId(Integer userId, K8sStatus k8sStatus) {
        K8sApp k8sApp = K8sApp.builder()
                .userId(userId)
                .status(k8sStatus)
                .build();
        List<K8sApp> content = k8sAppRepository.findAll(Example.of(k8sApp));
        List<K8sAppVo> k8sAppVos = content.stream().map(item -> {
            K8sAppVo k8sAppVo = new K8sAppVo();
            BeanUtils.copyProperties(item, k8sAppVo);
            return k8sAppVo;
        }).collect(Collectors.toList());
        return k8sAppVos;
    }
    @Override
    public PageModel<K8sAppVo> page(SysUserDto user,Integer number, Integer size, List<QueryCriteriaVo> criteriaVos) {
        number -= 1;
        number = number <= 0 ? 0 : number;
        size = size <= 0 ? 10 : size;
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(number, size, Sort.by(order));


        K8sApp k8sApp = new K8sApp();
        if(!user.getLoginName().equals("admin")){
            k8sApp = K8sApp.builder()
                    .userId(user.getId())
                    .build();
        }
        Page<K8sApp> page = k8sAppRepository.findAll(Example.of(k8sApp),pageRequest);

        List<K8sApp> content = page.getContent();
        List<Integer> userIds = ServiceUtil.fetchListProperty(content, K8sApp::getUserId);

        R<List<SysUserDto>> userList = sysUserFeignService.getByIds(userIds);
        Map<Integer, SysUserDto> userDtoMap = ServiceUtil.convertToMap(userList.getData(), SysUserDto::getId);
        List<K8sAppVo> k8sAppVos = content.stream().map(item -> {
            K8sAppVo k8sAppVo = new K8sAppVo();
            if (userDtoMap.containsKey(item.getUserId())) {
                SysUserDto sysUserDto = userDtoMap.get(item.getUserId());
                k8sAppVo.setNickname(sysUserDto.getName());
            }
            BeanUtils.copyProperties(item, k8sAppVo);
            return k8sAppVo;
        }).collect(Collectors.toList());


        return PageModel.<K8sAppVo>builder()
                .count((int) page.getTotalElements())
                .content(k8sAppVos)
                .number(number + 1)
                .size(size)
                .build();
    }
    @Override
    public K8sApp findById(String id) {
        return k8sAppRepository.findById(id).orElse(null);
    }

    @Override
    public K8sApp del(String s) {
        K8sApp k8sApp = findById(s);
        if(k8sApp.getStatus()!=null && k8sApp.getStatus().equals(K8sStatus.RUNNING)){
            throw new RuntimeException("程序正在运行，不能删除！");
        }
        k8sAppRepository.delete(k8sApp);
        portsService.delPort(k8sApp.getNodePort());
        return k8sApp;
    }
    public K8sApp save(K8sApp k8sApp) {
        return k8sAppRepository.save(k8sApp);
    }
    @Override
    public K8sApp save(K8sApp k8sApp, SysUserDto user) {
        Ports ports = portsService.getPorts();
        ports.setUserId(user.getId());
        portsService.save(ports);

        String uuid = "k8s-"+UUID.randomUUID();
        k8sApp.setPodName(uuid);
        k8sApp.setTargetPort(k8sApp.getPort());
        k8sApp.setNodePort(ports.getPort());
        k8sApp.setUserId(user.getId());
        String url ="/cloud-gateway/app/"+k8sApp.getNodePort()+"/";
        k8sApp.setUrl(url);

        check(k8sApp);
        return k8sAppRepository.save(k8sApp);
    }

    private void check(K8sApp k8sApp) {
        Map<String,String> volumes= new HashMap<>();
        Map<String,String> env= new HashMap<>();
        List<Mounts> mounts= new ArrayList<>();
        if(k8sApp.getImage().contains("code-server")){
            log.info("====>create image: rstudio");
//            env.put("PUID",uid);
//            env.put("PGID",uid);
            List<String> pvcList = mapK8sProperties.getPvc();
            for (String pvc:pvcList){
                volumes.put(pvc,pvc);
            }

            Map<String, String> vscode = mapK8sProperties.getVscode();
            if(vscode!=null && !"".equals(vscode)){
                for (Map.Entry<String,String> item : vscode.entrySet()){
                    for (String pvc:pvcList){
                        String key = item.getKey();
                        String value =item.getValue();
                        if(item.getValue().equals("workspace")){
                            value = item.getValue()+"/"+k8sApp.getUserId();
                            mounts.add(Mounts.builder().name(pvc).mountPath("/data/"+value).subPath(value).build());
                        }
                        if(key.startsWith(pvc)){
                            key = key.replace(pvc+"-","");
                            mounts.add(Mounts.builder().name(pvc).mountPath("/"+key.replaceAll("-","/")).subPath(value).build());
                            break;
                        }
                        mounts.add(Mounts.builder().name(pvc).mountPath("/data").readOnly(true).build());

                    }
                }
            }


        }else if (k8sApp.getImage().contains("rstudio")){
            log.info("====>create image: code-server");

//            env.put("USERID",uid);
//            env.put("GROUPID",uid);
            env.put("DISABLE_AUTH","true");
            List<String> pvcList = mapK8sProperties.getPvc();
            for (String pvc:pvcList){
                volumes.put(pvc,pvc);
            }
            Map<String, String> rstudio = mapK8sProperties.getRstudio();
            if(rstudio!=null && !"".equals(rstudio)){
                for (Map.Entry<String,String> item : rstudio.entrySet()){
                    for (String pvc:pvcList){
                        String key = item.getKey();
                        String value =item.getValue();
                        if(item.getValue().equals("workspace")){
                            value = item.getValue()+"/"+k8sApp.getUserId();
                            mounts.add(Mounts.builder().name(pvc).mountPath("/data/"+value).subPath(value).build());
                        }
                        if(key.startsWith(pvc)){
                            key = key.replace(pvc+"-","");
                            mounts.add(Mounts.builder().name(pvc).mountPath("/"+key.replaceAll("-","/")).subPath(value).build());
                            break;
                        }
                        mounts.add(Mounts.builder().name(pvc).mountPath("/data").subPath("/data").readOnly(true).build());

                    }
                }
            }
        }

//        k8sApp.setEnv(env);
//        k8sApp.setVolumes(volumes);
        k8sApp.setMounts(mounts);

    }

    @Override
    public K8sApp update(String id, K8sApp k8sAppParams) {
        K8sApp k8sApp = findById(id);
        BeanUtils.copyProperties(k8sAppParams, k8sApp, "id");
        return k8sAppRepository.save(k8sApp);
    }


    @Override
    public K8sApp install(String imageId, SysUserDto user) {
        Images images = imagesService.findById(imageId);

        Ports ports = portsService.getPorts();
        ports.setUserId(user.getId());
        portsService.save(ports);
        K8sApp k8sApp = new K8sApp();


        String cmd = images.getCmd();
        if(cmd!=null){
            cmd = cmd.replace("${port}",String.valueOf(ports.getPort()));
            String[] splitCmd = cmd.split(" ");
            List<String> cmdList = Arrays.asList(splitCmd);
            k8sApp.setCommand(cmdList);
        }


        k8sApp.setRunUser(images.getRunUser());
        k8sApp.setRunGroup(images.getRunGroup());

        k8sApp.setImage(images.getImages());
        k8sApp.setName(images.getName());
        k8sApp.setPort(images.getTargetPort());

        String uuid = "k8s-"+UUID.randomUUID();
        k8sApp.setPodName(uuid);
        k8sApp.setStatus(K8sStatus.STOP);

        k8sApp.setTargetPort(images.getTargetPort());
        k8sApp.setNodePort(ports.getPort());
        k8sApp.setUserId(user.getId());
        String  url ="/cloud-gateway/app/"+k8sApp.getNodePort()+"/";
        if(images.getIsRelative()!=null && images.getIsRelative()){
            url ="/cloud-gateway/rapp/"+k8sApp.getNodePort()+"/";
        }

        k8sApp.setUrl(url);

        if(images.getMounts()!=null){
            List<Mounts> mounts = images.getMounts().stream().map(item->{
                String mountPath = item.getMountPath();
                String subPath = item.getSubPath();
                if(mountPath!=null){
                    mountPath = mountPath.replace("${user}", String.valueOf(user.getId()));
                    item.setMountPath(mountPath);

                }

                if(subPath!=null){
                    subPath = subPath.replace("${user}", String.valueOf(user.getId()));
                    item.setSubPath(subPath);
                }
                return item;

            }).collect(Collectors.toList());
            k8sApp.setMounts(mounts);
        }


        k8sApp.setEnv(images.getEnv());
        k8sApp.setVolumes(images.getVolumes());

        return k8sAppRepository.save(k8sApp);

//        mounts.add(Mounts.builder().name(pvc).mountPath("/data/"+value).subPath(value).build());


//        if(k8sApp.getImage().contains("code-server")){
//            log.info("====>create image: rstudio");
//            env.put("PUID",uid);
//            env.put("PGID",uid);
//            List<String> pvcList = mapK8sProperties.getPvc();
//            for (String pvc:pvcList){
//                volumes.put(pvc,pvc);
//            }
//
//            Map<String, String> vscode = mapK8sProperties.getVscode();
//            if(vscode!=null && !"".equals(vscode)){
//                for (Map.Entry<String,String> item : vscode.entrySet()){
//                    for (String pvc:pvcList){
//                        String key = item.getKey();
//                        String value =item.getValue();
//                        if(item.getValue().equals("workspace")){
//                            value = item.getValue()+"/"+k8sApp.getUserId();
//                            mounts.add(Mounts.builder().name(pvc).mountPath("/data/"+value).subPath(value).build());
//                        }
//                        if(key.startsWith(pvc)){
//                            key = key.replace(pvc+"-","");
//                            mounts.add(Mounts.builder().name(pvc).mountPath("/"+key.replaceAll("-","/")).subPath(value).build());
//                            break;
//                        }
//                        mounts.add(Mounts.builder().name(pvc).mountPath("/data").readOnly(true).build());
//
//                    }
//                }
//            }
//
//
//        }else if (k8sApp.getImage().contains("rstudio")){
//            log.info("====>create image: code-server");
//
//            env.put("USERID",uid);
//            env.put("GROUPID",uid);
//            env.put("DISABLE_AUTH","true");
//            List<String> pvcList = mapK8sProperties.getPvc();
//            for (String pvc:pvcList){
//                volumes.put(pvc,pvc);
//            }
//            Map<String, String> rstudio = mapK8sProperties.getRstudio();
//            if(rstudio!=null && !"".equals(rstudio)){
//                for (Map.Entry<String,String> item : rstudio.entrySet()){
//                    for (String pvc:pvcList){
//                        String key = item.getKey();
//                        String value =item.getValue();
//                        if(item.getValue().equals("workspace")){
//                            value = item.getValue()+"/"+k8sApp.getUserId();
//                            mounts.add(Mounts.builder().name(pvc).mountPath("/data/"+value).subPath(value).build());
//                        }
//                        if(key.startsWith(pvc)){
//                            key = key.replace(pvc+"-","");
//                            mounts.add(Mounts.builder().name(pvc).mountPath("/"+key.replaceAll("-","/")).subPath(value).build());
//                            break;
//                        }
//                        mounts.add(Mounts.builder().name(pvc).mountPath("/data").subPath("/data").readOnly(true).build());
//
//                    }
//                }
//            }
//        }


    }

    //    kubectl port-forward  --address 0.0.0.0 svc/test-pod 31000:80
    @Override
    public K8sApp startK8sApp(String id){
        K8sApp k8sApp = findById(id);
        k8sApp.setStatus(K8sStatus.RUNNING);
        save(k8sApp);
        CoreV1Api api = k8sApiService.getApi();

        V1Pod v1Pod = k8sApiService.findPodByName(api, namespace, k8sApp.getPodName());
        if(v1Pod==null){
            Map<String,String> labels = new HashMap<>();
            labels.put("userId",String.valueOf(k8sApp.getUserId()));
            labels.put("type","webApp");
//            k8sApiService.delPodByName(api,namespace,k8sApp.getPodName());
            k8sApiService.createPod(api, namespace, Pod.builder()
                    .labels(labels)
                    .name(k8sApp.getPodName())
                    .image(k8sApp.getImage())
                    .env(k8sApp.getEnv())
                    .runUser(k8sApp.getRunUser())
                    .runGroup(k8sApp.getRunGroup())
                    .mounts(k8sApp.getMounts())
                    .volumes(k8sApp.getVolumes())
                    .command(k8sApp.getCommand())
                    .build(), new ApiCallback<V1Pod>() {
                @Override
                public void onFailure(ApiException e, int i, Map<String, List<String>> map) {

                }

                @Override
                public void onSuccess(V1Pod v1Pod, int i, Map<String, List<String>> map) {
                    String date = map.get("date").get(0);
                    k8sApp.setStartPodDate(date);
                    k8sApp.setStopPodDate(null);
                    save(k8sApp);
                }

                @Override
                public void onUploadProgress(long l, long l1, boolean b) {

                }

                @Override
                public void onDownloadProgress(long l, long l1, boolean b) {

                }
            });
        }
        V1Service v1Service = k8sApiService.findServiceByName(api, namespace, k8sApp.getPodName());
        if(v1Service==null){
//            k8sApiService.delServiceByName(api,namespace, k8sApp.getPodName());
            k8sApiService.createService(api, namespace, KService.builder()
                    .name(k8sApp.getPodName())
                    .nodePort(k8sApp.getNodePort())
                    .targetPort(k8sApp.getTargetPort())
                    .port(k8sApp.getPort())
                    .build(), new ApiCallback<V1Service>() {
                @Override
                public void onFailure(ApiException e, int i, Map<String, List<String>> map) {

                }

                @Override
                public void onSuccess(V1Service v1Service, int i, Map<String, List<String>> map) {
                    String date = map.get("date").get(0);
                    k8sApp.setStartPodDate(date);
                    k8sApp.setStopServiceDate(null);
                    save(k8sApp);

                }

                @Override
                public void onUploadProgress(long l, long l1, boolean b) {

                }

                @Override
                public void onDownloadProgress(long l, long l1, boolean b) {

                }
            });
        }
        return k8sApp;
    }


    @Override
    public K8sApp stopK8sApp(String id){
        K8sApp k8sApp = findById(id);
        CoreV1Api api = k8sApiService.getApi();
        V1Pod v1Pod = k8sApiService.findPodByName(api, namespace, k8sApp.getPodName());
        if(v1Pod!=null){
            k8sApiService.delPodByName(api, namespace, k8sApp.getPodName(), new ApiCallback<V1Pod>() {
                @Override
                public void onFailure(ApiException e, int i, Map<String, List<String>> map) {

                }

                @Override
                public void onSuccess(V1Pod v1Pod, int i, Map<String, List<String>> map) {
                    String date = map.get("date").get(0);
                    k8sApp.setStartPodDate(null);
                    k8sApp.setStopServiceDate(date);
                    save(k8sApp);
                }

                @Override
                public void onUploadProgress(long l, long l1, boolean b) {

                }

                @Override
                public void onDownloadProgress(long l, long l1, boolean b) {

                }
            });

        }
        V1Service v1Service = k8sApiService.findServiceByName(api, namespace, k8sApp.getPodName());
        if(v1Service!=null){
            k8sApiService.delServiceByName(api, namespace, k8sApp.getPodName(), new ApiCallback<V1Service>() {
                @Override
                public void onFailure(ApiException e, int i, Map<String, List<String>> map) {

                }

                @Override
                public void onSuccess(V1Service v1Service, int i, Map<String, List<String>> map) {
                    String date = map.get("date").get(0);
                    k8sApp.setStartPodDate(null);
                    k8sApp.setStopServiceDate(date);
                    save(k8sApp);
                }

                @Override
                public void onUploadProgress(long l, long l1, boolean b) {

                }

                @Override
                public void onDownloadProgress(long l, long l1, boolean b) {

                }
            });

        }
        k8sApp.setStatus(K8sStatus.STOP);
        save(k8sApp);
        return k8sApp;
    }
}
