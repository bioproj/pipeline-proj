package com.bioproj.controller;

import com.bioproj.pojo.task.Task;
import com.bioproj.pojo.task.TaskData;
import com.bioproj.service.ITaskDataService;
import com.bioproj.service.ITaskService;
import com.bioproj.domain.BaseResponse;
import com.bioproj.utils.FileUtils;
import com.mbiolance.cloud.auth.common.SystemRuntimeException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/taskProcess")
@Slf4j
@Api(tags={"TaskProcessController"})
public class TaskProcessController {

    @Autowired
    ITaskService taskProcessService;

    @Autowired
    ITaskDataService taskDataService;
    @ApiOperation("查找TaskProcess")
    @GetMapping("/find/{id}")
    public BaseResponse subTakes( @PathVariable("id")String id){
        Task task = taskProcessService.findById(id);
        if(task==null){
            throw new SystemRuntimeException("task["+task.getId()+"]不存在!");
        }
        TaskData taskData = taskDataService.findById(task.getDataId());
        if(taskData==null){
            throw new SystemRuntimeException("taskData["+taskData.getId()+"]不存在!");
        }
        String workDir = taskData.getWorkdir(); //taskProcess.getWorkdir();

        List<Path> fileList = Arrays.asList(Paths.get(workDir, ".command.sh"),
                Paths.get(workDir, ".command.log"),
                Paths.get(workDir, ".command.err"),
                Paths.get(workDir, ".command.run"),
                Paths.get(workDir, ".command.begin"),
                Paths.get(workDir, ".command.trace"),
                Paths.get(workDir, ".command.yaml"),
                Paths.get(workDir, ".command.out"));

        Map<String,String> map = new HashMap<>();
        for (Path file : fileList){
            if(file.toFile().exists()){
                String content = FileUtils.openFile(file.toFile());
                String key = file.getFileName().toString().replace(".command.", "");
                map.put(key,content);
            }
        }
        return BaseResponse.ok(map);
    }
}
