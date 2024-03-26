package com.bioproj.pojo;

import lombok.Data;

@Data
public class Progress {
    private Integer aborted=0;
    private Integer succeeded=0;
    private Integer peakMemory=0;
    private Integer peakCpus=0;
    private Integer failed=0;
    private Integer running=0;
    private Integer retries=0;
    private Integer peakRunning=0;
    private Integer cached=0;
    private Integer submitted=0;


    private Integer pending=0;
    private Integer ignored=0;
    private Integer loadCpus=0;
    private Integer loadMemory=0;

}
