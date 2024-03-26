package com.bioproj.service.images;

import com.bioproj.service.images.model.DockerRepositories;
import com.bioproj.service.images.model.DockerVersion;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DockerRegistryHttp {


    @GET("/v2/_catalog")
    Call<DockerRepositories> repositories();

    @GET("/v2/{repository}/tags/list")
    Call<DockerVersion> version(@Path("repository") String  repository);
}
