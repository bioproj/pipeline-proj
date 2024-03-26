package com.bioproj.k8s;

import com.bioproj.domain.k8s.Mounts;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {
    private String name;
    private String image;
    private String workDir;
    private String restartPolicy;
    private String workflowId;
    private Long runUser;
    private Integer backoffLimit;
    private Integer ttlSecondsAfterFinished;
    private Long runGroup;
    private Map<String,String> volumes;
    private Map<String,String> env;
    private List<Mounts> mounts;
    private List<String> command;
    private Map<String, String> labels;
}
