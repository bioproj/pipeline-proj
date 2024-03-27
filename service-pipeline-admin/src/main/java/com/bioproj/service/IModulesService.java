package com.bioproj.service;

import com.bioproj.domain.PageModel;
import com.bioproj.domain.QueryCriteriaVo;
import com.bioproj.pojo.Modules;

import java.util.List;

public interface IModulesService {
    PageModel<Modules> page(Integer number, Integer size, List<QueryCriteriaVo> criteriaVos);

    Modules findById(String id);

    Modules del(String s);

    Modules save(Modules modules);

    Modules update(String id, Modules modulesParam);
}
