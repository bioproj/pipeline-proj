package com.bioproj.service;

import com.bioproj.pojo.Modules;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.vo.QueryCriteriaVo;

import java.util.List;

public interface IModulesService {
    PageModel<Modules> page(Integer number, Integer size, List<QueryCriteriaVo> criteriaVos);

    Modules findById(String id);

    Modules del(String s);

    Modules save(Modules modules);

    Modules update(String id, Modules modulesParam);
}
