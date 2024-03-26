package com.bioproj.pojo;

import com.bioproj.domain.enums.WorkflowType;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Repos {
    //ID
    private String id;
    //仓库名
    private String name;
    //描述
    private String description;
    // clone 地址
    private String cloneUrl;
    //仓库创建者
    private String projectCreator;
    //仓库创建者
    private String orgNo;
    private Integer userId;

    private WorkflowType workflowType;
}
