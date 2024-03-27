package com.bioproj.service.impl;

import com.bioproj.domain.PageModel;
import com.bioproj.domain.QueryCriteriaVo;
import com.bioproj.domain.SysUserDto;
import com.bioproj.domain.vo.ApplicationVo;
import com.bioproj.domain.vo.K8sAppVo;
import com.bioproj.pojo.Application;
import com.bioproj.pojo.K8sApp;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.repository.AppRepository;
import com.bioproj.service.IApplicationService;
import com.bioproj.service.IProcessService;
import com.bioproj.service.IWorkflowService;
import com.bioproj.utils.ServiceUtil;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements IApplicationService {

    @Resource
    private AppRepository appRepository;


//    @Autowired
//    SysUserFeignService sysUserFeignService;
    @Autowired
    private IProcessService processService;

//    @Resource
//    private IWorkflowService workflowService;
    @Override
    public void save(Application app) {
        appRepository.save(app);
    }

    @Override
    public Application findById(String id) {
        return appRepository.findById(id).orElse(null);
    }

    @Override
    public Application update(String id, Application appParam) {
        return appRepository.save(appParam);
    }

    @Override
    public void del(String s) {
        appRepository.deleteById(s);
    }
    @Override
    public void del(Application application) {
        appRepository.delete(application);
    }
    @Override
    public List<Application> findAll() {
        return appRepository.findAll();
    }

    @Override
    public Page<Application> page(Pageable pageable) {
        return appRepository.findAll(pageable);
    }


    @Override
    public Page<Application> page(SysUserDto user, Pageable pageable) {
        Application application = new Application();
        if(!user.getLoginName().equals("admin")){
            application = Application.builder()
                    .userId(user.getId())
                    .build();
        }

        return appRepository.findAll(Example.of(application),pageable);
    }

    @Override
    public PageModel<ApplicationVo> page(SysUserDto user, Integer number, Integer size, List<QueryCriteriaVo> criteriaVos) {
        number -= 1;
        number = number <= 0 ? 0 : number;
        size = size <= 0 ? 10 : size;
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "dateCreated");
        PageRequest pageRequest = PageRequest.of(number, size, Sort.by(order));


        Application application = new Application();
        if(!user.getLoginName().equals("admin")){
            application = Application.builder()
                    .userId(user.getId())
                    .build();
        }
        Page<Application> page = appRepository.findAll(Example.of(application),pageRequest);

        List<Application> content = page.getContent();
        List<Integer> userIds = ServiceUtil.fetchListProperty(content, Application::getUserId);

//        R<List<SysUserDto>> userList = sysUserFeignService.getByIds(userIds);
//        Map<Integer, SysUserDto> userDtoMap = ServiceUtil.convertToMap(userList.getData(), SysUserDto::getId);
        List<ApplicationVo> k8sAppVos = content.stream().map(item -> {
            ApplicationVo applicationVo = new ApplicationVo();
//            if (userDtoMap.containsKey(item.getUserId())) {
//                SysUserDto sysUserDto = userDtoMap.get(item.getUserId());
//                applicationVo.setNickname(sysUserDto.getName());
//            }
            BeanUtils.copyProperties(item, applicationVo);
            return applicationVo;
        }).collect(Collectors.toList());


        return PageModel.<ApplicationVo>builder()
                .count((int) page.getTotalElements())
                .content(k8sAppVos)
                .number(number + 1)
                .size(size)
                .build();
    }
    @Override
    public void fluxLaunch(Workflow taskApp) {
        processService.fluxLaunch(taskApp);
    }

    @Override
    public Application findByUserAndRepoId(SysUserDto user, String id) {
        Application application = appRepository.findOne(Example.of(Application.builder()
                .repoId(id)
                .userId(user.getId())
                .build())).orElse(null);
        return application;
    }
}
