package com.bioproj.k8s;

import io.kubernetes.client.extended.controller.Controller;
import io.kubernetes.client.extended.controller.builder.ControllerBuilder;
import io.kubernetes.client.extended.controller.builder.DefaultControllerBuilder;
import io.kubernetes.client.extended.controller.reconciler.Reconciler;
import io.kubernetes.client.extended.controller.reconciler.Request;
import io.kubernetes.client.extended.controller.reconciler.Result;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.informer.cache.Lister;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;

//@Configuration
public class K8sAppConfig {

//    @Bean
//    public ApiClient test() throws IOException {
//        String kubeConfigPath = System.getenv("HOME") + "/.kube/config";
//
//        // loading the out-of-cluster config, a kubeconfig from file-system
//        ApiClient client =
//                ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
//        // set the global default api-client to the in-cluster one from above
//        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client);
//        return client;
//    }

    @Bean
    public CommandLineRunner commandLineRunner(
            SharedInformerFactory sharedInformerFactory, Controller nodePrintingController) {
        return args -> {
            System.out.println("starting informers..");
            sharedInformerFactory.startAllRegisteredInformers();

            System.out.println("running controller..");
            nodePrintingController.run();
        };
    }

    @Bean
    public Controller nodePrintingController(
            SharedInformerFactory sharedInformerFactory, NodePrintingReconciler reconciler) {
        DefaultControllerBuilder builder = ControllerBuilder.defaultBuilder(sharedInformerFactory);
        builder =
                builder.watch(
                        (q) -> {
                            return ControllerBuilder.controllerWatchBuilder(V1Node.class, q)
                                    .withResyncPeriod(Duration.ofMinutes(1))
                                    .build();
                        });
        builder.withWorkerCount(2);
        builder.withReadyFunc(reconciler::informerReady);
        return builder.withReconciler(reconciler).withName("nodePrintingController").build();
    }

    @Bean
    public SharedIndexInformer<V1Endpoints> endpointsInformer(
            ApiClient apiClient, SharedInformerFactory sharedInformerFactory) {
        GenericKubernetesApi<V1Endpoints, V1EndpointsList> genericApi =
                new GenericKubernetesApi<>(
                        V1Endpoints.class, V1EndpointsList.class, "", "v1", "endpoints", apiClient);
        return sharedInformerFactory.sharedIndexInformerFor(genericApi, V1Endpoints.class, 0);
    }

    @Bean
    public SharedIndexInformer<V1Node> nodeInformer(
            ApiClient apiClient, SharedInformerFactory sharedInformerFactory) {
        GenericKubernetesApi<V1Node, V1NodeList> genericApi =
                new GenericKubernetesApi<>(V1Node.class, V1NodeList.class, "", "v1", "nodes", apiClient);
        return sharedInformerFactory.sharedIndexInformerFor(genericApi, V1Node.class, 60 * 1000L);
    }

    @Bean
    public SharedIndexInformer<V1Pod> podInformer(
            ApiClient apiClient, SharedInformerFactory sharedInformerFactory) {
        GenericKubernetesApi<V1Pod, V1PodList> genericApi =
                new GenericKubernetesApi<>(V1Pod.class, V1PodList.class, "", "v1", "pods", apiClient);
        return sharedInformerFactory.sharedIndexInformerFor(genericApi, V1Pod.class, 0);
    }
}

