package com.bioproj.service.impl;

import com.bioproj.pojo.task.TaskData;
import com.bioproj.repository.TaskDataRepository;
import com.bioproj.service.ITaskDataService;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.vo.QueryCriteriaVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TaskDataServiceImpl implements ITaskDataService {

    @Autowired
    TaskDataRepository taskDataRepository;


    @Override
    public TaskData getTaskDataBySessionIdAndHash(String sessionId, String hash) {
       return taskDataRepository.findOne(Example.of(TaskData.builder()
                .sessionId(sessionId)
                .hash(hash)
                .build())).orElse(null);
    }

    @Override
    public TaskData save(TaskData taskData) {
        return taskDataRepository.save(taskData);
    }

    @Override
    public List<TaskData> listByIds(Set<String> ids){
        return taskDataRepository.findByIdsIn(ids);
    }
    @Override
    public TaskData findById(String id) {
        return taskDataRepository.findById(id).orElse(null);
    }


    @Override
    public PageModel<TaskData> page(Integer number, Integer size, List<QueryCriteriaVo> criteriaVos) {
        number -= 1;
        number = number <= 0 ? 0 : number;
        size = size <= 0 ? 10 : size;
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(number, size, Sort.by(order));
        Page<TaskData> page = taskDataRepository.findAll(pageRequest);

        return PageModel.<TaskData>builder()
                .count((int) page.getTotalElements())
                .content(page.getContent())
                .number(number + 1)
                .size(size)
                .build();
    }
    @Override
    public TaskData del(String s) {
        TaskData taskData = findById(s);
        taskDataRepository.delete(taskData);
        return taskData;
    }

    @Override
    public List<TaskData> del(Set<String> ids) {
        List<TaskData> taskDataList = taskDataRepository.findByIdsIn(ids);
        taskDataRepository.deleteAll(taskDataList);
        return taskDataList;
    }
    @Override
    public TaskData update(String id, TaskData appParam) {
        TaskData taskData = findById(id);
        BeanUtils.copyProperties(appParam, taskData, "id");
        return taskDataRepository.save(taskData);
    }

}
