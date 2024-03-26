package com.bioproj.k8s;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KService {
    private String name;
    private Integer port;
    private Integer targetPort;
    private Integer nodePort;
}
