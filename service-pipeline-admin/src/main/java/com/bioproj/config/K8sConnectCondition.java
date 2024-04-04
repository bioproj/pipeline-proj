package com.bioproj.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class K8sConnectCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String kubeConfigPath = System.getenv("HOME") + "/.kube/config";
        Path path = Paths.get(kubeConfigPath);
        if(!path.toFile().exists()){
            log.info("k8s配置文件{}不存在！",kubeConfigPath);
            return false;
        }
        return true;
    }


}
