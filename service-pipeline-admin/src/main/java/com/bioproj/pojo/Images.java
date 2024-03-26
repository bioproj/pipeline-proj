package com.bioproj.pojo;

import com.bioproj.domain.enums.ImageType;
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
public class Images {

    //ID
    @Id
    private String id;
    private String name;
    private String images;
    private String cmd;
    private Integer targetPort;
    private Boolean isRelative;
    private List<String> tags;
    private List<Volumes> volumes;
    private List<Env> env;
    private List<Mounts> mounts;
    private ImageType imageType;
    private Long runUser;
    private Long runGroup;
}
