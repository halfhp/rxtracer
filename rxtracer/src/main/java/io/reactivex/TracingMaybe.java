package io.reactivex;

import com.halfhp.rxtracer.MaybeObserverWrapper;

import com.halfhp.rxtracer.StackTraceRewriter;
import io.reactivex.annotations.NonNull;

public class TracingMaybe<T> extends Maybe<T> {

    private final Maybe<T> wrapped;
    private final StackTraceRewriter rewriter;

    public TracingMaybe(@NonNull Maybe<T> wrapped, @NonNull StackTraceRewriter rewriter) {
        this.wrapped = wrapped;
        this.rewriter = rewriter;
    }

    @Override
    protected void subscribeActual(MaybeObserver<? super T> observer) {
        wrapped.subscribeActual(new MaybeObserverWrapper<>(observer, rewriter));
    }

}
