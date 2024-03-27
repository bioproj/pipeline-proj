package com.bioproj.rpc;


import io.swagger.annotations.Api;


@Api(tags = "应用 - 工作流管理")
//@FeignClient(contextId = "application", value = "nextflow-java-api", path = "/application")
public interface ApplicationFeignService {


//    @ApiOperation("运行流程")
//    @PostMapping("/{id}/launch")
//    BaseResponse launch(@PathVariable("id") String id, @RequestBody WorkflowParams workflowParams);
}
