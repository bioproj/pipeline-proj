package com.bioproj.service.store;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.bioproj.pojo.Repos;
import com.bioproj.repository.StoreRepository;
import com.bioproj.utils.PageBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class StoreServiceImpl implements IStoreService {

    @Resource
    private StoreRepository wareHouseRepository;

    @Value("${giteaUrl}")
    String giteaUrl;


    @Value("${wareHouse.type}")
    String wareHouse_type;

    @Value("${wareHouse.orgNo}")
    String wareHouse_orgNo;

    @Value("${wareHouse.token}")
    String wareHouse_token;



//    @Value("${readmeTmpPath}")
//    String readmeTmpPath;


    @Resource(name = "restTemplate")
    private RestTemplate restTemplate;


    @Override
    public Repos save(Repos wareHouse){
       return wareHouseRepository.save(wareHouse);
    }

    public List<Map<String,String>>  list(){
        List<Map<String,String>> mapList = new ArrayList<>();
        switch (wareHouse_type){
            case "gitea":{
                String url = "http://192.168.10.177:3000/api/v1/orgs/"+wareHouse_orgNo+"/repos";
                JSONArray jsonArray = restTemplate.getForObject(url, JSONArray.class);
                for (Object o : jsonArray) {
                    Map<String,String> map = new HashMap<>();
                    JSONObject o1 = (JSONObject) o;
                    String id = o1.get("id").toString();
                    String name = o1.get("name").toString();
                    String description = o1.get("description").toString();
                    String clone_url =   o1.get("clone_url").toString();
                    map.put("id",id);
                    map.put("name",name);
                    map.put("description",description);
                    map.put("clone_url",clone_url);
                    mapList.add(map);
                }
                break;
            }
            case "gitee":{
                String url = "https://gitee.com/api/v5/orgs/"+wareHouse_orgNo+"/repos?access_token=" + wareHouse_token + "&type=all&page=1&per_page=100";
                JSONArray jsonArray = restTemplate.getForObject(url, JSONArray.class);
                for (Object object : jsonArray) {
                    Map<String,String> map = new HashMap<>();
                    JSONObject o1 = (JSONObject) object;
                    String id = o1.get("id").toString();
                    String name = o1.get("name").toString();
                    String description = o1.get("description").toString();
                    String clone_url = o1.get("html_url").toString();
                    map.put("id",id);
                    map.put("name",name);
                    map.put("description",description);
                    map.put("clone_url",clone_url);
                    mapList.add(map);
                }

                break;
            }
            case "github":{
                break;
            }
        }


        return mapList;
    }

    @Override
    public List<Repos> list_new() {
        List<Repos> mapList = new ArrayList<>();
        switch (wareHouse_type){
            case "gitea":{
                String url = "http://192.168.10.177:3000/api/v1/orgs/"+wareHouse_orgNo+"/repos";
                JSONArray jsonArray = restTemplate.getForObject(url, JSONArray.class);
                for (Object o : jsonArray) {
                    Repos map = new Repos();
                    JSONObject o1 = (JSONObject) o;
                    String id = o1.get("id").toString();
                    String name = o1.get("name").toString();
                    String description = o1.get("description").toString();
                    String clone_url =   o1.get("clone_url").toString();
                    String orgNo = ((JSONObject) o1.get("owner")).get("login").toString();
                    map.setId(id);
                    map.setName(name);
                    map.setDescription(description);
                    map.setCloneUrl(clone_url);
                    map.setOrgNo(orgNo);
                    mapList.add(map);
                }
                break;
            }
            case "gitee":{
                String url = "https://gitee.com/api/v5/orgs/"+wareHouse_orgNo+"/repos?access_token=" + wareHouse_token + "&type=all&page=1&per_page=100";
                JSONArray jsonArray = restTemplate.getForObject(url, JSONArray.class);
                for (Object object : jsonArray) {
                    Repos map = new Repos();
                    JSONObject o1 = (JSONObject) object;
                    String id = o1.get("id").toString();
                    String name = o1.get("name").toString();
                    String description = o1.get("description").toString();
                    String clone_url = o1.get("html_url").toString();
                    String orgNo = ((JSONObject) o1.get("namespace")).get("path").toString();
                    String project_creator = o1.get("project_creator").toString();
                    map.setId(id);
                    map.setName(name);
                    map.setDescription(description);
                    map.setCloneUrl(clone_url);
                    map.setProjectCreator(project_creator);
                    map.setOrgNo(orgNo);
                    mapList.add(map);
                }
                break;
            }
            case "github":{
                break;
            }
        }


        return mapList;
    }

    @Override
    public List<Repos> list_db() {
        return wareHouseRepository.findAll();
    }

    public Page<Repos> page(@PageableDefault(sort = {"id"},direction = DESC) Pageable pageable){
        return wareHouseRepository.findAll(pageable);
//        PageBean<Map<String,String>> pageBean = new PageBean<>();
//        int pageNumber = pageable.getPageNumber();
//        int pageSize = pageable.getPageSize();
//        pageBean.setPageNo(pageNumber);
//        pageBean.setPageSize(pageSize);
//        Integer  skip= 0;
//        if (pageNumber <= 1) {
//            skip = 0;
//        }else {
//            skip = pageSize * pageNumber - pageSize;
//        }
//        List<WareHouse> data = this.list();
//        pageBean.setTotalRecords(data.size());
//        List<WareHouse> mapList = data.stream().skip(skip).limit(pageSize).collect(Collectors.toList());
//        pageBean.setList(mapList);
//        return pageBean;
    }

    @Override
    public PageBean<Repos> page_new(Pageable pageable) {
        PageBean<Repos> pageBean = new PageBean<>();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        pageBean.setPageNo(pageNumber);
        pageBean.setPageSize(pageSize);
        Integer  skip= 0;
        if (pageNumber <= 1) {
            skip = 0;
        }else {
            skip = pageSize * pageNumber - pageSize;
        }
        List<Repos> data = this.list_db();
        pageBean.setTotalRecords(data.size());
        List<Repos> mapList = data.stream().skip(skip).limit(pageSize).collect(Collectors.toList());
        pageBean.setList(mapList);
        return pageBean;
    }

    @Override
    public Page<Repos> page_db(Pageable pageable) {
        return wareHouseRepository.findAll(pageable);
    }

    @Override
    public List<Repos> saveAll(List<Repos> data){
        List<Repos> wareHouses = wareHouseRepository.saveAll(data);
        return wareHouses;
    }

    @Override
    public void delALl() {
        wareHouseRepository.deleteAll();
    }

    @Override
    public Repos findById(String id) {
        return wareHouseRepository.findById(id).orElse(null);
    }
    @Override
    public List<Repos> listAll() {
        return wareHouseRepository.findAll();
    }

    @Override
    public List<String> branches(String id) {
        List<String> list = new ArrayList<>();
        Repos wareHouse = wareHouseRepository.findById(id).orElse(null);
        String orgNo = wareHouse.getOrgNo();
        String name = wareHouse.getName();
        switch (wareHouse_type){
            case "gitea":{
                String url = "http://192.168.10.177:3000/api/v1/repos/"+orgNo+"/"+name+"/branches";
                JSONArray jsonArray = restTemplate.getForObject(url , JSONArray.class);
                for (Object object : jsonArray) {
                    JSONObject o1 = (JSONObject) object;
                    String name1 = o1.get("name").toString();
                    list.add(name1);
                }
                break;
            }
            case "gitee":{
                String url = "https://gitee.com/api/v5/repos/"+orgNo+"/"+name+"/branches?access_token=" + wareHouse_token;
                JSONArray jsonArray = restTemplate.getForObject(url , JSONArray.class);
                for (Object object : jsonArray) {
                    JSONObject o1 = (JSONObject) object;
                    String name1 = o1.get("name").toString();
                    list.add(name1);
                }
                break;
            }
            case "github":{
                break;
            }
        }
        return list;
    }

    @Override
    public void readme(String id, HttpServletResponse response) {
        String contentBase64 = "";
        Repos wareHouse = wareHouseRepository.findById(id).orElse(null);
        String orgNo = wareHouse.getOrgNo();
        String name = wareHouse.getName();
        switch (wareHouse_type){
            case "gitea":{
                String url = "http://192.168.10.177:3000/api/v1/repos/"+orgNo+"/"+name+"/contents/README.md";
                JSONObject jsonObject = restTemplate.getForObject(url , JSONObject.class);
                contentBase64 = jsonObject.get("content").toString();
                break;
            }
            case "gitee":{
                String url = "https://gitee.com/api/v5/repos/"+orgNo+"/"+name+"/readme?access_token=" + wareHouse_token;
                JSONObject jsonObject = restTemplate.getForObject(url , JSONObject.class);
                contentBase64 = jsonObject.get("content").toString();
                break;
            }
            case "github":{
                break;
            }
        }
//        String path = readmeTmpPath + File.separator + name ;
//        File file = new File(path);
//        Base64Utils.encodeBase64File(".txt",file);
//        Base64Utils.decodeBase64String(".txt", contentBase64, path);
//        String filePath = path + ".txt";
//        File file_new = new File(filePath);
//        InputStream inputStream = null;
//        try {
//            inputStream = FileUtils.file2InputStream(file_new);
//            response.setContentType("application/txt;charset=utf-8");
//            byte[] read = IOUtils.read(inputStream);
//            IoUtil.write(response.getOutputStream(),true,read);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public Repos del(String s) {
        Repos repos = findById(s);
        wareHouseRepository.delete(repos);
        return repos;
    }

    @Override
    public Repos update(String id, Repos reposParam) {
        Repos repos = findById(id);
        BeanUtils.copyProperties(reposParam, repos, "id");
        return wareHouseRepository.save(repos);
    }



}
