package com.bioproj.pojo.task;

import com.bioproj.domain.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class SampleTask {
    @Id
    private String id;
    private String workflowId;
    private Integer taskId;
    private String dataId;
    private TaskStatus status;
    private String nativeId;
    private String tag;
    private String name;
    private String process;
    //    @ReadOnlyProperty
    private List<SampleTaskData> sampleTaskData;
    private String submit;
    private String start;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime=new Date();



//    public  boolean checkIsCached() {
//        return status == TaskStatus.CACHED;
//    }
}
