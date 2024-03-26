package com.bioproj.repository;

import com.bioproj.pojo.Application;
import com.bioproj.pojo.Reports;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportsRepository extends MongoRepository<Reports, String> {
}
