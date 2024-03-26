package com.bioproj.repository;

import com.bioproj.pojo.Application;
import com.bioproj.pojo.Modules;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModulesRepository extends MongoRepository<Modules, String> {
}
