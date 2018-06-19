package io.reactivex;

import com.halfhp.rxtracer.SingleObserverWrapper;

import com.halfhp.rxtracer.StackTraceRewriter;
import io.reactivex.annotations.NonNull;

public class TracingSingle<T> extends Single<T> {

    private final Single<T> wrapped;
    private final StackTraceRewriter rewriter;

    public TracingSingle(@NonNull Single<T> wrapped, @NonNull StackTraceRewriter rewriter) {
        this.wrapped = wrapped;
        this.rewriter = rewriter;
    }

    @Override
    protected void subscribeActual(SingleObserver<? super T> observer) {
        wrapped.subscribeActual(new SingleObserverWrapper<>(observer, rewriter));
    }

}
