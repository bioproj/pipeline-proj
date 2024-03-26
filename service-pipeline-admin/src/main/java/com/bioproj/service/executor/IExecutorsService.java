package com.bioproj.service.executor;

import com.bioproj.pojo.task.Workflow;

import java.util.List;

public interface IExecutorsService {
    boolean ping();

    Boolean runScript(Workflow task, String file);

    Boolean delete(Workflow task);


    void writeFile(Workflow task);

    void submit(Workflow task);
    Boolean cancel(String id);
    Boolean status(String id);
    List<Workflow> jobs();
    Boolean stop(Workflow task);



}
