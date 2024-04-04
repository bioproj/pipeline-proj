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


@Slf4j
public class KafkaConnectCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        String kafkaServers = environment.getProperty("spring.kafka.bootstrap-servers");
        log.info("获取到的kafkaServers：{}", kafkaServers);
        if (StringUtils.isBlank(kafkaServers)){
            return false;
        }

        String serverPort = kafkaServers.split(",")[0];
        URI uri = URI.create("http://" + serverPort);

        return this.isConnectable(uri.getHost(), uri.getPort());
    }

    /**
     * 判断kafka服务能否正常连接
     * @param host
     * @param port
     * @return
     */
    private boolean isConnectable(String host, int port) {
//        boolean result = true;
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port),3000);
        } catch (IOException e) {
            log.error("========注意!!!!!未能连接上kafka服务,意味着kafka监听将不开启，{}:{},{}", host, port, e.getMessage());
//            result = false;
            return  false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log.error("关闭kafka服务socket出错，{}:{},{}", host, port, e.getMessage());
//                result = false;
                return false;
            }
        }
        log.info("========kafka服务能正常连接========");
        return true;
    }
}