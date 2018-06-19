package com.halfhp.rxtracer.test;

import io.reactivex.annotations.NonNull;
import org.junit.Ignore;

import static junit.framework.Assert.fail;

@Ignore
public class TraceChecker {
    private String errorMessage = null;

    @NonNull
    private final String expectedClassName;
    private final int expectedLineNumber;

    public TraceChecker(@NonNull Class expectedClass, int expectedLineNumber) {
        this.expectedClassName = expectedClass.getName();
        this.expectedLineNumber = expectedLineNumber;
    }

    public void check(@NonNull Throwable e) {
        e.printStackTrace();
        for (StackTraceElement element : e.getStackTrace()) {
            final String thisClassName = element.getClassName();
            if (thisClassName.equals(expectedClassName) && element.getLineNumber() == expectedLineNumber) {
                return;
            }
        }
        this.errorMessage = "Trace missing an entry for " + expectedClassName + ":" + expectedLineNumber;
    }

    public void assertValid() {
        if (errorMessage != null) {
            fail(errorMessage);
        }
    }
}
