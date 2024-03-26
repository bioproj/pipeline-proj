package com.bioproj.service.images.model;

import lombok.Data;

import java.util.List;

@Data
public class DockerVersion {
    private String name;
    private List<String> tags;

}
