package com.bioproj.pojo.task;

import com.bioproj.domain.enums.TaskStatus;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class Task {
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
    private List<TaskData> taskData;
    private String submit;
    private String start;



    public  boolean checkIsCached() {
        return status == TaskStatus.CACHED;
    }
}
