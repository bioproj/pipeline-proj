package com.bioproj.service.impl;

import com.bioproj.pojo.Samples;
import com.bioproj.repository.SampleRepository;
import com.bioproj.service.ISampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SampleServiceImpl implements ISampleService {

    @Autowired
    SampleRepository sampleRepository;



    @Override
    public List<Samples> saveAll(List<Samples> samples){
        return sampleRepository.saveAll(samples);
    }

    @Override
    public List<Samples> findByWorkflowId(String workflowId){
        List<Samples> samples = sampleRepository.findAll(Example.of(Samples.builder().workflowId(workflowId).build()));
        return samples;
    }

    @Override
    public List<Samples> delByWorkflowId(String workflowId){
        List<Samples> samples = findByWorkflowId(workflowId);
        sampleRepository.deleteAll(samples);
        return samples;
    }


}
