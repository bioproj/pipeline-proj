package com.bioproj.service.impl;

import com.bioproj.pojo.reference.Reference;
import com.bioproj.repository.ReferenceRepository;
import com.bioproj.service.IReferenceService;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.vo.QueryCriteriaVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReferenceServiceImpl implements IReferenceService {

    @Autowired
    ReferenceRepository referenceRepository;

    @Override
    public PageModel<Reference> page(Integer number, Integer size, List<QueryCriteriaVo> criteriaVos) {
        number -= 1;
        number = number <= 0 ? 0 : number;
        size = size <= 0 ? 10 : size;
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(number, size, Sort.by(order));
        Page<Reference> page = referenceRepository.findAll(pageRequest);

        return PageModel.<Reference>builder()
                .count((int) page.getTotalElements())
                .content(page.getContent())
                .number(number + 1)
                .size(size)
                .build();
    }
    @Override
    public Reference findById(String id) {
        return referenceRepository.findById(id).orElse(null);
    }

    @Override
    public Reference del(String s) {
        Reference reference = findById(s);
        referenceRepository.delete(reference);
        return reference;
    }

    @Override
    public Reference save(Reference reference) {
        return referenceRepository.save(reference);
    }

    @Override
    public Reference update(String id, Reference appParam) {
        Reference reference = findById(id);
        BeanUtils.copyProperties(appParam, reference, "id");
        return referenceRepository.save(reference);
    }
}
