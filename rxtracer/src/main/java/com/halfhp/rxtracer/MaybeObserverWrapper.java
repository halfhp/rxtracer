package com.halfhp.rxtracer;

import io.reactivex.MaybeObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class MaybeObserverWrapper<T> extends TracingObserver<MaybeObserver<? super T>> implements MaybeObserver<T> {

    public MaybeObserverWrapper(@NonNull MaybeObserver<? super T> wrapped, @NonNull StackTraceRewriter rewriter) {
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

    @Override
    public void onComplete() {
        wrapped.onComplete();
    }
}
