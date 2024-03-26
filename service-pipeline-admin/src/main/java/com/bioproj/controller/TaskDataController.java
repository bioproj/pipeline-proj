package com.bioproj.controller;

import com.bioproj.pojo.task.TaskData;
import com.bioproj.service.ITaskDataService;
import com.bioproj.domain.BaseResponse;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/taskData")
public class TaskDataController {

    @Autowired
    ITaskDataService taskDataService;

    @GetMapping
    public R<PageModel<TaskData>> page(Integer number, Integer size) {
        PageModel<TaskData> pageModel = taskDataService.page(number, size, null);
        return R.ok(pageModel);
    }

    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable("id")String id){
        TaskData taskData = taskDataService.del(id);
        return BaseResponse.ok("删除成功！");
    }

    @ApiOperation("修改")
    @PostMapping("/update/{id}")
    public BaseResponse update(@PathVariable("id") String id,@RequestBody TaskData taskData){
        taskDataService.update(id,taskData);
        return BaseResponse.ok("修改成功!");
    }

    @ApiOperation("新增")
    @PutMapping
    public BaseResponse add(@RequestBody TaskData taskData){
        taskData.setId(null);

        taskDataService.save(taskData);
        return BaseResponse.ok("新增成功!");
    }
    @ApiOperation("查找")
    @GetMapping("/find/{id}")
    public R<TaskData> find(@PathVariable("id") String  id){
        TaskData taskData = taskDataService.findById(id);
        return R.ok(taskData);
    }

}
