package com.bioproj.pojo.vo;

import lombok.Data;

@Data
public class SampleVo {
    private String  dataKey;
    private String fastq1;
    private String fastq2;
    private String species;

    private String taskId;
}
