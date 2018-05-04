package com.halfhp.rxtracer;

public abstract class TracingObserver<T> {
    protected final T wrapped;

    // supposedly Throwable.getStackTrace is faster than Thread.currentThread().getStackTrace():
    protected final StackTraceElement[] stackTrace = new Throwable().getStackTrace();

    public TracingObserver(T wrapped) {
        this.wrapped = wrapped;
    }
}
