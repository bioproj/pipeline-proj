package com.bioproj.k8s;

import io.kubernetes.client.extended.controller.reconciler.Reconciler;
import io.kubernetes.client.extended.controller.reconciler.Request;
import io.kubernetes.client.extended.controller.reconciler.Result;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformer;
import io.kubernetes.client.informer.cache.Lister;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1Pod;
import org.springframework.stereotype.Component;

//@Component
public class NodePrintingReconciler implements Reconciler {

    //    @Value("${namespace}")
    private String namespace = "default";

    private SharedInformer<V1Node> nodeInformer;

    private SharedInformer<V1Pod> podInformer;

    private Lister<V1Node> nodeLister;

    private Lister<V1Pod> podLister;

    public NodePrintingReconciler(
            SharedIndexInformer<V1Node> nodeInformer, SharedIndexInformer<V1Pod> podInformer) {
        this.nodeInformer = nodeInformer;
        this.podInformer = podInformer;
        this.nodeLister = new Lister<>(nodeInformer.getIndexer(), namespace);
        this.podLister = new Lister<>(podInformer.getIndexer(), namespace);
    }

    // *OPTIONAL*
    // If you want to hold the controller from running util some condition..
    public boolean informerReady() {
        return podInformer.hasSynced() && nodeInformer.hasSynced();
    }

    @Override
    public Result reconcile(Request request) {
        V1Node node = nodeLister.get(request.getName());

        System.out.println("get all pods in namespace " + namespace);
        podLister.namespace(namespace).list().stream()
                .map(pod -> pod.getMetadata().getName())
                .forEach(System.out::println);

//        System.out.println("triggered reconciling " + node.getMetadata().getName());
        return new Result(false);
    }

}
