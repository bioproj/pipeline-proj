package com.bioproj.trace.kafka;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bioproj.domain.enums.WorkflowType;
import com.bioproj.live.LiveEventsService;
import com.bioproj.pojo.*;
import com.bioproj.domain.enums.NFTaskStatus;
import com.bioproj.domain.enums.WorkflowStatus;
import com.bioproj.domain.ProcessesProgress;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.service.IReportsService;
import com.bioproj.service.ITaskService;
import com.bioproj.service.IWorkflowService;
import com.bioproj.trace.TraceService;
import com.bioproj.trace.model.TraceTaskData;
import com.bioproj.trace.model.TraceProgressData;
import com.bioproj.utils.ServiceUtil;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@Slf4j
public class TraceController {
    @Autowired
    IWorkflowService workflowService;
    @Autowired
    LiveEventsService liveEventsService;
    @Autowired
    IReportsService reportsService;

    @Autowired
    ITaskService taskProcessService;


    @Autowired
    TraceService traceService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
//    @KafkaListener(topics = "tasks-result", containerFactory = "kafkaListenerContainerFactory")
//    public void tasksResult(ConsumerRecord<String, Workflow> record) {
//        try {
//            Workflow task = record.value();
//            String key = record.key();
//            log.info(">>>>>>>>>>>>Received Message in group tasks-result: key: "+key+"  taskId:" + task.getId()+" userId:" +task.getUserId()+" status:"+task.getWorkflowStatus());
//            task = workflowService.save(task);
////            SseServerImpl.sendMessage(String.valueOf(task.getUserId()), JSON.toJSONString(task));
//            liveEventsService.publishTaskEvent(task,"tasksResult");
////            webSocketServer.sendMessageToUser(String.valueOf(task.getUserId()),JSON.toJSONString(task));
//
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//        }
//
//    }
    @KafkaListener(topics = "nextflow-trace", containerFactory = "kafkaTraceListenerContainerFactory")
    public void nextflowResult(ConsumerRecord<String, Object> record) {

        Observable.create(new ObservableOnSubscribe<ConsumerRecord<String, Object>>() {
            @Override
            public void subscribe(ObservableEmitter<ConsumerRecord<String, Object>> e) throws Exception {
                e.onNext(record);
                e.onComplete();
            }
        }).subscribe(new Consumer<ConsumerRecord<String, Object>>() {
            @Override
            public void accept(ConsumerRecord<String, Object> record) throws Throwable {



                String key = record.key();
                Object value = record.value();
                log.info("<<<<<<<<<<<<<<<<<<<<<<<<< kafka accept nextflow message  {}",key);
                if(key.length()<2)return;

                Workflow workflow = workflowService.findById(key.substring(2));
                if(workflow==null){
                    log.error("任务["+key+"]不存在！");
                    return;
                }


                if(key.startsWith("W-")){
                    log.info("workflow");
                    workflow.setWorkflowStatus(WorkflowStatus.RUNNING);
                    workflow(workflow, value);
                    liveEventsService.publishTaskEvent(workflow,"1. create workflow");
                }else if (key.startsWith("P-")){
                    log.info("processes");
                    workflow.setWorkflowStatus(WorkflowStatus.RUNNING);
                    processes(workflow, value);
                    liveEventsService.publishTaskEvent(workflow,"2. create process");
                }else if (key.startsWith("T-")){
                    log.info("tasks");
                    workflow.setWorkflowStatus(WorkflowStatus.RUNNING);
                    String obj =(String) value;
                    JSONObject jsonObject = JSON.parseObject(obj);
                    List<TraceTaskData> traceTaskData = jsonObject.getList("tasks", TraceTaskData.class);
                    TraceProgressData traceProgressData = jsonObject.getObject("progress", TraceProgressData.class);

                    traceService.handleTaskTrace(workflow,traceProgressData,traceTaskData);
//                    tasks(workflow, value);
                    liveEventsService.publishTaskEvent(workflow,"3. create tasks");
                }else if (key.startsWith("C-")){
                    log.info("complete");
                    workflow.setWorkflowStatus(WorkflowStatus.COMPLETE);
                    complete(workflow, value);
//                    kafkaTemplate.send("tasks-queue","stop",workflow);
                    liveEventsService.publishTaskEvent(workflow,"4. complete process");
                }else if (key.startsWith("R-")){
                    log.info("report");
                    workflow.setWorkflowStatus(WorkflowStatus.RUNNING);
                    report(workflow, value);
                    liveEventsService.publishTaskEvent(workflow,"R. report tasks");
                }



            }
        });
    }

    public void workflow(Workflow workflow, Object value){
        String obj =(String) value;
//        task.setProcesses(null);
        workflow.setNfTaskStatus(NFTaskStatus.CREATE);
        if(workflow.getWorkflowType()!=null && workflow.getWorkflowType().equals(WorkflowType.SAMPLE_QUEUE)){
            return;
        }
        taskProcessService.delByWorkflowId(workflow.getId());
        reportsService.delWorkflowId(workflow.getId());
        workflowService.save(workflow);
//        System.out.println();

    }
    public void processes(Workflow task, Object value){
//        Task task = taskService.findById(workflowId);
        String obj =(String) value;

        JSONObject jsonObject = JSON.parseObject(obj);
        List<String> processNames = jsonObject.getList("processNames", String.class);
        List<ProcessesProgress> processesList = processNames.stream().map(item -> {
            ProcessesProgress processes = new ProcessesProgress();
            processes.setName(item);
            return processes;
        }).collect(Collectors.toList());
        task.setProcessesProgresses(processesList);
        task.setNfTaskStatus(NFTaskStatus.RUNNING);
        workflowService.save(task);

    }
//    public void tasks(Workflow task, Object value){
////        Task task = taskService.findById(workflowId);
//
//        String obj =(String) value;
//        JSONObject jsonObject = JSON.parseObject(obj);
//        List<Task> taskProcesses = jsonObject.getList("tasks", Task.class);
//
//
//        List<Sample> samples = task.getSamples();
//        Map<String, Sample> sampleMap = ServiceUtil.convertToMap(samples, Sample::getDataKey);
//
////
////        List<Task> taskProcessList = taskProcessService.findByTaskId(task.getId());
////        Map<Integer, Task> dbTaskMap = ServiceUtil.convertToMap(taskProcessList, Task::getTaskId);
////        for (Task item : taskProcesses){
////            Task dbTask = dbTaskMap.get(item.getTaskId());
////            if(dbTask==null){
////                dbTask = new Task();
////            }
//////            BeanUtils.copyProperties(item,dbTask );
//////            if(sampleMap.containsKey(item.getDataKey())){
//////                Sample sample = sampleMap.get(item.getDataKey());
//////                BeanUtils.copyProperties(sample,dbTask );
//////            }
//////            dbTask.setRelationTaskId(task.getId());
//////            Task saveTask = taskProcessService.save(dbTask);
////        }
//
////        task.getSubTasks().addAll(addTasks);
//
//
//
//
//        List<ProcessesProgress> processes = jsonObject.getJSONObject("progress").getList("processes", ProcessesProgress.class);
//        Map<String, ProcessesProgress> processesMap = ServiceUtil.convertToMap(task.getProcessesProgresses(), ProcessesProgress::getName);
//        for (ProcessesProgress process :processes){
//            if(processesMap.containsKey(process.getName())){
//                ProcessesProgress dbProcess = processesMap.get(process.getName());
//                BeanUtils.copyProperties(process,dbProcess);
//            }
//        }
//
//
//        Progress progress = jsonObject.getObject("progress", Progress.class);
//        task.setProgress(progress);
//
//        workflowService.save(task);
//    }

    public void report(Workflow workflow, Object value){
        String obj =(String) value;

        List<Reports> reportsList = reportsService.findByTaskId(workflow.getId());


        List<Reports> reports = JSON.parseArray(obj, Reports.class);
        Map<String, Reports> reportsMap = ServiceUtil.convertToMap(reportsList, Reports::getDestination);
        List<Reports> needSaveReport = new ArrayList<>();
        for (Reports item : reports){
            if(!reportsMap.containsKey(item.getDestination())){
                item.setWorkflowId(workflow.getId());
                item.setUserId(workflow.getUserId());
                needSaveReport.add(item);
            }
        }

        //        List<Reports> dbReports = task.getReports();
//        reportsList.addAll(needSaveReport);

//        for (Integer i=0;i<reportsList.size();i++){
//            reportsList.get(i).setId(i);
//            reportsList.get(i).setTaskId(task.getId());
//        }
        reportsService.saveAll(needSaveReport);

//        taskService.save(task);
//        System.out.println();
    }

    public void complete(Workflow task, Object value){
        task.setWorkflowStatus(WorkflowStatus.COMPLETE);
        task.setNfTaskStatus(NFTaskStatus.COMPLETE);

//        // 对于命令行运行缓存的task
//        if(task.getSubTasks()==null){
//            task.setSubTasks(new ArrayList<>());
//        }
//        String obj =(String) value;
//        JSONObject jsonObject = JSON.parseObject(obj);
//        List<SubTasks> subTasks = jsonObject.getList("tasks", SubTasks.class);
//
//        List<SubTasks> addTasks = new ArrayList<>();
//
//        Map<Integer, SubTasks> dbTaskMap = ServiceUtil.convertToMap(task.getSubTasks(), SubTasks::getTaskId);
//        for (SubTasks item : subTasks){
//            if(dbTaskMap.containsKey(item.getTaskId())){
//                SubTasks dbTask = dbTaskMap.get(item.getTaskId());
//                BeanUtils.copyProperties(item,dbTask );
//            }else {
//                addTasks.add(item);
//            }
//        }
//
//        task.getSubTasks().addAll(addTasks);
//        taskService.save(task);
        workflowService.save(task);
//        liveEventsService.publishTaskEvent(task);

    }
}
