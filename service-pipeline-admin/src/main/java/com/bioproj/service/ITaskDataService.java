package com.bioproj.service;

import com.bioproj.domain.PageModel;
import com.bioproj.domain.QueryCriteriaVo;
import com.bioproj.pojo.task.TaskData;

import java.util.List;
import java.util.Set;

public interface ITaskDataService {
    TaskData getTaskDataBySessionIdAndHash(String sessionId, String hash);

    TaskData save(TaskData taskData);

    List<TaskData> listByIds(Set<String> ids);

    TaskData findById(String id);

    PageModel<TaskData> page(Integer number, Integer size, List<QueryCriteriaVo> criteriaVos);

    TaskData del(String s);

    List<TaskData> del(Set<String> ids);

    TaskData update(String id, TaskData appParam);
}
