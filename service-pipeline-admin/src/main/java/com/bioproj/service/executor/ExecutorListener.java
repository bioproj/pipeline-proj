package com.bioproj.service.executor;

import com.bioproj.pojo.task.Workflow;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ExecutorListener {

    @Autowired
    @Qualifier("dockerExecutorImpl")
    IExecutorsService executorsService;


//    @KafkaListener(topics = "tasks-queue", containerFactory = "kafkaListenerContainerFactory")
    public void listenTaskQueue(ConsumerRecord<String, Workflow> record) {
        Workflow task = record.value();
        String key = record.key();
        if(key.equals("submit")){
            executorsService.submit(task);
        } else if (key.equals("stop")) {
            executorsService.stop(task);
        } else if (key.equals("write")) {
            executorsService.writeFile(task);
        }
//        System.out.println("docker: <<<<<<<<<<<< Send Message in group tasks-queue: " + task.getId());

    }
}
