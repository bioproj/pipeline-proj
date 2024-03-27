package com.bioproj.controller;

import com.bioproj.domain.PageModel;
import com.bioproj.domain.R;
import com.bioproj.domain.SysUserDto;
import com.bioproj.pojo.Reports;
import com.bioproj.service.IReportsService;
import com.bioproj.pojo.BaseResponse;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {
    @Autowired
    IReportsService reportsService;

    @GetMapping
    public R<PageModel<Reports>> page(Integer number, Integer size) {
        SysUserDto user = new SysUserDto();// SysUserInfoContext.getUser();
        PageModel<Reports> pageModel = reportsService.page(user,number, size, null);
        return R.ok(pageModel);
    }

    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable("id")String id){
        Reports reports = reportsService.del(id);
        return BaseResponse.ok("删除成功！");
    }

    @ApiOperation("修改")
    @PostMapping("/update/{id}")
    public BaseResponse update(@PathVariable("id") String id,@RequestBody Reports reports){
        reportsService.update(id,reports);
        return BaseResponse.ok("修改成功!");
    }

    @ApiOperation("新增")
    @PutMapping
    public BaseResponse add(@RequestBody Reports reports){
        reports.setId(null);

        reportsService.save(reports);
        return BaseResponse.ok("新增成功!");
    }
    @ApiOperation("查找")
    @GetMapping("/find/{id}")
    public R<Reports> find(@PathVariable("id") String  id){
        Reports reports = reportsService.findById(id);
        return R.ok(reports);
    }
}
