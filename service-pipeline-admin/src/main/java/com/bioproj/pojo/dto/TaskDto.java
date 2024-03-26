package com.bioproj.pojo.dto;

import com.bioproj.pojo.task.Workflow;
import lombok.Data;

import java.util.Set;

@Data
public class TaskDto extends Workflow {
    private Set<Integer> seqIds;
}
