package com.bioproj.trace.audit;

enum AuditEventType {
    workflow_created,
    workflow_status_changed,
    workflow_completed,
    workflow_deleted,
    workflow_dropped,
    access_token_created,
    access_token_deleted,
    user_created,
    user_updated,
    user_deleted,
    user_sign_in
}
