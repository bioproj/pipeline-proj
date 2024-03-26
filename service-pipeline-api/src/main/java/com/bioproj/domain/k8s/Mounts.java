package com.bioproj.domain.k8s;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mounts {
    private String name;
    private String mountPath;
    private String subPath;
    private Boolean readOnly;
}
