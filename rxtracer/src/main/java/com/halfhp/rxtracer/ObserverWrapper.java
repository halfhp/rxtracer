package com.halfhp.rxtracer;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class ObserverWrapper<T> extends TracingObserver<Observer<? super T>> implements Observer<T> {

    public ObserverWrapper(@NonNull Observer<? super T> wrapped, @NonNull StackTraceRewriter rewriter) {
        super(wrapped, rewriter);
    }

    @Override
    public void onSubscribe(Disposable d) {
        wrapped.onSubscribe(d);
    }

    @Override
    public void onNext(T t) {
        wrapped.onNext(t);
    }

    @Override
    public void onError(Throwable t) {
        wrapped.onError(rewriter.rewrite(t, this.stackTrace));
    }

    @Override
    public void onComplete() {
        wrapped.onComplete();
    }
}
