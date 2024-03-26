package com.bioproj.k8s;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PodVo {
    private String name;
    private String taskName;
    private String processName;
    private String images;
    private String jobName;
    private String sessionId;
    private String runName;
    private String creationTimestamp;

}
