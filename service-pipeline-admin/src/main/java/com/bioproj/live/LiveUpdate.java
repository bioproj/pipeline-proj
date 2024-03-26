package com.bioproj.live;

import com.bioproj.domain.enums.WorkflowStatus;
import lombok.Data;

@Data
public class LiveUpdate {
    private String submitId;
    private String message;
    private WorkflowStatus taskStatus;
    private String eventType;


    public LiveUpdate(String message) {
        this.message = message;
    }
    public LiveUpdate() {
    }
}
