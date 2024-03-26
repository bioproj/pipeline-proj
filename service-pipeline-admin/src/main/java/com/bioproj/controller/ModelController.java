package com.bioproj.controller;

import com.bioproj.pojo.task.Workflow;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/model")
public class ModelController {

    @GetMapping("/config")
    public List<Workflow> config(){
        return null;
    }

    @PostMapping("/train")
    public Workflow train(){
        return null;
    }
    @PostMapping("/predict")
    public Workflow predict(){
        return null;
    }

}
