package com.bioproj.controller;

import com.bioproj.domain.BaseResponse;
import com.bioproj.pojo.Modules;
import com.bioproj.pojo.reference.Reference;
import com.bioproj.service.IModulesService;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/modules")
@Slf4j
@Api(tags={"软件模块"})
public class ModulesController {

    @Autowired
    IModulesService modulesService;


    @GetMapping
    public R<PageModel<Modules>> page(Integer number, Integer size) {
        PageModel<Modules> pageModel = modulesService.page(number, size, null);
        return R.ok(pageModel);
    }

    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable("id")String id){
        Modules modules = modulesService.del(id);
        return BaseResponse.ok("删除成功！");
    }

    @ApiOperation("修改")
    @PostMapping("/update/{id}")
    public BaseResponse update(@PathVariable("id") String id,@RequestBody Modules modules){
        modulesService.update(id,modules);
        return BaseResponse.ok("修改成功!");
    }

    @ApiOperation("新增")
    @PutMapping
    public BaseResponse add(@RequestBody Modules modules){
        modules.setId(null);

        modulesService.save(modules);
        return BaseResponse.ok("新增成功!");
    }
    @ApiOperation("查找")
    @GetMapping("/find/{id}")
    public R<Modules> find(@PathVariable("id") String  id){
        Modules modules = modulesService.findById(id);
        return R.ok(modules);
    }
}
