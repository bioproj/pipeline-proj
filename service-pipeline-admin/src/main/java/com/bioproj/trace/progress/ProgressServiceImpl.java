package com.bioproj.trace.progress;

import cn.hutool.core.lang.copier.Copier;
import com.bioproj.pojo.task.Task;
import com.bioproj.pojo.task.Workflow;
import com.bioproj.trace.model.TraceProgressData;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class ProgressServiceImpl implements ProgressService{

    @Autowired
    ProgressOperationsImpl target;

    PublishSubject<Runnable> publisher;

    @PostConstruct
    void init() {
//        log.info "Creating workflow progress updater -- store=${store.getClass().getSimpleName()};"
        publisher = PublishSubject.create();
        publisher
                .observeOn(Schedulers.computation())
                .subscribe(this::doEvent);
    }

    public void doEvent(Runnable event) {
        if (event instanceof Runnable) {
            ((Runnable) event).run();
        } else {
            System.out.println("Illegal progress event=" + event);
        }
    }
    @Override
    public void updateProgress(String workflowId, TraceProgressData progress) {
        target.updateProgress(workflowId, progress);
    }

    @Override
    public void aggregateMetrics(String workflowId, List<Task> tasks) {

    }

    @Override
    public void create(Workflow workflow, TraceProgressData traceProgressData) {

    }

    @Override
    public void updateProgress(Workflow workflow, TraceProgressData traceProgressData) {
        publisher.onNext( () -> target.updateProgress(workflow, traceProgressData));

    }
}
