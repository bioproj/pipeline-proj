package com.bioproj.repository;

import com.bioproj.pojo.task.TaskData;
import com.bioproj.pojo.task.Workflow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface WorkflowRepository extends MongoRepository<Workflow,String>{
    @Query("{'id': {$in: ?0}}")
    List<Workflow> findByIdsIn(Set<String> ids);
}
