package com.bioproj.controller;

import com.bioproj.live.LiveEventsService;
import com.bioproj.live.LiveUpdate;
import com.bioproj.live.Workflow2;
import io.reactivex.rxjava3.core.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping
@Slf4j
//https://www.baeldung.com/spring-server-sent-events
public class TestFlux {
    private final ApplicationEventPublisher eventPublisher;
    @Autowired
    LiveEventsService liveEventsService;
    @GetMapping(value = "/live", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Publisher<ServerSentEvent<List<LiveUpdate>>> live(HttpServletRequest request) {
        log.debug("== Client subscribing to live events [remoteAddress=${request.remoteAddress}]");
        try {
            return liveEventsService.getEventPublisher();
        }
        catch (Exception e) {
//            String message = "Unexpected error while obtaining event emitter"
//            log.error("${message} | ${e.message}", e)

            return Flowable.just( Duration.ofSeconds(1))
                    .map(sequence -> ServerSentEvent.<List<LiveUpdate>> builder()
                            .id(String.valueOf(sequence))
                            .event("periodic-event")
                            .data(Collections.emptyList())
                            .build());
        }
    }


    public TestFlux(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    @GetMapping(path = "/stream-flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamFlux() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> "Flux - " + LocalTime.now().toString());
    }
    @GetMapping("/stream-sse")
    public Flux<ServerSentEvent<String>> streamEvents() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> ServerSentEvent.<String> builder()
                        .id(String.valueOf(sequence))
                        .event("periodic-event")
                        .data("SSE - " + LocalTime.now().toString())
                        .build());
    }

    @GetMapping("/rxjava")
    Publisher<ServerSentEvent<String>> live4(HttpServletRequest request) {
//        log.debug("== Client subscribing to live events [remoteAddress=${request.remoteAddress}]")
//        try {
//            return serverSentEventsService.getEventPublisher()
//        }
//        catch (Exception e) {
//            String message = "Unexpected error while obtaining event emitter"
////            log.error("${message} | ${e.message}", e)

            return Flowable.just( Duration.ofSeconds(1))
                    .map(sequence -> ServerSentEvent.<String> builder()
                            .id(String.valueOf(sequence))
                            .event("periodic-event")
                            .data("SSE - " + LocalTime.now().toString())
                            .build());


    }
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> live2(HttpServletRequest request) {

        try {
            return Flux.create(emitter -> {
                eventPublisher.publishEvent(emitter);
                emitter.onDispose(() -> {
                    // Clean up resources if needed
                });
            });
        } catch (Exception e) {
//            String message = "Unexpected error while obtaining event emitter";
//            log.error("{} | {}", message, e.getMessage(), e);
            return Mono.just(  Duration.ofSeconds(1))
                    .map(sequence -> ServerSentEvent.<String> builder()
                            .id(String.valueOf(sequence))
                            .event("periodic-event")
                            .data("SSE - " + LocalTime.now().toString())
                            .build()).flux();
        }
    }
    @GetMapping("/events")
    public void createEvent(String eventData) {
        // 创建事件并发布
//        Event event = new Event(eventData);
//        eventPublisher.publishEvent("test");
        liveEventsService.publishWorkflowEvent(new Workflow2());
    }
}
