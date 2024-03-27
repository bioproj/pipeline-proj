package com.bioproj.repository;

import com.bioproj.pojo.Application;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface AppRepository extends MongoRepository<Application, String> {

}
