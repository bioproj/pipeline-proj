package com.bioproj.domain.params;

import com.bioproj.domain.ProcessesProgress;
import com.bioproj.domain.WorkflowLoad;
import com.bioproj.domain.WorkflowProgress;
import com.bioproj.domain.enums.NFTaskStatus;
import com.bioproj.domain.enums.ParamsFormat;
import com.bioproj.domain.enums.WorkflowStatus;

import com.bioproj.domain.enums.WorkflowType;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class WorkflowParams {
    private String appId;
    private String name;
    private String applicationId;
    //创建时间



    private Date finishDate;
    //提交时间

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

    private ParamsFormat paramsFormat=ParamsFormat.JSON;
    private String paramsData;
    private String execDir;
    //输入路径
    private String inputDir;
    //输出路径
    private String outputDir;
    private String resultDir;
//    @Field(name = "input_files")
//    private List<String> inputFiles;

    private Boolean outputData;

    private Long pid;
    private String submitId;

    private String log;
    //启动次数
    private Integer attempts;

    //工作路径
    private String workDir;
    private String logPath;
    private String paramPath;
    private String configPath;
    // for k8s storageMountPath
    private String storagePath;
    private String runPath;
    private String envPath;
    private String cmdLog;

    private WorkflowStatus workflowStatus;
    private NFTaskStatus nfTaskStatus;
    private String runName;
    private String commandLine;
    private String env;
    private String sessionId;
    private String configuration;

    private String type; //1为本地 2为远程


    private Boolean isDebug=false;

    //    private List<Sample> samples;
    private Integer userId;

    private String tracePath;
    private String timelinePath;
    private String reportPath;
    private List<ProcessesProgress> processesProgresses;
    private WorkflowProgress workflowProgress;
    private WorkflowLoad workflowLoad;
    private List<SampleParams> samples;
    private String templateId;
    private WorkflowType workflowType;
    private String topic;
}
