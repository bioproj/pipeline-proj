package com.bioproj.service;

import com.bioproj.pojo.task.Workflow;
import com.bioproj.pojo.Workflows;
import org.springframework.scheduling.annotation.Async;

public interface IProcessService {
    /**
     *
     * @param workflows
     * @param resume 1开始 2 恢复 3 停止
     */
    void launchAndResume(Workflows workflows, Integer resume);

//    void launch(TaskApp app);

//    void resume(TaskApp app);

    @Async("taskExecutor")
    void locaLaunch(Workflow app, String[] args);

    @Async("taskExecutor")
    void fluxLaunch(Workflow app);



    void cancel(Workflow app);


}
