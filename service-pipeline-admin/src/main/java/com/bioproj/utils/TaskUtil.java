package com.bioproj.utils;

import com.bioproj.pojo.task.Workflow;

public class TaskUtil {
    public static String getRunName(Workflow app){
        return "TaskApp-"+app.getPipelineName()+"-"+app.getAttempts();
    }

}
