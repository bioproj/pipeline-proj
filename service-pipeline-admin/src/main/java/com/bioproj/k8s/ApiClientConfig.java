package com.bioproj.k8s;

import com.bioproj.config.K8sConnectCondition;
import com.bioproj.config.KafkaConnectCondition;
import com.google.gson.reflect.TypeToken;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import io.kubernetes.client.util.Watch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.FileReader;
import java.io.IOException;

@Configuration
@Slf4j
@Conditional(K8sConnectCondition.class)
public class ApiClientConfig {
    private Thread spooler;
    private Watch<V1Namespace> watch;

    @PostConstruct
    @Bean
    public ApiClient initClient() throws IOException {
        // file path to your KubeConfig
        String kubeConfigPath = System.getenv("HOME") + "/.kube/config";

        // loading the out-of-cluster config, a kubeconfig from file-system
        ApiClient client =
                ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
        // set the global default api-client to the in-cluster one from above
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client);
        spooler = new Thread(() -> watch(client));
        spooler.setDaemon(true);
        spooler.setName("k8s-watch");
        spooler.start();
        return client;

//        WebSockets.stream(
//                args[0],
//                "GET",
//                client,
//                new WebSockets.SocketListener() {
//                    private volatile WebSocket socket;
//
//                    @Override
//                    public void open(String protocol, WebSocket socket) {
//                        this.socket = socket;
//                    }
//
//                    @Override
//                    public void close() {}
//
//                    @Override
//                    public void bytesMessage(InputStream is) {}
//
//                    @Override
//                    public void failure(Throwable t) {
//                        t.printStackTrace();
//                    }
//
//                    @Override
//                    public void textMessage(Reader in) {
//                        try {
//                            BufferedReader reader = new BufferedReader(in);
//                            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
//                                System.out.println(line);
//                            }
//                        } catch (IOException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                });

//        https://github.com/kubernetes-client/java/blob/master/examples/examples-release-18/src/main/java/io/kubernetes/client/examples/WatchExample.java

    }

//    @PostConstruct

    protected void watch(ApiClient client ) {
        try {
//            System.out.println("111111111111111111111111111111111111111111111");
            BatchV1Api api = new BatchV1Api();
            watch =      Watch.createWatch(
                            client,
                            api.listNamespacedJobCall(
                                    "nextflow", null, null, null, null, null, null, null, null,null,Boolean.TRUE, null),
                            new TypeToken<Watch.Response<V1Namespace>>() {}.getType());

            try {
                for (Watch.Response<V1Namespace> item : watch) {
                    System.out.printf("%s : %s%n", item.type, item.object.getMetadata().getName());
                }
            } finally {
                watch.close();
            }
        } catch (ApiException | IOException e) {
            log.info("k8s连接失败！");
            throw new RuntimeException(e);
        }
    }
    @PreDestroy
    protected void destroy() {
        try {
            watch.remove();
            watch.close();
            spooler.join(); // 将当前线程阻塞，直到被调用的线程执行完成。
        }  catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
