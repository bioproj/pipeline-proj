package com.bioproj.pojo.task;

import com.bioproj.domain.enums.TaskStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SampleTaskData{
    private String name;
    private String process;
    private String tag;
    private String workdir;
    private String submit;
    private String start;
    private TaskStatus status;

}