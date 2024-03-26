package com.bioproj.pojo;

import com.bioproj.domain.enums.WorkflowType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class Application {
    @Id
    private String id;
    private String name;
    private String repoId;
    //创建时间
    @Field(name = "date_created")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date dateCreated;
    //提交时间
    @Field(name = "date_submitted")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date dateSubmitted;
    //输出文件
    @Field(name = "output_files")
    private List<String> outputFiles;
    //管道
    private String pipeline;
    //状态   //nascent 新生的  //running 运行的  //completed
    private String status;
    //配置文件
    private String profiles;

    private String revision;
//    @Field(name = "params_format")
//    private String paramsFormat;
//    @Field(name = "params_data")
//    private String paramsData;

    //输入路径
    @Field(name = "input_dir")
    private String inputDir;
    //输出路径
    @Field(name = "output_dir")
    private String outputDir;

    @Field(name = "input_files")
    private List<String> inputFiles;

    @Field(name = "output_data")
    private Boolean outputData;

    private long pid;

    private String log;
    //启动次数
    private Integer attempts;

    //工作路径
    private String workDir;

    private Integer userId;
    private WorkflowType workflowType;



}
