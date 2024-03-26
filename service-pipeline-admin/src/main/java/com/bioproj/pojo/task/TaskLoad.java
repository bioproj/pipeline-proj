package com.bioproj.pojo.task;


import lombok.Data;

@Data
public class TaskLoad {
    private long loadCpus;
    private long loadMemory;
    private long peakCpus;
    private long peakTasks;
    private long peakMemory;
}
