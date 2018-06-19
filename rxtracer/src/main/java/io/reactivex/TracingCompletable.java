package io.reactivex;

import com.halfhp.rxtracer.CompletableObserverWrapper;

import com.halfhp.rxtracer.StackTraceRewriter;
import io.reactivex.annotations.NonNull;

public class TracingCompletable extends Completable {

    private final Completable wrapped;
    private final StackTraceRewriter rewriter;

    public TracingCompletable(@NonNull Completable wrapped, @NonNull StackTraceRewriter rewriter) {
        this.wrapped = wrapped;
        this.rewriter = rewriter;
    }

    @Override
    protected void subscribeActual(CompletableObserver co) {
        wrapped.subscribeActual(new CompletableObserverWrapper(co, rewriter));
    }

}
