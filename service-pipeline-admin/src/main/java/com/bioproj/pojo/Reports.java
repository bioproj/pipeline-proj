package com.bioproj.pojo;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class Reports {
    @Id
    private String id;
    private String destination;
    private String display;
    private String workflowId;
    private Integer userId;
}
