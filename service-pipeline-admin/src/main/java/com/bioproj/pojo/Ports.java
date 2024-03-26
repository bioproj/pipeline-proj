package com.bioproj.pojo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class Ports {
    @Id
    private String id;
    private Integer userId;
    private Integer port;
    private Boolean isUse;
}
