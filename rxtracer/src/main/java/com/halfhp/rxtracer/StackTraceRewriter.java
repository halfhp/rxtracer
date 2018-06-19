package com.halfhp.rxtracer;

import io.reactivex.annotations.NonNull;

public class StackTraceRewriter {

    private Mode mode;

    public StackTraceRewriter(@NonNull Mode mode) {
        this.mode = mode;
    }

    public <T extends Throwable> T rewrite(@NonNull T throwable, StackTraceElement[] elements) {
        switch (mode) {
            case APPEND:
                throwable.setStackTrace(concat(throwable.getStackTrace(), elements));
                break;
            case PREPEND:
                throwable.setStackTrace(concat(elements, throwable.getStackTrace()));
                break;
            case REWRITE:
                throwable.setStackTrace(elements);
                break;
        }
        return throwable;
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(@NonNull Mode mode) {
        this.mode = mode;
    }

    private static StackTraceElement[] concat(StackTraceElement[] first, StackTraceElement[] second) {
        final StackTraceElement[] concatenatedTrace = new StackTraceElement[first.length + second.length];
        System.arraycopy(first, 0, concatenatedTrace, 0, first.length);
        System.arraycopy(second, 0, concatenatedTrace, first.length, second.length);
        return concatenatedTrace;
    }
}
