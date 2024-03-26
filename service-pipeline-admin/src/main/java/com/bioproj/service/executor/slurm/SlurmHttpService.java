package com.bioproj.service.executor.slurm;

import com.bioproj.service.executor.slurm.model.SlurmJobs;
import com.bioproj.service.executor.slurm.model.SlurmPing;
import retrofit2.Call;
import retrofit2.http.GET;

public interface SlurmHttpService {
    @GET("/slurm/v0.0.36/jobs")
    Call<SlurmJobs> jobs();

    Call<SlurmPing> ping();
}
