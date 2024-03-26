package com.bioproj.repository;

import com.bioproj.pojo.task.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, String> {
}
