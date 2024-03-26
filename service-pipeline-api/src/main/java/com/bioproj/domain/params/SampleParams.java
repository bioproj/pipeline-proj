package com.bioproj.domain.params;

import lombok.Data;

@Data
public class SampleParams {
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
