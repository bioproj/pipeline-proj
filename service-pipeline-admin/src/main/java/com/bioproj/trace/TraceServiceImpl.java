package com.bioproj.trace;

import com.bioproj.domain.enums.WorkflowType;
import com.bioproj.pojo.task.*;
import com.bioproj.service.ISampleTaskService;
import com.bioproj.service.ITaskService;
import com.bioproj.service.IWorkflowService;
import com.bioproj.trace.audit.AuditEventPublisher;
import com.bioproj.trace.cost.CostService;
import com.bioproj.trace.model.*;
import com.bioproj.trace.progress.ProgressService;
import com.bioproj.utils.ServiceUtil;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Phaser;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TraceServiceImpl implements TraceService{
    private static final Random random = new Random();
    private Integer bufferSize=10000;
    private     Integer maxAttempts=3;
    @Autowired
    AuditEventPublisher eventPublisher;
    @Autowired
    ProgressService progressService;
    private Phaser phaser;
//    private ExecutorService taskSaveExecutor;

    @Autowired
    ITaskService taskService;

    @Autowired
    ISampleTaskService sampleTaskService;

    @Autowired
    CostService costService;


    @Autowired
    IWorkflowService workflowService;

    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @EqualsAndHashCode
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TaskEntry {
//        Task task;
//        TaskData taskData;
        Workflow workflow;
        TraceTaskData traceTaskData;

    }

    @PostConstruct
    void init() {
//        log.info("+ Creating trace service poolMinSize=$poolMinSize; poolMaxSize=$poolMaxSize; poolQueueSize=$poolQueueSize; maxAttempts=$maxAttempts; aggregate bufferCount=$bufferSize; poolTerminationTimeout=$poolTerminationTimeout")
        phaser = new Phaser();
        phaser.register();
        // executor to save tasks
    }

//    @PreDestroy
//    void cleanup() {
////        log.info "< Flushing trace tasks publisher"
//        // the onComplete event is needed to flush partial buffered task entries
//        phaser.arriveAndAwaitAdvance();
//        ThreadPoolExecutor pool = threadPoolTaskExecutor.getThreadPoolExecutor();
//        try {
//            pool.shutdown();
//            // Wait a while for existing tasks to terminate
//            if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
//                pool.shutdownNow(); // Cancel currently executing tasks
//                // Wait a while for tasks to respond to being cancelled
//                if (!pool.awaitTermination(30, TimeUnit.SECONDS))
//                    System.err.println("Pool did not terminate");
//            }
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
////        taskSaveExecutor.awaitTermination(poolTerminationTimeout.toMillis(), TimeUnit.MILLISECONDS);
//        // shutdown the thread pool
////        taskSaveExecutor.shutdown();
////        taskSaveExecutor.awaitTermination(poolTerminationTimeout.toMillis(), TimeUnit.MILLISECONDS);
//    }

    @Override
    public Workflow processWorkflowTrace(TraceWorkflowRequest request) {
        return null;
    }

    @Override
    public List<Task> processTaskTrace(TraceTaskRequest request) {
        return null;
    }

    @Override
    public void keepAlive(String workflowId) {

    }

    @Override
    public Workflow handleFlowBegin(TraceBeginRequest request) {
        if( !request.getWorkflow().checkIsRunning() )
            throw new IllegalStateException("Workflow status should be running -- current=${request.workflow.status}");
        // 创建workflow以及process

        progressService.updateProgress(request.getWorkflow().getId(), TraceProgressData.EMPTY);
        eventPublisher.workflowCreation(request.getWorkflow().getId());
        return null;
    }

    @Override
    public Workflow handleFlowComplete(TraceCompleteRequest request) {
        return null;
    }
//    @NotTransactional

    @Override
    public void handleTaskTrace(Workflow workflow, TraceProgressData progress, List<TraceTaskData> traceTaskDataList) {

        progressService.updateProgress(workflow, progress);
        if( traceTaskDataList==null || traceTaskDataList.isEmpty() ) return;

        // save tasks using a rx publisher in separate thread
        PublishSubject<TaskEntry> publisher = createTasksPublisher();

        for (TraceTaskData traceTaskData :traceTaskDataList){
//            Task task = new Task();
//            TaskData taskData = new TaskData();
//            BeanUtils.copyProperties(taskRequestDto,task);
//            BeanUtils.copyProperties(taskRequestDto,taskData);
            publisher.onNext(new TaskEntry( workflow, traceTaskData));
        }




//        Workflow workflow = workflowService.findById(workflowId);
//        for( TaskRequest taskRequest : taskRequests )
//            publisher.onNext(new TaskEntry(taskRequest.getTask(),taskRequest.getTaskData(), workflow));
        publisher.onComplete();
    }

    private PublishSubject<TaskEntry> createTasksPublisher() {
        phaser.bulkRegister(2);
        PublishSubject<TaskEntry> subject = PublishSubject.create();

        Flowable<TaskEntry> receiver = subject
                .toFlowable(BackpressureStrategy.BUFFER)
                .parallel()
                .runOn( Schedulers.from(threadPoolTaskExecutor) )
                .map((TaskEntry  task) -> safeCheckTask(task) )
                .sequential()
                .share();

        // save tasks
        receiver.doOnComplete( ()->  phaser.arriveAndDeregister())
                .subscribe(task ->   safeSaveTask(task) ,
                         err-> log.error("Unexpected error while saving task",err) );



        // aggregate metrics
//        receiver.buffer(bufferSize)
//                .flatMapIterable((List<TaskEntry> entries) -> {
//                    Map<String, List<TaskEntry>> groupedEntries = new HashMap<>();
//                    for (TaskEntry entry : entries) {
//                        String workflowId = entry.getWorkflow().getId();
//                        groupedEntries.computeIfAbsent(workflowId, k -> new ArrayList<>()).add(entry);
//                    }
//                    return groupedEntries.values();})
//                .doOnComplete(() ->phaser.arriveAndDeregister() )
//                .subscribe( (List<TaskEntry> it)->safeAggregateMetrics(it) , err-> log.error("Unexpected error while aggregating metrics",err) );

        return subject;
    }

    @Override
    public void handleHeartbeat(String workflowId, TraceProgressData progress) {

    }
    @Transactional(propagation = Propagation.REQUIRED)
    protected TaskEntry checkTask(TaskEntry entry) {
            Task task = new Task();
            TaskData taskData = new TaskData();
            BeanUtils.copyProperties(entry.getTraceTaskData(),task);
            BeanUtils.copyProperties(entry.getTraceTaskData(),taskData);
        if(task.getStatus().isTerminal()){
            BigDecimal cost = costService.computeCost(taskData);
            entry.getTraceTaskData().setCost(cost);
        }

        return entry;
    }

    protected TaskEntry safeCheckTask(TaskEntry entry) {
        try {
            return checkTask(entry);
        }
        catch( Throwable e ) {
            log.error("Unable to determine compute cost for taskId=${entry.task.taskId}; workflow Id=$entry.workflow.id", e);
            return entry;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected void saveTask(TaskEntry entry) {
        log.info("Saving task entry={}",entry.getTraceTaskData().getTaskId());
//        entry.task.setWorkflowId(entry.workflow.getId());
        Workflow workflow = entry.getWorkflow();
        TraceTaskData traceTaskData = entry.getTraceTaskData();
//        Task taskParams = new Task();
//        TaskData taskDataParams = new TaskData();
//        BeanUtils.copyProperties(traceTaskData,taskParams);
//        BeanUtils.copyProperties(traceTaskData,taskDataParams);

        if(workflow.getWorkflowType()!=null && workflow.getWorkflowType().equals(WorkflowType.SAMPLE_QUEUE) && traceTaskData.getTag()!=null ) {
            sampleTaskService.save(entry.getWorkflow(),entry.getTraceTaskData());
        }else {
            taskService.save(entry.getWorkflow(),entry.getTraceTaskData());
        }

    }
    @Transactional(propagation = Propagation.REQUIRED)
    protected void aggregateMetrics(List<TaskEntry> entries) {
        log.trace("Trace aggregating metrics workflowId=${entries.first()?.workflow?.id} size=${entries.size()}");
        String workflowId = entries.get(0).getWorkflow().getId();
//        List<Task> tasks = ServiceUtil.fetchListProperty(entries, TaskEntry::getTask);
        List<Task> tasks = entries.stream().map(item -> {
            Task task = new Task();
            BeanUtils.copyProperties(item.getTraceTaskData(), task);
            return task;
        }).collect(Collectors.toList());
        progressService.aggregateMetrics(workflowId, tasks);
    }

    protected void safeSaveTask(TaskEntry entry) {
        saveTask(entry);
//        int attempt = 1;
//        while(true) try {
//
//            return;
//        }catch( Exception e ) {
//            String msg = "Failed to save task entry id=${entry?.task?.id}; taskId=${entry?.task?.taskId}; workflow id=${entry?.workflow?.id}; attempt=$attempt";
//            if( attempt++ >= maxAttempts ) {
//                log.error(msg, e);
//                break;
//            }
//            // sleep and try it again
//            Integer  delay= 50  + random.nextInt(500);
//            msg += "; cause=${e.message ?: e.toString()}; await $delay msg and retry";
//            log.error(msg);
//            try {
//                Thread.sleep(delay);
//            } catch (InterruptedException ex) {
//                throw new RuntimeException(ex);
//            }
//        }
    }
    protected void safeAggregateMetrics(List<TaskEntry> entries) {
        int attempt = 1;
        while(true) try {
            aggregateMetrics(entries);
            return;
        }
        catch( Exception e ) {
            String msg = "Failed to aggregate metrics for task entries=${entries?.task?.id}; taskId=${entries?.task?.taskId}; workkflow id=${entries?.workflow?.id}; attempt=$attempt";
            if( attempt++ >= maxAttempts ) {
                log.error(msg, e);
                break;
            }
            // sleep and try it again
            Integer delay = 50  + random.nextInt(500);
            msg += "; cause=${e.message ?: e.toString()}; await $delay msg and retry";
            log.error(msg);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
