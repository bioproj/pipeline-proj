package com.bioproj.domain.enums;

import java.util.Arrays;
import java.util.List;

public enum TaskStatus {
    NEW,        // just created
    SUBMITTED,  // submitted to scheduler, pending execution
    RUNNING,    // task execution started
    CACHED,     // task cached
    COMPLETED,  // completed successfully
    FAILED,     // completed with error
    ABORTED;    // execution aborted


    private static final List<TaskStatus> TERMINAL = Arrays.asList(COMPLETED, FAILED, ABORTED, CACHED);

    public boolean isTerminal() {
        return TERMINAL.contains(this);
    }
}
