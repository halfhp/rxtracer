package io.reactivex;

import com.halfhp.rxtracer.ObserverWrapper;

import com.halfhp.rxtracer.StackTraceRewriter;
import io.reactivex.annotations.NonNull;

public class TracingObservable<T> extends Observable<T> {

    private final Observable<T> wrapped;
    private final StackTraceRewriter rewriter;

    public TracingObservable(@NonNull Observable<T> wrapped, @NonNull StackTraceRewriter rewriter) {
        this.wrapped = wrapped;
        this.rewriter = rewriter;
    }

    @Override
    protected void subscribeActual(io.reactivex.Observer<? super T> observer) {
        wrapped.subscribeActual(new ObserverWrapper<>(observer, rewriter));
    }

}
