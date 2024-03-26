package com.bioproj.trace.audit;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditEvent {
    String clientIp;
    String target;
    String status;
    String principal;
    AuditEventType type;

}
