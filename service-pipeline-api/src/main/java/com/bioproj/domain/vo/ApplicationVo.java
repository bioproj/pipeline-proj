package com.bioproj.domain.vo;

import com.bioproj.domain.enums.WorkflowType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class ApplicationVo {

    private String id;
    private String name;
    private String repoId;
    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date dateCreated;
    //提交时间
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date dateSubmitted;
    //输出文件

    private List<String> outputFiles;
    //管道
    private String pipeline;
    //状态   //nascent 新生的  //running 运行的  //completed
    private String status;
    //配置文件
    private String profiles;

    private String revision;

    private String inputDir;
    //输出路径

    private String outputDir;
    private List<String> inputFiles;
    private Boolean outputData;
    private long pid;

    private String log;
    //启动次数
    private Integer attempts;
    //工作路径
    private String workDir;

    private Integer userId;
    private WorkflowType workflowType;
    private String nickname;

}
