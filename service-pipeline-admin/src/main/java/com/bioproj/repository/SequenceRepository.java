package com.bioproj.repository;

import com.bioproj.pojo.Application;
import com.bioproj.pojo.Sequence;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SequenceRepository extends MongoRepository<Sequence, String> {
}
