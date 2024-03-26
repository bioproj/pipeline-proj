package com.bioproj.repository;

import com.bioproj.pojo.Repos;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends MongoRepository<Repos, String> {
}
