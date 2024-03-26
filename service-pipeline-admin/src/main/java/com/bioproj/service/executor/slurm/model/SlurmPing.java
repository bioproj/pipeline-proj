package com.bioproj.service.executor.slurm.model;

import lombok.Data;

import java.util.Map;

@Data
public class SlurmPing {
    @Data
    class Ping{
        private String hostname;
        private String ping;
        private String status;
        private String mode;
    }
    private SlurmMeta meta;
    private Map<String,String> errors;
    private Ping ping;

}
