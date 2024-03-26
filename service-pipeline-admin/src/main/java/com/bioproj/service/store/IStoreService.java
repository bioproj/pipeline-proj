package com.bioproj.service.store;

import com.bioproj.pojo.Repos;
import com.bioproj.utils.PageBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.DESC;

public interface IStoreService {

    Repos save(Repos wareHouse);

    List<Map<String,String>> list();

    List<Repos> list_new();

    List<Repos> list_db();

//    PageBean<Map<String,String>> page(@PageableDefault(sort = {"id"},direction = DESC) Pageable pageable);
    Page<Repos> page(Pageable pageable);

    PageBean<Repos> page_new(@PageableDefault(sort = {"id"},direction = DESC) Pageable pageable);

    Page<Repos> page_db(@PageableDefault(sort = {"id"},direction = DESC) Pageable pageable);

    List<Repos> saveAll(List<Repos> data);

    void  delALl();

    Repos findById(String id);

    List<Repos> listAll();

    List<String> branches(String id);

    void readme(String id, HttpServletResponse response);

    Repos del(String s);

    Repos update(String id, Repos reposParam);
}
