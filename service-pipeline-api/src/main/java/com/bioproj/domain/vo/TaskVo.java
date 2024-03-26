package com.bioproj.domain.vo;

import com.bioproj.domain.enums.TaskStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaskVo {
    private String id;
    private String workflowId;
    private Integer taskId;
    private String dataId;
    private TaskStatus status;
    private String sessionId;
    private String projectDir;
    private String profile;
    private String homeDir;
    private String container;
    private String commitId;
    private String repository;
    private String containerEngine;
    private String scriptFile;
    private String userName;
    private String launchDir;
    private String runName;
    private String scriptId;
    private String revision;
    private String commandLine;
    private String projectName;
    private String scriptName;
    //  private   WorkflowStatus status;
    //Multi-value properties encoded as JSON
    private String configFiles;
    private String configText;
    private String params;
//  private   WfManifest manifest;
//  private   WfNextflow nextflow;

    private Integer exitStatus;
    private String errorMessage;
    private String errorReport;
    private Long duration;
    //  private   WfStats stats;
    private Boolean deleted;
    private Boolean success;
    private String launchId;


    private String  dataKey;
    private String fastq1;
    private String fastq2;
    private String species;
    private String name;
    private String process;
    private String tag;
    private String workdir;
    //    private String status;
    private Integer exit;
    private String executor;
    private BigDecimal cost;
    private Long realtime;
    private Integer cpus;
    private String hash;
    private String nativeId;

    private String submit;
    private String start;
}
