package com.halfhp.rxtracer;

import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class SingleObserverWrapper<T> extends TracingObserver<SingleObserver<? super T>> implements SingleObserver<T> {

    public SingleObserverWrapper(@NonNull SingleObserver<? super T> wrapped, @NonNull StackTraceRewriter rewriter) {
        super(wrapped, rewriter);
    }

    @Override
    public void onSubscribe(Disposable d) {
        wrapped.onSubscribe(d);
    }

    @Override
    public void onSuccess(T t) {
        wrapped.onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        wrapped.onError(rewriter.rewrite(e, this.stackTrace));
    }
}
