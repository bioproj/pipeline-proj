package com.bioproj.domain.k8s;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Volumes {
    private String key;
    private String value;
}
