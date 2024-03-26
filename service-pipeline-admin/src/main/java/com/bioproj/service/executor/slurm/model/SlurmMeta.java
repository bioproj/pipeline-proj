package com.bioproj.service.executor.slurm.model;

import lombok.Data;

@Data
public class SlurmMeta {
    @Data
    class Plugin{
        private String type;
        private String name;
    }
    @Data
    class Slurm{
        private String release;
    }
    private Plugin plugin;
    private Slurm Slurm;
}

