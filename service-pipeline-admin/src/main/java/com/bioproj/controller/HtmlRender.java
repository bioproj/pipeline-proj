package com.bioproj.controller;

import com.bioproj.pojo.Reports;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.service.IReportsService;
import com.bioproj.service.IWorkflowService;
import com.bioproj.utils.FileUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/html")
@Slf4j
@Api(tags={"HtmlRender"})
public class HtmlRender {
    @Autowired
    private IWorkflowService taskAppService;

    @Autowired
    IReportsService reportsService;
    @GetMapping("/task/{id}/timeline")
    public void timeline(@PathVariable("id") String id, HttpServletResponse response){
        response.setContentType("text/html;charset=utf-8");
        Workflow taskApp = taskAppService.findById(id);
        Path file = Paths.get(taskApp.getTimelinePath());
        String content = "";
        if(file.toFile().exists()){
            content = FileUtils.openFile(file.toFile());
        }
        PrintWriter out= null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println(content);
    }
    @GetMapping("/task/{id}/report")
    public void report(@PathVariable("id") String id, HttpServletResponse response){
        response.setContentType("text/html;charset=utf-8");
        Workflow taskApp = taskAppService.findById(id);
        Path file = Paths.get(taskApp.getReportPath());
        String content = "";
        if(file.toFile().exists()){
            content = FileUtils.openFile(file.toFile());
        }
        PrintWriter out= null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println(content);
    }
    @GetMapping("/task/report/{id}.html")
    public void reportItem(@PathVariable("id") String id, HttpServletResponse response){

        response.setContentType("text/html;charset=utf-8");
        Reports reports = reportsService.findById(id);

        String content = "";
        if(reports==null){
            content="report"+id+"不存在！";

        }else {
            Path file = Paths.get(reports.getDestination());
            if(!file.toFile().exists()){
                content="文件"+reports.getDestination()+"不存在！";
            }else {
                if(file.toString().endsWith(".log") || file.toString().endsWith(".txt")) {
                    response.setContentType("text/plain;charset=utf-8");
                }
                content = FileUtils.openFile(file.toFile());
            }
        }

        try {
            try (PrintWriter out = response.getWriter()){
                out.println(content);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
