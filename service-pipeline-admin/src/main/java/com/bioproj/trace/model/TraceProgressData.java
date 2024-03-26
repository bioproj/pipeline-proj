package com.bioproj.trace.model;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class TraceProgressData {
    static final public TraceProgressData EMPTY = new TraceProgressData();

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
    private List<TraceProgressDetail> processes;

}
