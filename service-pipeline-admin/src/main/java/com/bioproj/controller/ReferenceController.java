package com.bioproj.controller;

import com.bioproj.pojo.reference.Reference;
import com.bioproj.service.IReferenceService;
import com.bioproj.domain.BaseResponse;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reference")
public class ReferenceController {

    @Autowired
    IReferenceService referenceService;

    @GetMapping
    public R<PageModel<Reference>> page(Integer number, Integer size) {
        PageModel<Reference> pageModel = referenceService.page(number, size, null);
        return R.ok(pageModel);
    }

    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable("id")String id){
        Reference application = referenceService.del(id);
        return BaseResponse.ok("删除成功！");
    }

    @ApiOperation("修改")
    @PostMapping("/update/{id}")
    public BaseResponse update(@PathVariable("id") String id,@RequestBody Reference reference){
        referenceService.update(id,reference);
        return BaseResponse.ok("修改成功!");
    }

    @ApiOperation("新增")
    @PutMapping
    public BaseResponse add(@RequestBody Reference reference){
        reference.setId(null);

        referenceService.save(reference);
        return BaseResponse.ok("新增成功!");
    }
    @ApiOperation("查找")
    @GetMapping("/find/{id}")
    public R<Reference> find(@PathVariable("id") String  id){
        Reference reference = referenceService.findById(id);
        return R.ok(reference);
    }


}
