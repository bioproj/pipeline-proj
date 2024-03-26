package com.bioproj.k8s;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "k8s")
@Data
public class MapK8sProperties {
    private Map<String,String> vscode;
    private Map<String,String> rstudio;
    private List<String> pvc;

}
