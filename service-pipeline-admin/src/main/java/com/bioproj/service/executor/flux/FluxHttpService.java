package com.bioproj.service.executor.flux;

import com.bioproj.service.executor.flux.model.FluxJobs;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public  interface FluxHttpService {

    @GET("/v1/jobs?details=false&listing=false")
    Call<FluxJobs> jobs();

    @GET("/v1/jobs?details=false&listing=false")
    Call<FluxJobs> jobsLimit(@Query("limit") Integer limit);
}
