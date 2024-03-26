package com.bioproj.controller;

import com.bioproj.domain.BaseResponse;
import com.bioproj.domain.vo.K8sAppVo;
import com.bioproj.pojo.K8sApp;
import com.bioproj.pojo.reference.Reference;
import com.bioproj.service.IK8sAppService;
import com.mbiolance.cloud.auth.common.SysUserInfoContext;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.R;
import com.mbiolance.cloud.auth.domain.dto.SysUserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/k8sApp")
@Slf4j
@Api(tags={"应用"})
public class K8sAppController {
    @Autowired
    IK8sAppService k8sAppService;

    @GetMapping
    public R<PageModel<K8sAppVo>> page(Integer number, Integer size) {
        SysUserDto user = SysUserInfoContext.getUser();
        PageModel<K8sAppVo> pageModel = k8sAppService.page(user,number, size, null);
        return R.ok(pageModel);
    }

    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable("id")String id){
        K8sApp k8sApp = k8sAppService.del(id);
        return BaseResponse.ok("删除成功！");
    }

    @ApiOperation("修改")
    @PostMapping("/update/{id}")
    public BaseResponse update(@PathVariable("id") String id,@RequestBody K8sApp k8sApp){
        k8sAppService.update(id,k8sApp);
        return BaseResponse.ok("修改成功!");
    }
    @ApiOperation("安装应用")
    @GetMapping("/install/{imageId}")
    public BaseResponse install(@PathVariable("imageId") String imageId){
        SysUserDto user = SysUserInfoContext.getUser();
        K8sApp k8sApp= k8sAppService.install(imageId,user);
        return BaseResponse.ok("安装成功!");
    }
    @ApiOperation("新增")
    @PutMapping
    public BaseResponse add(@RequestBody K8sApp k8sApp){
        SysUserDto user = SysUserInfoContext.getUser();
        k8sApp.setId(null);

        k8sAppService.save(k8sApp,user);
        return BaseResponse.ok("新增成功!");
    }
    @ApiOperation("查找")
    @GetMapping("/find/{id}")
    public R<K8sApp> find(@PathVariable("id") String  id){
        K8sApp k8sApp = k8sAppService.findById(id);
        return R.ok(k8sApp);
    }
    @ApiOperation("重启")
    @GetMapping("/startK8sApp/{id}")
    public BaseResponse startK8sApp(@PathVariable("id") String id){
        K8sApp k8sApp = k8sAppService.startK8sApp(id);
        return BaseResponse.ok(k8sApp);
    }

    @ApiOperation("重启")
    @GetMapping("/stopK8sApp/{id}")
    public BaseResponse stopK8sApp(@PathVariable("id") String id){
        K8sApp k8sApp = k8sAppService.stopK8sApp(id);
        return BaseResponse.ok(k8sApp);
    }

}
