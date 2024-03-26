package com.bioproj.service;

import com.bioproj.pojo.Samples;

import java.util.List;

public interface ISampleService {
    List<Samples> saveAll(List<Samples> samples);

    List<Samples> findByWorkflowId(String workflowId);

    List<Samples> delByWorkflowId(String workflowId);
}
