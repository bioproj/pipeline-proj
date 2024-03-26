package com.bioproj.pojo;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection= "samples")
public class Samples {
    @Id
    private String id;
    private String workflowId;
//    private String  dataKey;
    private String number;
    private String fastq1;
    private String fastq2;
    private String species;
    private String name;
    private Integer userId;
}
