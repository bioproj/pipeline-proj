package com.bioproj.live;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.internal.util.Closure;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class BackpressureBuffer<T> {
    public  final Duration DEFAULT_TIMEOUT = Duration.parse("PT1S");
    public  final Duration DEFAULT_HEARTBEAT = Duration.parse("PT5M");
    private LinkedBlockingQueue<T> eventQueue = new LinkedBlockingQueue<>();
    public final int DEFAULT_MAX_COUNT = 100;
    private Duration heartbeat = DEFAULT_HEARTBEAT;
    private Duration timeout = DEFAULT_TIMEOUT;
    private int maxCount = DEFAULT_MAX_COUNT;
    private Closure action;
    private volatile Boolean terminated=false;
    private Thread spooler;

    private String name = "Back-pressure buffer";
    BackpressureBuffer setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    BackpressureBuffer setTimeout(Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    BackpressureBuffer setHeartbeat(Duration duration) {
        this.heartbeat = duration;
        return this;
    }

    BackpressureBuffer setMaxCount(int count) {
        this.maxCount = count;
        return this;
    }
    BackpressureBuffer onNext(  Closure action) {
        this.action = action;
        return this;
    }
    BackpressureBuffer start () {
//        spooler = Thread.startDaemon(name, this.&emitEvents0);
        spooler = new Thread(this::emitEvents0);
        spooler.setDaemon(true);
        spooler.setName(name);
        spooler.start();
        return this;
    }


    void offer(T payload) {

        eventQueue.add(payload);
    }
    void terminate() {
        terminated = true;
    }

    void await() {
        try {
            spooler.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void terminateAndAwait() {
        terminate();
        await();
    }
    protected void emitEvents0() {
        final Map<Integer,T> buffer = new HashMap<Integer,T>(maxCount);
        long previous = System.currentTimeMillis();
        final long period = timeout.toMillis();
        final long pollTimeout = period / 10 ;
        log.info("Starting backpressure buffer thread | maxSize={}; timeout={} ({}ms); heartbeat={} ({}ms); period={}ms; poolTimeout={}ms",maxCount,timeout,timeout.toMillis(),heartbeat,heartbeat.toMillis(),period,pollTimeout);


        while( !terminated ) {
            try {
                final T  event = eventQueue.poll(pollTimeout, TimeUnit.MILLISECONDS);
                // reconcile task events ie. send out only the last event
                if( event!=null ) {
                    buffer.put(event.hashCode(),event);
                    log.info("Taking event={} -- buffer={}",event,buffer);
                }

                // check if there's something to send
                final long  now = System.currentTimeMillis();
                final long delta = now -previous;

                if( buffer.size()==0 ) {
                    if( delta > heartbeat.toMillis() ) {
                        log.info("Heartbeat event!");
                        action.invoke(Collections.emptyList());
    //                    action.call(  Collections.emptyList());
                        previous = now;
                    }
                    continue;
                }

                final boolean timeoutFlag = delta > period;
                final boolean maxCountFlag = buffer.size() >= maxCount;
                if(  timeoutFlag || maxCountFlag || terminated ) {
                    log.info("${ {} ? 'Timeout' : ({} ? 'Max buffer size' : 'Terminate')} event",timeout,maxCountFlag);
                    // send
                    final List payload = new ArrayList<>((Collection)buffer.values());
                    action.invoke(payload);
                    // clean up for next iteration
                    previous = now;
                    buffer.clear();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if( !buffer.isEmpty() ) {
//            List payload = new ArrayList<>((Collection)buffer.values());
//            Object collected = payload.stream().distinct().collect(Collectors.toList());
            List payload = new ArrayList<>(new LinkedHashSet<>(buffer.values()));
            action.invoke(payload);
//            .unique();
//            buffer.values()
        }

    }

}
