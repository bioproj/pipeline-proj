package com.bioproj.controller;

import com.bioproj.domain.enums.ImageType;
import com.bioproj.domain.vo.ImagesVo;
import com.bioproj.pojo.Images;
import com.bioproj.service.images.IImagesService;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.bioproj.domain.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/images")
@Slf4j
@Api(tags={"DOCKER仓库"})
public class ImageController {

    @Autowired
    private IImagesService imagesService;

    @ApiOperation("查询tag列表")
    @GetMapping
    public R<PageModel<Images>> list(@RequestParam(required = false) String tagName, @RequestParam(defaultValue = "1", required = false) Integer number
            , @RequestParam(defaultValue = "20", required = false) Integer size){
        PageModel<Images> page = imagesService.page(number, size, tagName);
        return R.ok(page);
    }
    @ApiOperation("查询tag列表")
    @PostMapping("/page")
    public R<PageModel<Images>> page(@RequestParam(defaultValue = "1", required = false) Integer number
            , @RequestParam(defaultValue = "20", required = false) Integer size,@RequestBody ImagesVo imagesVo){
        PageModel<Images> page = imagesService.page(number, size, imagesVo);
        return R.ok(page);
    }
    @ApiOperation("查询版本列表")
    @GetMapping("versionList")
    public R<List<String>> versionList(@RequestParam(required = true) String tagName){
        Images dockerRegistry = imagesService.byName(tagName);
        dockerRegistry.getTags();
        return R.ok(dockerRegistry.getTags());
    }

    @ApiOperation("刷新")
    @GetMapping("refresh")
    public BaseResponse taglist(){
        imagesService.refresh();
        return BaseResponse.ok("刷新成功！");
    }



    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable("id")String id){
        Images images = imagesService.del(id);
        return BaseResponse.ok("删除成功！");
    }

    @ApiOperation("修改")
    @PostMapping("/update/{id}")
    public BaseResponse update(@PathVariable("id") String id,@RequestBody Images images){
        imagesService.update(id,images);
        return BaseResponse.ok("修改成功!");
    }

    @ApiOperation("新增")
    @PutMapping
    public BaseResponse add(@RequestBody Images images){
        images.setId(null);
        images.setImageType(ImageType.APPLICATION);
        imagesService.save(images);
        return BaseResponse.ok("新增成功!");
    }
    @ApiOperation("查找")
    @GetMapping("/find/{id}")
    public R<Images> find(@PathVariable("id") String  id){
        Images images = imagesService.findById(id);
        return R.ok(images);
    }
}
