package com.bioproj.service.impl;

import com.bioproj.domain.PageModel;
import com.bioproj.domain.QueryCriteriaVo;
import com.bioproj.pojo.Modules;
import com.bioproj.repository.ModulesRepository;
import com.bioproj.service.IModulesService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModulesServiceImpl implements IModulesService {

    @Autowired
    ModulesRepository modulesRepository;

    @Override
    public PageModel<Modules> page(Integer number, Integer size, List<QueryCriteriaVo> criteriaVos) {
        number -= 1;
        number = number <= 0 ? 0 : number;
        size = size <= 0 ? 10 : size;
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(number, size, Sort.by(order));
        Page<Modules> page = modulesRepository.findAll(pageRequest);

        return PageModel.<Modules>builder()
                .count((int) page.getTotalElements())
                .content(page.getContent())
                .number(number + 1)
                .size(size)
                .build();
    }
    @Override
    public Modules findById(String id) {
        return modulesRepository.findById(id).orElse(null);
    }

    @Override
    public Modules del(String s) {
        Modules modules = findById(s);
        modulesRepository.delete(modules);
        return modules;
    }

    @Override
    public Modules save(Modules modules) {
        return modulesRepository.save(modules);
    }

    @Override
    public Modules update(String id, Modules modulesParam) {
        Modules modules = findById(id);
        BeanUtils.copyProperties(modulesParam, modules, "id");
        return modulesRepository.save(modules);
    }
}
