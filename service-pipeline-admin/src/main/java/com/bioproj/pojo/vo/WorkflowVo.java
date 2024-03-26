package com.bioproj.pojo.vo;

import com.bioproj.domain.ProcessesProgress;
import com.bioproj.domain.WorkflowLoad;
import com.bioproj.domain.WorkflowProgress;
import com.bioproj.domain.enums.WorkflowType;
import com.bioproj.domain.vo.K8sAppVo;
import com.bioproj.domain.vo.TaskVo;
import com.bioproj.pojo.*;
import com.bioproj.domain.enums.ParamsFormat;
import com.bioproj.domain.enums.WorkflowStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Data
public class WorkflowVo {
    @Id
    private String id;
    private String appId;
    private String name;
    private String applicationId;
    //创建时间
    @Field(name = "create_data")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createDate;
    @Field(name = "finish_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date finishDate;
    //提交时间
    @Field(name = "date_submitted")
    private Date dateSubmitted;
    //输出文件
//    @Field(name = "output_files")
//    private List<String> outputFiles;
    //管道
    private String pipeline;
    private String pipelineName;
    //状态   //nascent 新生的  //running 运行的  //completed  成功  //failed 失败
//    private String status;
    //配置文件
    private String profiles;

    private String revision;
    @Field(name = "params_format")
    private ParamsFormat paramsFormat=ParamsFormat.JSON;
    @Field(name = "params_data")
    private String paramsData;
    @Field(name = "exec_dir")
    private String execDir;
    //输入路径
    @Field(name = "input_dir")
    private String inputDir;
    //输出路径
    @Field(name = "output_dir")
    private String outputDir;
    private String resultDir;
//    @Field(name = "input_files")
//    private List<String> inputFiles;

    @Field(name = "output_data")
    private Boolean outputData;

    private Long pid;

    private String log;
    //启动次数
    private Integer attempts;

    //工作路径
    private String workDir;
    private String logPath;
    private String paramPath;
    private String cmdLog;

    private WorkflowStatus workflowStatus;
    private String runName;
    @Column(columnDefinition = "longtext")
    private String commandLine;
    private String sessionId;
    private String configuration;

    private String type; //1为本地 2为远程


    private String submitId;

    private List<Samples> samples;

    private String script;
    private Boolean isDebug;
    private Integer userId;

    private String tracePath;
    private String timelinePath;
    private String reportPath;
    private Progress progress;
    private Page<Reports> reports;
    private Page<TaskVo> tasks;
    private List<ProcessesProgress> processesProgresses;
    private WorkflowProgress workflowProgress;
    private WorkflowLoad workflowLoad;
    private WorkflowType workflowType;
    private List<K8sAppVo> k8sAppVo;

    private String topic;
}
