package com.bioproj.k8s;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobVo {

    private String name;
    private String taskName;
    private String processName;
    private String images;
    private String creationTimestamp;
    private Integer ttlSecondsAfterFinished;
}
