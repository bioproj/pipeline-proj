package com.bioproj.service;

import com.bioproj.pojo.Repos1;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface GiteaHttpService {

    @GET("/api/v1/orgs/{org}/repos")
    Call<List<Repos1>> getRepos(@Path("org") String org);



}
