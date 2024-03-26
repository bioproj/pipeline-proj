package com.bioproj.repository;

import com.bioproj.pojo.Application;
import com.bioproj.pojo.K8sApp;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface K8sAppRepository extends MongoRepository<K8sApp, String> {
}
