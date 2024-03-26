package com.bioproj.controller;

import com.bioproj.pojo.WorkDir;
import com.bioproj.repository.WorkDirRepository;
import com.bioproj.domain.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/workdir")
@Api(tags={"工作路径"})
@Slf4j
public class WorkDirController {

    @Autowired
    private WorkDirRepository workDirRepository;

    @ApiOperation("列表")
    @GetMapping("list")
    public BaseResponse list(){
        List<WorkDir> all = workDirRepository.findAll();
        return BaseResponse.ok(all);
    }
    @ApiOperation("分页")
    @GetMapping("page")
    public  BaseResponse page(@PageableDefault(sort = {"id"},direction = DESC) Pageable pageable){
        return BaseResponse.ok(workDirRepository.findAll(pageable));
    }


    @ApiOperation("增加")
    @PutMapping()
    public BaseResponse add(@RequestBody WorkDir e){
        e.setId(null);
        WorkDir all = workDirRepository.save(e);
        return BaseResponse.ok(all);
    }

    @ApiOperation("删除")
    @DeleteMapping()
    public BaseResponse delete(@RequestParam("idList") List<String> idList){
        for (String s : idList) {
            WorkDir all = new WorkDir();
            all.setId(s);
            workDirRepository.delete(all);
        }
        return BaseResponse.ok("删除成功！");
    }

    @ApiOperation("修改")
    @PostMapping()
    public BaseResponse update(@RequestBody WorkDir e){
        String id = e.getId();
        if (null == id) {
            return BaseResponse.error("id 不能为空");
        }
        if (workDirRepository.findById(id).orElse(null) == null) {
           return BaseResponse.error("数据不存在！");
        }
        WorkDir all = workDirRepository.save(e);
        return BaseResponse.ok(all);
    }

    @ApiOperation("单查")
    @GetMapping("/{id}")
    public BaseResponse id(@PathVariable("id")String id){
        WorkDir all = workDirRepository.findById(id).orElse(null);
        if (all == null) {
            return  BaseResponse.error("数据不存在！");
        }
        return BaseResponse.ok(all);
    }







}
