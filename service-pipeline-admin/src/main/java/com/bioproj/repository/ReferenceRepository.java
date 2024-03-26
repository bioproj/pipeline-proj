package com.bioproj.repository;

import com.bioproj.pojo.Application;
import com.bioproj.pojo.reference.Reference;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReferenceRepository extends MongoRepository<Reference, String> {
}
