package com.halfhp.rxtracer;

import io.reactivex.CompletableObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class CompletableObserverWrapper extends TracingObserver<CompletableObserver> implements CompletableObserver {

    public CompletableObserverWrapper(@NonNull CompletableObserver wrapped, @NonNull StackTraceRewriter rewriter) {
        super(wrapped, rewriter);
    }

    @Override
    public void onSubscribe(Disposable d) {
        wrapped.onSubscribe(d);
    }

    @Override
    public void onComplete() {
        wrapped.onComplete();
    }

    @Override
    public void onError(Throwable t) {
        wrapped.onError(rewriter.rewrite(t, this.stackTrace));
    }
}
