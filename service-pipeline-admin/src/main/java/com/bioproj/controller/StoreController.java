package com.bioproj.controller;

import com.bioproj.pojo.BaseResponse;
import com.bioproj.domain.PageModel;
import com.bioproj.domain.R;
import com.bioproj.pojo.Repos;
import com.bioproj.service.store.IStoreService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/store")
@Slf4j
@Api(tags={"商店"})
public class StoreController {
    @Resource
    private IStoreService wareHouseService;



    @GetMapping("page")
    @ApiOperation("分页")
    public R<PageModel<Repos>> page(@RequestParam(defaultValue = "1", required = false) Integer number
            , @RequestParam(defaultValue = "20", required = false) Integer size){
//    public BaseResponse page(@PageableDefault(sort = {"id"},direction = DESC) Pageable pageable){
        number -= 1;
        number = number <= 0 ? 0 : number;
        size = (size <= 0) ? 10 : size;
        PageRequest pageRequest = PageRequest.of(number, size, Sort.by(Sort.Order.desc("createDate")));
        Page<Repos> page = wareHouseService.page(pageRequest);
        List<Repos> content = page.getContent();

        PageModel<Repos> pageModel = PageModel.<Repos>builder()
                .count((int) page.getTotalElements())
                .content(content)
                .number(number+1)
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .build();

        return R.ok(pageModel);
//        return BaseResponse.ok(wareHouseService.page_db(pageable));
    }

    @GetMapping("refresh")
    @ApiOperation("刷新")
    public BaseResponse refresh(){
        List<Repos> wareHouses = wareHouseService.list_new();
        wareHouseService.delALl();
        wareHouseService.saveAll(wareHouses);
        return BaseResponse.ok("刷新成功！");
    }



    @ApiOperation("单查")
    @GetMapping("/{id}")
    public BaseResponse id(@PathVariable("id")String id){
        Repos all = wareHouseService.findById(id);
        if (all == null) {
            return  BaseResponse.error("数据不存在！");
        }
        return BaseResponse.ok(all);
    }

    @ApiOperation("获取分支列表")
    @GetMapping("/branches/{id}")
    public BaseResponse branches(@PathVariable("id")String id){
        List<String> all = wareHouseService.branches(id);
        if (all == null) {
            return  BaseResponse.error("数据不存在！");
        }
        return BaseResponse.ok(all);
    }

    @ApiOperation("获取readme")
    @GetMapping("/readme/{id}")
    public void readme(@PathVariable("id")String id, HttpServletResponse response){
        wareHouseService.readme(id, response);
    }

    @PutMapping
    @ApiOperation("新增")
    public BaseResponse add(@RequestBody Repos wareHouse){
//        SysUserDto user = SysUserInfoContext.getUser();
//        wareHouse.setUserId(user.getId());
        wareHouse.setId(null);
        Repos save = wareHouseService.save(wareHouse);
        return BaseResponse.ok(save);
    }


    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable("id")String id){
        Repos application = wareHouseService.del(id);
        return BaseResponse.ok("删除成功！");
    }

    @ApiOperation("修改")
    @PostMapping("/update/{id}")
    public BaseResponse update(@PathVariable("id") String id,@RequestBody Repos repos){
        wareHouseService.update(id,repos);
        return BaseResponse.ok("修改成功!");
    }

    @ApiOperation("查找")
    @GetMapping("/find/{id}")
    public R<Repos> find(@PathVariable("id") String  id){
        Repos reference = wareHouseService.findById(id);
        return R.ok(reference);
    }

    @ApiOperation("列表")
    @GetMapping("/listAll")
    public R<List<Repos>> list(){
        List<Repos> repos = wareHouseService.listAll();
        return R.ok(repos);
    }
}
