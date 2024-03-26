package com.bioproj.service;

import com.bioproj.domain.vo.ApplicationVo;
import com.bioproj.domain.vo.K8sAppVo;
import com.bioproj.pojo.Application;
import com.bioproj.pojo.task.Workflow;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.dto.SysUserDto;
import com.mbiolance.cloud.auth.domain.vo.QueryCriteriaVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IApplicationService {

    void save(Application workflows);

    Application findById(String id);

    Application update(String id, Application workflowsParam);

    void del(String s);

    void del(Application application);

    List<Application> findAll();

    Page<Application> page(Pageable pageable);


    PageModel<ApplicationVo> page(SysUserDto user, Integer number, Integer size, List<QueryCriteriaVo> criteriaVos);

    void fluxLaunch(Workflow taskApp);


    Page<Application> page(SysUserDto user, Pageable pageable);

    Application findByUserAndRepoId(SysUserDto user, String id);
}
