package com.bioproj.service.executor.slurm.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SlurmJob {
    @SerializedName("job_id")
    private String jobId;

}
