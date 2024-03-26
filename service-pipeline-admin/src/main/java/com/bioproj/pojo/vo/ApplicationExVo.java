package com.bioproj.pojo.vo;

import com.bioproj.pojo.Application;
import lombok.Data;

@Data
public class ApplicationExVo extends Application {

    private String configuration;
    private String paramsData;
}
