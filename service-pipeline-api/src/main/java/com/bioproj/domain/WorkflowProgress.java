package com.bioproj.domain;

import lombok.Data;

@Data
public class WorkflowProgress {
    private int pending;
    private int submitted;
    private int running;
    private int succeeded;
    private int cached;
    private int failed;
    private int aborted;
    private int stored;
    private int ignored;
    private int retries;

    private long loadCpus;
    private long loadMemory;
    private int peakRunning;
    private long peakCpus;
    private long peakMemory;
}
