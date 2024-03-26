package com.bioproj.trace.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class AuditEventPublisher {
    @Autowired
    ApplicationEventPublisher eventPublisher;


    public void workflowCreation(String workflowId) {
        AuditEvent auditEvent = AuditEvent.builder()
                .target(workflowId)
                .build();
//        final event = new AuditEvent(
//                type: AuditEventType.workflow_created,
//                target: workflowId,
//                clientIp: getClientAddress(),
//                principal: getPrincipal() )

        eventPublisher.publishEvent(auditEvent);
    }
}
