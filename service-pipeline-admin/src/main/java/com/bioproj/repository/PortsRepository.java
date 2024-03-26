package com.bioproj.repository;

import com.bioproj.pojo.Application;
import com.bioproj.pojo.Ports;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PortsRepository extends MongoRepository<Ports, String> {
}
