package com.bioproj.trace.model;

import com.bioproj.pojo.task.Task;
import com.bioproj.pojo.task.TaskData;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {
    private Task task;
    private TaskData taskData;
}
