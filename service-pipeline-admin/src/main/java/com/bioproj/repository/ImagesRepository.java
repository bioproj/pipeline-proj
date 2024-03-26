package com.bioproj.repository;

import com.bioproj.pojo.Images;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImagesRepository extends MongoRepository<Images, String> {
}
