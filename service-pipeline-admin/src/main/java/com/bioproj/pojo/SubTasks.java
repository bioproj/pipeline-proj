package com.bioproj.pojo;

import lombok.Data;

@Data
public class SubTasks {
    private Integer taskId;
    private String process;
    private String tag;
    private String name;
    private String workdir;
    private String status;
    private Integer exit;
    private String executor;


}
