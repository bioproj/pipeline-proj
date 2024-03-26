package com.bioproj.live;

import com.bioproj.pojo.task.Workflow;
import org.reactivestreams.Publisher;
import org.springframework.http.codec.ServerSentEvent;

import java.util.List;

public interface LiveEventsService {
    void publishEvent(LiveUpdate traceSseResponse);

    Publisher<ServerSentEvent<List<LiveUpdate>>> getEventPublisher();

    default void publishWorkflowEvent(Long targetUserId, String workflowId) {
//        publishEvent(LiveUpdate.of(targetUserId, workflowId, LiveAction.WORKFLOW_UPDATE)  );
    }

    default void publishProgressEvent(Long targetUserId, String workflowId) {
//        publishEvent(LiveUpdate.of(targetUserId, workflowId, LiveAction.PROGRESS_UPDATE));
    }

    default void publishWorkflowEvent(Workflow2 workflow) {
        publishEvent(new LiveUpdate("aaaaaaaaaaaaaaa"));
    }
    default void publishTaskEvent(Workflow task) {
        LiveUpdate liveUpdate = new LiveUpdate();
        liveUpdate.setTaskStatus(task.getWorkflowStatus());
        liveUpdate.setSubmitId(task.getSubmitId());
        publishEvent(liveUpdate);
    }
    default void publishTaskEvent(Workflow task, String eventType) {
        LiveUpdate liveUpdate = new LiveUpdate();
        liveUpdate.setTaskStatus(task.getWorkflowStatus());
        liveUpdate.setSubmitId(task.getSubmitId());
        liveUpdate.setEventType(eventType);
        publishEvent(liveUpdate);
    }

    default void publishProgressEvent(Workflow2 workflow) {
//        publishEvent(LiveUpdate.of(workflow.owner.id, workflow.id, LiveAction.PROGRESS_UPDATE));
    }
}
