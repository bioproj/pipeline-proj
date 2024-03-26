package com.bioproj.service.executor.flux;

import com.bioproj.pojo.task.Workflow;
import com.bioproj.service.executor.IExecutorsService;
import com.bioproj.service.executor.RetrofitFactory;
import com.bioproj.service.executor.flux.model.FluxJobs;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.io.IOException;
import java.util.List;

@Service
public  class FluxExecutorImpl implements IExecutorsService {


    public static FluxHttpService getExecutor() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
//                        .header(HttpHeaders.AUTHORIZATION, HttpHeaders.AUTHORIZATION_BEARER_X + "anXNFXA8ng0ri04DIAz99Vdd")
//                        .header(HttpHeaders.ZOTERO_API_VERSION, "3")
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        };
        return RetrofitFactory.createService("http://192.168.10.177:5000",interceptor,FluxHttpService.class);
    }



    @Override
    public void submit(Workflow app) {
        FluxHttpService fluxHttpService = getExecutor();
//        Call<List<Job>> jobs = fluxHttpService.jobs();

    }

    @Override
    public boolean ping() {
        return true;
    }

    @Override
    public List<Workflow> jobs() {
        try {
            FluxHttpService fluxHttpService = getExecutor();
            Call<FluxJobs> jobs = fluxHttpService.jobsLimit(5);
            FluxJobs fluxes = jobs.execute().body();
            System.out.println(fluxes);
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Boolean cancel(String id) {
        return true;
    }
    @Override
    public Boolean status(String id) {
        return null;
    }
    @Override
    public Boolean delete(Workflow task){
        return true;
    }
    @Override
    public Boolean stop(Workflow task) {
        return null;
    }

    @Override
    public void writeFile(Workflow task) {

    }
    @Override
    public Boolean runScript(Workflow task, String file) {
        return null;
    }
}
