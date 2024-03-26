package com.bioproj.trace.cost;

import com.bioproj.pojo.task.Task;
import com.bioproj.pojo.task.TaskData;

import java.math.BigDecimal;

public interface CostService {
    BigDecimal computeCost(TaskData task);
}
