package com.bioproj.controller;

import com.bioproj.domain.enums.Partition;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enum")
@Slf4j
@Api(tags={"枚举"})
public class EnumController {


    @GetMapping
    public Object listName(@RequestParam String name){

        if(name.equals("Partition")){
            return Partition.values();
        }

        throw new RuntimeException("enum 不存在！");
    }
}
