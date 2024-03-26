package com.bioproj.domain;

import lombok.Data;

@Data
public class WorkflowLoad {
    private long cpus;
    private long cpuTime;
    private long cpuLoad;
    private long memoryRss;
    private long memoryReq;
    private long readBytes;
    private long writeBytes;
    private long volCtxSwitch;
    private long invCtxSwitch;
     // @VerFST(1) BigDecimal cost
    private long loadTasks;
    private long loadCpus;
    private long loadMemory;
    private long peakCpus;
    private long peakTasks;
    private long peakMemory;
}
