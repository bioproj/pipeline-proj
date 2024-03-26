package com.bioproj.service.images;

import com.alibaba.fastjson2.JSONObject;
import com.bioproj.domain.enums.ImageType;
import com.bioproj.domain.vo.ImagesVo;
import com.bioproj.pojo.Images;
import com.bioproj.repository.ImagesRepository;
import com.bioproj.service.executor.RetrofitFactory;
import com.bioproj.service.images.model.DockerRepositories;
import com.bioproj.service.images.model.DockerVersion;
import com.bioproj.utils.ServiceUtil;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.R;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImagesServiceImpl implements IImagesService {

    @Autowired
    private ImagesRepository imagesRepository;

    DockerRegistryHttp dockerRegistryHttp;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${dockerRegistryUrl}")
    String dockerRegistryUrl;

    public  DockerRegistryHttp getExecutor() {
        if(dockerRegistryHttp==null){
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
            dockerRegistryHttp = RetrofitFactory.createService(dockerRegistryUrl,interceptor,DockerRegistryHttp.class);
            return dockerRegistryHttp;
        }
        return dockerRegistryHttp;

    }


    public List<Images> list() {
        try {
            List<Images> imagesList = new ArrayList<>();
            Call<DockerRepositories> repositoriesCall = getExecutor().repositories();
            DockerRepositories repositoriesObj = repositoriesCall.execute().body();

            List<String> repositories = repositoriesObj.getRepositories();
            for (String  repository: repositories){
                Call<DockerVersion> dockerVersionCall = getExecutor().version(repository);
                DockerVersion dockerVersion = dockerVersionCall.execute().body();
                Images images = new Images();
                images.setImageType(ImageType.SOFTWARE);
                BeanUtils.copyProperties(dockerVersion, images);
                imagesList.add(images);
            }

            return imagesList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        List<DockerRegistry> data = new ArrayList<>();
//        String url = dockerRegistryUrl + "/v2/_catalog";
//
//        List<String> repositories = (List<String>) restTemplate.getForObject(url, JSONObject.class).get("repositories");
//        for (String repository : repositories) {
//            DockerRegistry dockerRegistry = new DockerRegistry();
//            String url2 = dockerRegistryUrl + "/v2/"+repository+"/tags/list";
//            List<String> repositories2 =(List<String>) restTemplate.getForObject(url2, JSONObject.class).get("tags");
//            dockerRegistry.setName(repository);
//            dockerRegistry.setVersion(repositories2);
//            data.add(dockerRegistry);
//        }
//        return data;
    }

    public List<Images> listByImageType(ImageType imageType){
        List<Images> imagesList = imagesRepository.findAll(Example.of(Images.builder()
                .imageType(imageType)
                .build()));
        return imagesList;
    }
    @Override
    public void refresh() {
//        imagesRepository.deleteAll();
        List<Images> images = this.list();
        List<Images> dbList = listByImageType(ImageType.SOFTWARE);
        imagesRepository.deleteAll(dbList);
        imagesRepository.saveAll(images);
//        Set<String> findImages = ServiceUtil.fetchProperty(images, Images::getName);
//        Set<String> dbImages = ServiceUtil.fetchProperty(dbList, Images::getName);

//        images.stream().filter(item->!dbImages.contains(item.getName()))

//        System.out.println();
//        imagesRepository.saveAll();
    }

    @Override
    public PageModel<Images> page(Integer number, Integer size, String name) {
        number -= 1;
        number = number <= 0 ? 0 : number;
        size = (size <= 0) ? 10 : size;
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(number, size, Sort.by(order));

        Images data = new Images();
        data.setName(name);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnorePaths("id")//忽略属性
                .withNullHandler(ExampleMatcher.NullHandler.IGNORE)
                .withIgnoreCase(true)//忽略大小写
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());//采用“包含匹配”的方式查询
        Example<Images> example = Example.of(data, exampleMatcher);
        Page<Images> page = imagesRepository.findAll(example, pageRequest);
        return PageModel.<Images>builder()
                .count((int) page.getTotalElements())
                .content(page.getContent())
                .number(number + 1)
                .size(size)
                .build();
    }


    @Override
    public PageModel<Images> page(Integer number, Integer size, ImagesVo imagesVo) {
        number -= 1;
        number = number <= 0 ? 0 : number;
        size = (size <= 0) ? 10 : size;
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(number, size, Sort.by(order));

        Images images = new Images();
        BeanUtils.copyProperties(imagesVo,images);
//        data.setName(name);
//        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
//                .withIgnorePaths("id")//忽略属性
//                .withNullHandler(ExampleMatcher.NullHandler.IGNORE)
//                .withIgnoreCase(true)//忽略大小写
//                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
//                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());//采用“包含匹配”的方式查询
        Example<Images> example = Example.of(images);
        Page<Images> page = imagesRepository.findAll(example, pageRequest);
        return PageModel.<Images>builder()
                .count((int) page.getTotalElements())
                .content(page.getContent())
                .number(number + 1)
                .size(size)
                .build();
    }

    @Override
    public Images byName(String name) {
        Images data = new Images();
        data.setName(name);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnorePaths("id")//忽略属性
                .withNullHandler(ExampleMatcher.NullHandler.IGNORE)
                .withIgnoreCase(true)//忽略大小写
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());//采用“包含匹配”的方式查询
        Example<Images> example = Example.of(data, exampleMatcher);
        List<Images> all = imagesRepository.findAll(example);
        return all == null ? null : all.get(0);
    }

    @ApiOperation("查询tag列表")
    @GetMapping("list")
    public R<PageModel<String>> list(@RequestParam(defaultValue = "1", required = false) Integer number
            , @RequestParam(defaultValue = "20", required = false) Integer size){

        String url = dockerRegistryUrl + "/v2/_catalog?n=" + 20000 + "&last=";
        List<String> repositories = (List<String>) restTemplate.getForObject(url, JSONObject.class).get("repositories");
        int skip = 0;
        if (number > 1) {
            skip = size * number - size;
        }
        List<String> list = repositories.stream().skip(skip).limit(size).collect(Collectors.toList());

        return R.ok(PageModel.<String>builder()
                .number(number).size(size)
                .content(list).count(repositories.size())
                .build());
    }
//    @ApiOperation("查询版本列表")
//    @GetMapping("taglist")
//    public R<PageModel<String>> taglist(@RequestParam(required = true) String tagName,@RequestParam(defaultValue = "1", required = false) Integer number
//            ,@RequestParam(defaultValue = "20", required = false) Integer size){
//        String url = dockerRegistryUrl + "/v2/"+tagName+"/tags/list";
//        List<String> repositories =(List<String>) restTemplate.getForObject(url, JSONObject.class).get("tags");
//        int skip = 0;
//        if (number > 1) {
//            skip = size * number - size;
//        }
//        List<String> list = repositories.stream().skip(skip).limit(size).collect(Collectors.toList());
//        return R.ok(PageModel.<String>builder()
//                .number(number).size(size)
//                .content(list).count(repositories.size())
//                .build());
//    }

    @Override
    public Images findById(String id) {
        return imagesRepository.findById(id).orElse(null);
    }

    @Override
    public Images del(String s) {
        Images images = findById(s);
        imagesRepository.delete(images);
        return images;
    }

    @Override
    public Images save(Images images) {
        return imagesRepository.save(images);
    }

    @Override
    public Images update(String id, Images imagesParams) {
        Images images = findById(id);
        BeanUtils.copyProperties(imagesParams, images, "id");
        images.setImageType(ImageType.APPLICATION);
        return imagesRepository.save(images);
    }


}
