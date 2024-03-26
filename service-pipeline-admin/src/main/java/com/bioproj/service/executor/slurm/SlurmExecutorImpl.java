package com.bioproj.service.executor.slurm;

import com.bioproj.pojo.task.Workflow;
import com.bioproj.service.executor.IExecutorsService;
import com.bioproj.service.executor.RetrofitFactory;
import com.bioproj.service.executor.slurm.model.SlurmJobs;
import com.bioproj.service.executor.slurm.model.SlurmPing;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.io.IOException;
import java.util.List;


@Service
public class SlurmExecutorImpl implements IExecutorsService {



    private static String slurmToken;
    @Value("${exec.slurmToken}")
    public  void setSlurmToken(String slurmToken) {
        SlurmExecutorImpl.slurmToken = slurmToken;
    }

    public static SlurmHttpService getExecutor() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("X-SLURM-USER-NAME", "admin")
                        .header("X-SLURM-USER-TOKEN", slurmToken)
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        };
        return RetrofitFactory.createService("http://192.168.10.177:6830",interceptor,SlurmHttpService.class);
    }

    @Override
    public void submit(Workflow app) {

    }

    @Override
    public boolean ping() {
        try {
            SlurmHttpService slurmHttpService = getExecutor();
            Call<SlurmPing> jobs = slurmHttpService.ping();
            SlurmPing slurmPing = jobs.execute().body();
            System.out.println();
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Workflow> jobs() {
        try {
            SlurmHttpService slurmHttpService = getExecutor();
            Call<SlurmJobs> jobs = slurmHttpService.jobs();
            SlurmJobs jobs1 = jobs.execute().body();
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
