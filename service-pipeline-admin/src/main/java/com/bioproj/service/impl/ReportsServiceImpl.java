package com.bioproj.service.impl;

import com.bioproj.pojo.Reports;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.repository.ReportsRepository;
import com.bioproj.service.IReportsService;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.dto.SysUserDto;
import com.mbiolance.cloud.auth.domain.vo.QueryCriteriaVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportsServiceImpl implements IReportsService {

    @Autowired
    ReportsRepository reportsRepository;

    @Override
    public Reports findById(String id){
        return reportsRepository.findById(id).orElse(null);
    }

    @Override
    public Reports save(Reports reports){
        return reportsRepository.save(reports);
    }
    @Override
    public List<Reports> saveAll(List<Reports> reports){
        return reportsRepository.saveAll(reports);
    }

    @Override
    public List<Reports> findByTaskId(String workflowId){
        return reportsRepository.findAll(Example.of(Reports.builder()
                        .workflowId(workflowId)
                .build()));
    }
    @Override
    public Page<Reports> pageByWorkflowId(String workflowId, Pageable pageable){
        Page<Reports> reportsPage = reportsRepository.findAll(Example.of(Reports.builder()
                .workflowId(workflowId)
                .build()), pageable);
        return reportsPage;
    }
    @Override
    public List<Reports> delWorkflowId(String workflowId){
        List<Reports> reports = reportsRepository.findAll(Example.of(Reports.builder()
                .workflowId(workflowId)
                .build()));
        reportsRepository.deleteAll(reports);
        return reports;
    }

    @Override
    public PageModel<Reports> page(Integer number, Integer size, List<QueryCriteriaVo> criteriaVos) {
        number -= 1;
        number = number <= 0 ? 0 : number;
        size = size <= 0 ? 10 : size;
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(number, size, Sort.by(order));
        Page<Reports> page = reportsRepository.findAll(pageRequest);

        return PageModel.<Reports>builder()
                .count((int) page.getTotalElements())
                .content(page.getContent())
                .number(number + 1)
                .size(size)
                .build();
    }

    @Override
    public PageModel<Reports> page(SysUserDto user, Integer number, Integer size, List<QueryCriteriaVo> criteriaVos) {
        number -= 1;
        number = number <= 0 ? 0 : number;
        size = size <= 0 ? 10 : size;
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(number, size, Sort.by(order));

        Reports reports  = new Reports();
        if(!user.getLoginName().equals("admin")){
            reports = Reports.builder()
                    .userId(user.getId())
                    .build();
        }

        Page<Reports> page = reportsRepository.findAll(Example.of(reports),pageRequest);

        return PageModel.<Reports>builder()
                .count((int) page.getTotalElements())
                .content(page.getContent())
                .number(number + 1)
                .size(size)
                .build();

    }

    @Override
    public Reports del(String s) {
        Reports reports = findById(s);
        reportsRepository.delete(reports);
        return reports;
    }
    @Override
    public Reports update(String id, Reports reportsParam) {
        Reports reports = findById(id);
        BeanUtils.copyProperties(reportsParam, reports, "id");
        return reportsRepository.save(reports);
    }
}
