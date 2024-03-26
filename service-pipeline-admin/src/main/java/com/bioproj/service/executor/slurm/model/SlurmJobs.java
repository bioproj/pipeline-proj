package com.bioproj.service.executor.slurm.model;

import lombok.Data;

import java.util.List;

@Data
public class SlurmJobs {
    private List<SlurmJob> jobs;
    private SlurmMeta meta;
}
