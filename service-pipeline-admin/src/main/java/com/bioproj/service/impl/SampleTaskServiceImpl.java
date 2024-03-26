package com.bioproj.service.impl;


import com.bioproj.domain.ProcessesProgress;
import com.bioproj.domain.enums.TaskStatus;
import com.bioproj.domain.enums.WorkflowType;
import com.bioproj.domain.vo.K8sAppVo;
import com.bioproj.domain.vo.TaskDataVo;
import com.bioproj.domain.vo.WorkflowResult;
import com.bioproj.domain.vo.WorkflowTaskVo;
import com.bioproj.pojo.K8sApp;
import com.bioproj.pojo.task.*;
import com.bioproj.repository.SampleTaskRepository;
import com.bioproj.repository.WorkflowRepository;
import com.bioproj.service.ISampleTaskService;
import com.bioproj.trace.model.TraceTaskData;
import com.bioproj.utils.ServiceUtil;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.R;
import com.mbiolance.cloud.auth.domain.dto.SysUserDto;
import com.mbiolance.cloud.auth.domain.vo.QueryCriteriaVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SampleTaskServiceImpl implements ISampleTaskService {

    @Autowired
    private SampleTaskRepository sampleTaskRepository;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Override
    public SampleTask save(Workflow workflow, TraceTaskData traceTaskData) {
        // 以样本为中心保存任务

        SampleTask sampleTask = findByWorkflowAndTag(workflow.getId(), traceTaskData.getTag());
        if(sampleTask==null){
            sampleTask = new SampleTask();
            log.info("添加样本{}",sampleTask.getTag());
        }else {
            log.info("更新样本{}",sampleTask.getTag());
        }
        sampleTask.setWorkflowId(workflow.getId());
        BeanUtils.copyProperties(traceTaskData,sampleTask,"id","workflowId");
//            List<TaskData> taskData = ;
//            if(task.getTaskDataMap()==null){
//                task.setTaskDataMap(new HashMap<>());
//            }
////            Map<String, TaskData> taskDataMap = ServiceUtil.convertToMap(task.getTaskData(), TaskData::getProcess);
//            task.getTaskDataMap().put(taskDataParams.getProcess(),taskDataParams);
        addOrUpdateTaskData(sampleTask,traceTaskData);

        List<String> processesProgress = ServiceUtil.fetchListProperty(workflow.getProcessesProgresses(), ProcessesProgress::getName);
        if(processesProgress.size()>0){
            String lastProcess = processesProgress.get(processesProgress.size()-1);
            if(lastProcess.equals(sampleTask.getProcess()) && workflow.getWorkflowType()!=null && workflow.getWorkflowType().equals(WorkflowType.SAMPLE_QUEUE) && (sampleTask.getStatus().equals(TaskStatus.COMPLETED) || sampleTask.getStatus().equals(TaskStatus.CACHED))){
                String withoutQuotes = Pattern.compile("\"").matcher(sampleTask.getTag()).replaceAll("");
                WorkflowResult workflowResult = new WorkflowResult();
                BeanUtils.copyProperties(workflow,workflowResult);
                workflowResult.setOutputDir(workflow.getResultDir()+ File.separator+workflow.getId()+File.separator+withoutQuotes);
                log.info("样本完成[{}]通知kafka[{}]",sampleTask.getTag(),"ngs-analysis-result");
                kafkaTemplate.send("ngs-analysis-result",withoutQuotes,workflowResult);
            }
        }

        return sampleTaskRepository.save(sampleTask);
    }

    private SampleTask findByWorkflowAndTag(String id, String tag) {
        SampleTask sampleTask = SampleTask.builder()
                .workflowId(id)
                .tag(tag)
                .build();
        List<SampleTask> sampleTaskList = sampleTaskRepository.findAll(Example.of(sampleTask));
        if(sampleTaskList.size()>0) return sampleTaskList.get(0);
        return null;
    }

    public void addOrUpdateTaskData( SampleTask sampleTask , TraceTaskData traceTaskData){

        SampleTaskData sampleTaskData = new SampleTaskData();
        BeanUtils.copyProperties(traceTaskData,sampleTaskData);



        if(sampleTask.getSampleTaskData()==null){
            sampleTask.setSampleTaskData(new ArrayList<>());
        }

        List<SampleTaskData> taskDataList = sampleTask.getSampleTaskData();

        boolean isExist = false;

        for (int i = 0; i < taskDataList.size(); i++) {
            SampleTaskData findSampleTask = taskDataList.get(i);

            if (findSampleTask.getProcess().equals(traceTaskData.getProcess())) {
                // 根据id字段进行判断，如果存在则更新对象
                taskDataList.set(i, sampleTaskData);
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            // 如果不存在，则添加对象
            taskDataList.add(sampleTaskData);
        }
    }


    @Override
    public List<WorkflowTaskVo> findByTag(String tag) {
        List<SampleTask> taskList = sampleTaskRepository.findAll(Example.of(SampleTask.builder().tag(tag).build()));

        Set<String> workflowIds = ServiceUtil.fetchProperty(taskList, SampleTask::getWorkflowId);
        List<Workflow> workflowList = workflowRepository.findByIdsIn(workflowIds);
        Map<String, Workflow> workflowMap = ServiceUtil.convertToMap(workflowList, Workflow::getId);


        List<WorkflowTaskVo> workflowTaskVos = taskList.stream().map(item -> {
            WorkflowTaskVo workflowTaskVo = new WorkflowTaskVo();

            List<SampleTaskData> taskDataList = item.getSampleTaskData();
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
    public WorkflowTaskVo findByTagAndWorkflowId(String tag, String workflowId) {
        List<SampleTask> taskList = sampleTaskRepository.findAll(Example.of(SampleTask.builder()
                                        .workflowId(workflowId)
                                        .tag(tag).build()));
        if(taskList.size()==0){
            throw new RuntimeException("["+tag+"] and ["+workflowId+"]没有找到！");
        }
        SampleTask sampleTask = taskList.get(0);
        Optional<Workflow> workflowOptional = workflowRepository.findById(workflowId);
        if(workflowOptional.isPresent()){
            throw new RuntimeException("["+workflowId+"]不存在！");
        }
        Workflow workflow = workflowOptional.get();
        WorkflowTaskVo workflowTaskVo = new WorkflowTaskVo();

        List<SampleTaskData> taskDataList = sampleTask.getSampleTaskData();
        List<TaskDataVo> taskDataVos = taskDataList.stream().map(taskData -> {
            TaskDataVo taskDataVo = new TaskDataVo();
            BeanUtils.copyProperties(taskData, taskDataVo);
            return taskDataVo;
        }).collect(Collectors.toList());
        workflowTaskVo.setWorkflowName(workflow.getName());


//        if(workflowMap.containsKey(item.getWorkflowId())){
//            Workflow workflow = workflowMap.get(item.getWorkflowId());
//            workflowTaskVo.setWorkflowName(workflow.getName());
//        }

        workflowTaskVo.setTaskDataVos(taskDataVos);
        workflowTaskVo.setSampleName(sampleTask.getTag());
        return workflowTaskVo;


//        Set<String> workflowIds = ServiceUtil.fetchProperty(taskList, SampleTask::getWorkflowId);
//        List<Workflow> workflowList = workflowRepository.findByIdsIn(workflowIds);
//        Map<String, Workflow> workflowMap = ServiceUtil.convertToMap(workflowList, Workflow::getId);
//
//
//        List<WorkflowTaskVo> workflowTaskVos = taskList.stream().map(item -> {
//
//        }).collect(Collectors.toList());
//
//
//        return workflowTaskVos;
    }

    @Override
    public List<SampleTask> findByWorkflowId(String workflowId) {
        SampleTask sampleTask = SampleTask.builder().workflowId(workflowId).build();
        List<SampleTask> sampleTaskList = sampleTaskRepository.findAll(Example.of(sampleTask));
        return sampleTaskList;
    }

    @Override
    public PageModel<SampleTask> pageBy(String workflowId, Integer number, Integer size, List<QueryCriteriaVo> criteriaVos) {
        number -= 1;
        number = number <= 0 ? 0 : number;
        size = size <= 0 ? 10 : size;
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(number, size, Sort.by(order));


        SampleTask sampleTask = SampleTask.builder().workflowId(workflowId).build();

        Page<SampleTask> page = sampleTaskRepository.findAll(Example.of(sampleTask),pageRequest);


        return PageModel.<SampleTask>builder()
                .count((int) page.getTotalElements())
                .content(page.getContent())
                .number(number + 1)
                .size(size)
                .build();
    }



}
