package com.bioproj.kafka;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bioproj.pojo.Application;
import com.bioproj.pojo.Samples;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.service.IApplicationService;
import com.bioproj.service.IWorkflowService;
import com.google.gson.JsonObject;
import com.mbiolance.cloud.auth.common.SysUserInfoContext;
import com.mbiolance.cloud.auth.common.SystemRuntimeException;
import com.mbiolance.cloud.auth.domain.dto.SysUserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CreateWorkflow {


    @Autowired
    IWorkflowService workflowService;
    @Autowired
    private IApplicationService applicationService;

    @KafkaListener(topics = "analysis-ngs", containerFactory = "kafkaTraceListenerContainerFactory")
    public void tasksResult(ConsumerRecord<String, Object> record) {
        try {
            Object workflowJson = record.value();
            String key = record.key();
            JSONObject jsonObject = JSON.parseObject(workflowJson.toString());

            Workflow workflow = new Workflow();

//            SysUserDto user = SysUserInfoContext.getUser();


//            BeanUtils.copyProperties(workflowParams, workflow);
//            Application app = applicationService.findById(id);
//            if(workflow.getIsDebug()){
//                Workflow findTask = workflowService.findByUserIdAndIsDebug(user.getId(),app.getId(), true);
//                if(findTask!=null){
//                    throw new SystemRuntimeException("用户["+user.getLoginName()+"]已经创建了应用["+app.getPipeline()+"]的debug任务！");
//                }
//
//            }
//
//
//            BeanUtils.copyProperties(app,workflow,"id" ,"name");
//            workflow.setPipelineName(app.getName());
//            workflow.setApplicationId(app.getId());
//
//            if(workflow.getIsDebug()==null){
//                workflow.setIsDebug(false);
//            }
//
//
//            List<Samples> samplesList = workflowParams.getSamples().stream().map(item -> {
//                Samples samples = new Samples();
//                BeanUtils.copyProperties(item, samples);
//                return samples;
//            }).collect(Collectors.toList());
//
//            workflowService.submit(workflow,samplesList, user);




//            log.info(">>>>>>>>>>>>Received Message in group tasks-result: key: "+key+"  taskId:" + task.getId()+" userId:" +task.getUserId()+" status:"+task.getWorkflowStatus());
//            task = workflowService.save(task);
////            SseServerImpl.sendMessage(String.valueOf(task.getUserId()), JSON.toJSONString(task));
//            liveEventsService.publishTaskEvent(task,"tasksResult");
//            webSocketServer.sendMessageToUser(String.valueOf(task.getUserId()),JSON.toJSONString(task));

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }
}
