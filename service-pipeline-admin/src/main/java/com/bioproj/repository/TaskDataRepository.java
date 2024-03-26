package com.bioproj.repository;

import com.bioproj.pojo.task.Task;
import com.bioproj.pojo.task.TaskData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Set;

public interface TaskDataRepository extends MongoRepository<TaskData, String> {

    @Query("{'id': {$in: ?0}}")
    List<TaskData> findByIdsIn(Set<String> ids);
}
