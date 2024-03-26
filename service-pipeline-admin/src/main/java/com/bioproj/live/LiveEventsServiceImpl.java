package com.bioproj.live;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.PublishProcessor;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.internal.util.Closure;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class LiveEventsServiceImpl  implements LiveEventsService {
    @Value("${live.buffer.time:1s}")
    Duration bufferTimeout;

    @Value("${live.buffer.count:100}")
    Integer bufferCount;

    @Value("${live.buffer.heartbeat:1m}")
    Duration heartbeatDuration;
    BackpressureBuffer<LiveUpdate> buffer;

    PublishProcessor<List<LiveUpdate>> eventProcessor;

    Flowable<ServerSentEvent<List<LiveUpdate>>> eventPublisher;



    @PostConstruct
    void initialize() {
        log.info("Creating SSE event buffer flowable timeout=$bufferTimeout count=$bufferCount heartbeat=$heartbeatDuration");

        eventProcessor = PublishProcessor.create();

        eventPublisher = eventProcessor.map(  traces ->{
            log.info("Publishing map traces ({}) $traces",traces.size());
            return ServerSentEvent.<List<LiveUpdate>> builder()
                    .data(traces)
                    .build();
        });


        // -- implements the back pressure logic
        buffer = new BackpressureBuffer<LiveUpdate>()
                .setName("Live events buffer")
                .setTimeout(bufferTimeout)
                .setHeartbeat(heartbeatDuration)
                .setMaxCount(bufferCount)
                .onNext((Closure<List<LiveUpdate>>) updates -> {
                    log.info("Publishing live updates -> ({}) {}",updates.size(),updates);
                    eventProcessor.onNext(updates);
                }).start();
    }


    @Override
    public void publishEvent(LiveUpdate liveUpdate) {
        buffer.offer(liveUpdate);
    }

    @Override
    public Publisher<ServerSentEvent<List<LiveUpdate>>> getEventPublisher() {
        return eventPublisher
                .mergeWith( Flowable.just(ServerSentEvent.<List<LiveUpdate>> builder()
                        .data(Collections.emptyList())
                        .build()) );
//                .mergeWith( Flowable.just(ServerSentEvent.of(Collections.emptyList())) );
    }

    @PreDestroy
    protected void destroy() {
        log.info("kill thread {}!",buffer.getName());
        buffer.terminateAndAwait();
    }

}
