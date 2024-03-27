package com.bioproj.service;

import com.bioproj.domain.PageModel;
import com.bioproj.domain.QueryCriteriaVo;
import com.bioproj.pojo.reference.Reference;

import java.util.List;


public interface IReferenceService {
    PageModel<Reference> page(Integer number, Integer size, List<QueryCriteriaVo> criteriaVos);

    Reference findById(String id);

    Reference del(String s);

    Reference save(Reference reference);

    Reference update(String id, Reference appParam);
}
