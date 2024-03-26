package com.bioproj.service;

import com.bioproj.pojo.task.TaskData;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.vo.QueryCriteriaVo;

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
