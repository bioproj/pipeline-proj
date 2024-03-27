package com.bioproj.service;

import com.bioproj.domain.PageModel;
import com.bioproj.domain.QueryCriteriaVo;
import com.bioproj.domain.SysUserDto;
import com.bioproj.pojo.Reports;
;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IReportsService {
    Reports findById(String id);

    Reports save(Reports reports);

    List<Reports> saveAll(List<Reports> reports);

    List<Reports> findByTaskId(String taskId);

    Page<Reports> pageByWorkflowId(String workflowId, Pageable pageable);

    List<Reports> delWorkflowId(String taskId);

    PageModel<Reports> page(Integer number, Integer size, List<QueryCriteriaVo> criteriaVos);
    PageModel<Reports> page(SysUserDto user, Integer number, Integer size, List<QueryCriteriaVo> criteriaVos);
    Reports del(String s);

    Reports update(String id, Reports reportsParam);
}
