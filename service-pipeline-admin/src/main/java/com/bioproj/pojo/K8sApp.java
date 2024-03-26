package com.bioproj.pojo;

import com.bioproj.domain.enums.K8sStatus;
import com.bioproj.domain.k8s.Env;
import com.bioproj.domain.k8s.Mounts;
import com.bioproj.domain.k8s.Volumes;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class K8sApp {

    @Id
    private String id;
    private String name;
    private String image;
    private String podName;
    private String serviceName;
    private Integer port;
    private Integer targetPort;
    private Integer nodePort;
    private K8sStatus status;
    private String url;
    private Integer userId;
    private String startPodDate;
    private String stopPodDate;

    private String startServiceDate;
    private String stopServiceDate;

    private List<Volumes> volumes;
    private List<Env> env;
    private List<Mounts> mounts;
    private List<String> command;

    private Long runUser;
    private Long runGroup;


}
