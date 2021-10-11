package com.halfhp.rxtracer;

import io.reactivex.annotations.NonNull;

public abstract class TracingObserver<T> {
    protected final T wrapped;
    protected final StackTraceRewriter rewriter;

    // Throwable.getStackTrace is thought to be faster than Thread.currentThread().getStackTrace():
    protected final StackTraceElement[] stackTrace = new Throwable().getStackTrace();

    public TracingObserver(@NonNull T wrapped, @NonNull StackTraceRewriter rewriter) {
        this.wrapped = wrapped;
        this.rewriter = rewriter;
    }
}
