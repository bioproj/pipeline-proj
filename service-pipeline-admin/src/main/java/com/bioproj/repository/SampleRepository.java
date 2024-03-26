package com.bioproj.repository;

import com.bioproj.pojo.Samples;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SampleRepository extends MongoRepository<Samples, String> {
}
