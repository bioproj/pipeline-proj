package com.bioproj.service;

import com.bioproj.pojo.reference.Reference;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.vo.QueryCriteriaVo;

import java.util.List;

public interface IReferenceService {
    PageModel<Reference> page(Integer number, Integer size, List<QueryCriteriaVo> criteriaVos);

    Reference findById(String id);

    Reference del(String s);

    Reference save(Reference reference);

    Reference update(String id, Reference appParam);
}
