package com.bioproj.service.impl;

import com.bioproj.pojo.Repos;
import com.bioproj.service.GiteaHttpService;
import com.bioproj.service.IGiteaService;
import com.bioproj.service.store.IStoreService;
import com.bioproj.pojo.BaseResponse;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GiteaServiceImpl implements IGiteaService {

    @Value("${giteaUrl}")
    String giteaUrl;

    @Resource
    private IStoreService wareHouseService;

    @GetMapping("list")
    public BaseResponse list(){
        List<Map<String,String>> mapList = wareHouseService.list();
        return BaseResponse.ok(mapList);
    }


    public GiteaHttpService gitInstance(){

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        Request request = original.newBuilder()
//                                .header(HttpHeaders.AUTHORIZATION,HttpHeaders.AUTHORIZATION_BEARER_X + "anXNFXA8ng0ri04DIAz99Vdd")
//                                .header(HttpHeaders.ZOTERO_API_VERSION,"3")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    }
                })

                .build();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Retrofit retrofit  = new Retrofit.Builder()
                .baseUrl(giteaUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))

//                .client(okHttpClient)
                .build();

        GiteaHttpService giteaHttpService = retrofit.create(GiteaHttpService.class);
        return giteaHttpService;
    }
//    @Override
//    public List<Repos> listRepos(String org){
//        try {
//            GiteaHttpService giteaHttpService = gitInstance();
//            Call<List<Repos>> serviceRepos = giteaHttpService.getRepos(org);
//            List<Repos> repos = serviceRepos.execute().body();
//            return repos;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public List<Repos> listRepos(String org){

        List<Repos> list = new ArrayList<>();
//        List<Map<String,String>> maLpList = wareHouseService.list();
//
//        for (Map<String, String> map : maLpList) {
//            Repos repos = new Repos();
//            String id = map.get("id");
//            String name = map.get("name");
//            String description = map.get("description");
//            String clone_url = map.get("clone_url");
//            repos.setId(id);
//            repos.setName(name);
//            repos.setFullName(description);
//            repos.setCloneUrl(clone_url);
//            list.add(repos);
//        }
//        return list;
        List<Repos> wareHouses = wareHouseService.list_db();
        for (Repos wareHouse : wareHouses) {
            Repos repos = new Repos();
            repos.setId(wareHouse.getId());
            repos.setName(wareHouse.getName());
//            repos.setFullName(wareHouse.getDescription());
            repos.setCloneUrl(wareHouse.getCloneUrl());
            list.add(repos);
        }

        return list;
    }
}
