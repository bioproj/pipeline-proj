package com.bioproj.repository;

import com.bioproj.pojo.Images;
import com.bioproj.pojo.task.SampleTask;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SampleTaskRepository  extends MongoRepository<SampleTask, String> {

}
