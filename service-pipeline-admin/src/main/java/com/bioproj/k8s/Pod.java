package com.bioproj.k8s;

import com.bioproj.domain.k8s.Env;
import com.bioproj.domain.k8s.Mounts;
import com.bioproj.domain.k8s.Volumes;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pod {
    private String name;
    private String image;
    private String restartPolicy;
    private Long runUser;
    private Long runGroup;
//    private Map<String,String> volumes;
//    private Map<String,String> env;
    private List<Mounts> mounts;
    private List<Volumes> volumes;
    private List<Env> env;
    private List<String> command;
    private Map<String, String> labels;

}
