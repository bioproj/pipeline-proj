package com.bioproj.controller;

import com.bioproj.kafka.KafkaMessageViewer;
import com.bioproj.live.LiveEventsService;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.domain.enums.WorkflowStatus;
import com.bioproj.service.IWorkflowService;
import com.bioproj.domain.BaseResponse;
import com.bioproj.websocket.WebSocketServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
@Slf4j
@Api(tags={"应用任务"})
public class TaskListener {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private IWorkflowService taskAppService;

    @Autowired
    private WebSocketServer webSocketServer;
    @Autowired
    LiveEventsService liveEventsService;


    @Autowired
    KafkaMessageViewer kafkaMessageViewer;

//    public void  viewTopicMessages(@PathVariable("topic") String  topic){
    @GetMapping("/viewTopicMessages")
    public void  viewTopicMessages(){
        kafkaMessageViewer.viewTopicMessages("59c4541e-a585-477a-be6f-d753106f52f8");
    }

//    @KafkaListener(topics = "nextflow-result", containerFactory = "kafkaNFListenerContainerFactory")
//    public void nextflowResult(ConsumerRecord<String, String> record) {
////        log.info(">>>>>>>>>>>>Received Message in group tasks-result: " + task.getId());
////        taskAppService.save(task);
//        String key = record.key();
//        String value = record.value();
//        Task task = taskAppService.findById(key);
////        SseEmitter sseEmitter = SseServerImpl.getSse(task.getUserId()+"");
////        sseEmitter.complete();
//
//        if(task!=null){
//            task.setSubmitId(null);
//            if(value.equals("success")){
//                task.setTaskStatus(TaskStatus.SUCCESS);
//            }else {
//                task.setTaskStatus(TaskStatus.FAILED);
//            }
////            SseServerImpl.sendMessage(String.valueOf(task.getUserId()), JSON.toJSONString(task));
////            taskAppService.save(task);
//            kafkaTemplate.send("tasks-queue","stop",task);
//        }
//
//    }

    @ApiOperation("stop")
    @GetMapping("/signal/{taskId}/error")
    public BaseResponse sendKafka(@PathVariable String taskId){
        Workflow task = taskAppService.findById(taskId);
        task.setWorkflowStatus(WorkflowStatus.FAILED);
        taskAppService.save(task);
        liveEventsService.publishTaskEvent(task);
        return  BaseResponse.ok("success");
    }


    @ApiOperation("发送样本状态")
    @GetMapping("/sendSampleStatus/{sampleId}")
    public BaseResponse sendSampleStatus(@PathVariable String taskId){
//        Workflow task = taskAppService.findById(taskId);
//        task.setWorkflowStatus(WorkflowStatus.FAILED);
//        taskAppService.save(task);
//        liveEventsService.publishTaskEvent(task);
        kafkaTemplate.send("sampleId","");
        return  BaseResponse.ok("success");
    }
}
