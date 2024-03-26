package com.bioproj.service;

import com.bioproj.domain.enums.K8sStatus;
import com.bioproj.domain.vo.K8sAppVo;
import com.bioproj.pojo.K8sApp;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.dto.SysUserDto;
import com.mbiolance.cloud.auth.domain.vo.QueryCriteriaVo;

import java.util.List;

public interface IK8sAppService {
    List<K8sAppVo> listByUserId(Integer userId, K8sStatus k8sStatus);

    PageModel<K8sAppVo> page(SysUserDto user, Integer number, Integer size, List<QueryCriteriaVo> criteriaVos);
    PageModel<K8sAppVo> page(Integer number, Integer size, List<QueryCriteriaVo> criteriaVos);

    K8sApp findById(String id);

    K8sApp del(String s);

    K8sApp save(K8sApp k8sApp, SysUserDto user);

    K8sApp update(String id, K8sApp k8sAppParams);

    K8sApp startK8sApp(String id);

    K8sApp stopK8sApp(String id);

    K8sApp install(String imageId, SysUserDto user);
}
