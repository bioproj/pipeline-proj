package com.bioproj.trace.cost;

import com.bioproj.pojo.task.Task;

import com.bioproj.pojo.task.TaskData;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CostServiceImpl  implements CostService {
//    @Value('${tower.costs.local.costPerCpuHour:0.1}')
    BigDecimal costCpuHour= BigDecimal.valueOf(0.1);
    @Override
    public BigDecimal computeCost(TaskData task) {
        return computeCost(task.getRealtime() != null ? task.getRealtime() : 0, task.getCpus() != null ? task.getCpus() : 1);
    }

    private BigDecimal computeCost(long millis, int cpus) {
        return compute(costCpuHour, millis, cpus);
    }

    private static BigDecimal compute(BigDecimal unitPerCpuHour, long millis, int cpus) {
        return unitPerCpuHour.multiply(BigDecimal.valueOf(millis)).multiply(BigDecimal.valueOf(cpus)).divide(BigDecimal.valueOf(3_600_000),10, RoundingMode.HALF_UP);
    }
}
