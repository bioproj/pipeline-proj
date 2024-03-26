package com.bioproj.rpc;


import com.bioproj.domain.BaseResponse;
import com.bioproj.domain.params.WorkflowParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Api(tags = "应用 - 工作流管理")
@FeignClient(contextId = "application", value = "nextflow-java-api", path = "/application")
public interface ApplicationFeignService {


    @ApiOperation("运行流程")
    @PostMapping("/{id}/launch")
    BaseResponse launch(@PathVariable("id") String id, @RequestBody WorkflowParams workflowParams);
}
