package com.bioproj.service.impl;

import com.bioproj.domain.ProcessesProgress;
import com.bioproj.domain.enums.TaskStatus;
import com.bioproj.domain.enums.WorkflowType;
import com.bioproj.domain.vo.TaskDataVo;
import com.bioproj.domain.vo.WorkflowResult;
import com.bioproj.domain.vo.WorkflowTaskVo;
import com.bioproj.pojo.task.Task;
import com.bioproj.pojo.task.TaskData;
import com.bioproj.domain.vo.TaskVo;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.repository.TaskRepository;
import com.bioproj.repository.WorkflowRepository;
import com.bioproj.service.ISampleTaskService;
import com.bioproj.service.ITaskDataService;
import com.bioproj.service.ITaskService;
import com.bioproj.trace.model.TraceTaskData;
import com.bioproj.utils.ServiceUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements ITaskService {

    @Autowired
    TaskRepository taskRepository;


    @Autowired
    ITaskDataService taskDataService;

    @Autowired
    WorkflowRepository workflowRepository;

    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;


    @Autowired
    private ISampleTaskService sampleTaskService;


//    @Override
//    public List<Task> findByTaskId(String id) {
//        List<Task> taskProcesses = taskRepository.findAll(Example.of(Task.builder().relationTaskId(id).build()));
//        return taskProcesses;
//    }

    @Override
    public Task save(Task dbTask) {
        Task taskProcess = taskRepository.save(dbTask);
        return taskProcess;
    }

    @Override
    public List<Task> findByWorkflowId(String workflowId){
        return taskRepository.findAll(Example.of(Task.builder().workflowId(workflowId).build()));
    }

    @Override
    public List<Task> delByWorkflowId(String workflowId) {
        List<Task> taskProcessList = findByWorkflowId(workflowId);
        Set<String> dataIds = ServiceUtil.fetchProperty(taskProcessList, Task::getDataId);
        taskDataService.del(dataIds);
        taskRepository.deleteAll(taskProcessList);
        return taskProcessList;
    }

    @Override
    public Task findById(String id) {
        return taskRepository.findById(id).orElse(null);
    }




    @Override
    public Task save(Workflow workflow, TraceTaskData traceTaskData) {
        Task taskParams = new Task();
        TaskData taskDataParams = new TaskData();
        BeanUtils.copyProperties(traceTaskData,taskParams);
        BeanUtils.copyProperties(traceTaskData,taskDataParams);
        TaskData taskData = taskDataService.getTaskDataBySessionIdAndHash(workflow.getSessionId(), taskDataParams.getHash());
        Task task =  findByWorkflowAndTaskId(workflow.getId(), taskParams.getTaskId());
        if(task==null){
            task = new Task();
        }

        BeanUtils.copyProperties(taskParams,task,"id","workflowId");
        task.setWorkflowId(workflow.getId());
        if(taskParams.checkIsCached() && taskData!=null && taskData.getWorkdir()!=null ){
            task.setDataId(taskData.getId());
        }else {
            if(taskData==null){
                taskData = new TaskData();
            }
            BeanUtils.copyProperties(taskDataParams,taskData,"id","sessionId");
            taskData.setSessionId(workflow.getSessionId());
            taskData = taskDataService.save(taskData);
            task.setDataId(taskData.getId());
        }
        return taskRepository.save(task);




//        if(workflow.getWorkflowType()!=null && workflow.getWorkflowType().equals(WorkflowType.SAMPLE_QUEUE) && taskParams.getTag()!=null ){
//
//        }else{
//
//        }
    }




    @Override
    @Deprecated
    public Task save(Workflow workflow, Task taskParams, TaskData taskDataParams) {


//      =null;
        if(workflow.getWorkflowType()!=null && workflow.getWorkflowType().equals(WorkflowType.SAMPLE_QUEUE) && taskParams.getTag()!=null ){
            Task task = findByWorkflowAndTaskTag(workflow.getId(), taskParams.getTag());
            if(task==null){
                task = new Task();
            }
            task.setWorkflowId(workflow.getId());
            BeanUtils.copyProperties(taskParams,task,"id","workflowId");
//            List<TaskData> taskData = ;
//            if(task.getTaskDataMap()==null){
//                task.setTaskDataMap(new HashMap<>());
//            }
////            Map<String, TaskData> taskDataMap = ServiceUtil.convertToMap(task.getTaskData(), TaskData::getProcess);
//            task.getTaskDataMap().put(taskDataParams.getProcess(),taskDataParams);
//            addOrUpdateTaskData(task,taskDataParams);

            List<String> processesProgress = ServiceUtil.fetchListProperty(workflow.getProcessesProgresses(), ProcessesProgress::getName);
            if(processesProgress.size()>0){
                String lastProcess = processesProgress.get(processesProgress.size()-1);
                if(lastProcess.equals(task.getProcess()) && workflow.getWorkflowType()!=null && workflow.getWorkflowType().equals(WorkflowType.SAMPLE_QUEUE) && (task.getStatus().equals(TaskStatus.COMPLETED) || task.getStatus().equals(TaskStatus.CACHED))){
                    String withoutQuotes = Pattern.compile("\"").matcher(task.getTag()).replaceAll("");
                    WorkflowResult workflowResult = new WorkflowResult();
                    BeanUtils.copyProperties(workflow,workflowResult);
                    kafkaTemplate.send("ngs-analysis-result",withoutQuotes,workflowResult);
                }
            }

            return taskRepository.save(task);
        }else{
            TaskData taskData = taskDataService.getTaskDataBySessionIdAndHash(workflow.getSessionId(), taskDataParams.getHash());
            Task task =  findByWorkflowAndTaskId(workflow.getId(), taskParams.getTaskId());
            if(task==null){
                task = new Task();
            }

            BeanUtils.copyProperties(taskParams,task,"id","workflowId");
            task.setWorkflowId(workflow.getId());
            if(taskParams.checkIsCached() && taskData!=null && taskData.getWorkdir()!=null ){
                task.setDataId(taskData.getId());
            }else {
                if(taskData==null){
                    taskData = new TaskData();
                }
                BeanUtils.copyProperties(taskDataParams,taskData,"id","sessionId");
                taskData.setSessionId(workflow.getSessionId());
                taskData = taskDataService.save(taskData);
                task.setDataId(taskData.getId());
            }
            return taskRepository.save(task);
        }

    }
    @Override
    public List<WorkflowTaskVo> findByTaskTag(String tag) {
        List<Task> taskList = taskRepository.findAll(Example.of(Task.builder().tag(tag).build()));

        Set<String> workflowIds = ServiceUtil.fetchProperty(taskList, Task::getWorkflowId);
        List<Workflow> workflowList = workflowRepository.findByIdsIn(workflowIds);
        Map<String, Workflow> workflowMap = ServiceUtil.convertToMap(workflowList, Workflow::getId);


        List<WorkflowTaskVo> workflowTaskVos = taskList.stream().map(item -> {
            WorkflowTaskVo workflowTaskVo = new WorkflowTaskVo();

            List<TaskData> taskDataList = item.getTaskData();
            List<TaskDataVo> taskDataVos = taskDataList.stream().map(taskData -> {
                TaskDataVo taskDataVo = new TaskDataVo();
                BeanUtils.copyProperties(taskData, taskDataVo);
                return taskDataVo;
            }).collect(Collectors.toList());

            if(workflowMap.containsKey(item.getWorkflowId())){
                Workflow workflow = workflowMap.get(item.getWorkflowId());
                workflowTaskVo.setWorkflowName(workflow.getName());
            }

            workflowTaskVo.setTaskDataVos(taskDataVos);
            workflowTaskVo.setSampleName(item.getTag());
            return workflowTaskVo;
        }).collect(Collectors.toList());


        return workflowTaskVos;
    }
    @Override
    public Task findByWorkflowAndTaskTag(String workflowId, String tag) {
        List<Task> taskList = taskRepository.findAll(Example.of(Task.builder().workflowId(workflowId).tag(tag).build()));
        if(taskList.size()>0) return taskList.get(0);
        return null;
    }

    private Task findByWorkflowAndTaskId(String workflowId, Integer taskId) {
        List<Task> taskList = taskRepository.findAll(Example.of(Task.builder().workflowId(workflowId).taskId(taskId).build()));
        if(taskList.size()>0) return taskList.get(0);
        return null;
    }

    @Override
    public Page<TaskVo> pageByWorkflowId(String workflowId, PageRequest pageRequest) {
        Page<Task> tasks = taskRepository.findAll(Example.of(Task.builder().workflowId(workflowId).build()), pageRequest);
        Set<String> dataIds = ServiceUtil.fetchProperty(tasks.getContent(), Task::getDataId);
        List<TaskData> taskDatas = taskDataService.listByIds(dataIds);
        Map<String, TaskData> taskDataMap = ServiceUtil.convertToMap(taskDatas, TaskData::getId);

        Page<TaskVo> taskVos = tasks.map(task -> {
            TaskVo taskVo = new TaskVo();
            BeanUtils.copyProperties(task,taskVo);
            TaskData taskData = taskDataMap.get(task.getDataId());
            if(taskData!=null){
                BeanUtils.copyProperties(taskData,taskVo,"id");
            }
            return taskVo;
        });
        return taskVos;
    }



}
