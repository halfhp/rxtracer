package io.reactivex;

import com.halfhp.rxtracer.RxTracer;
import com.halfhp.rxtracer.TracingObserver;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class TracingCompletable extends Completable {

    private final Completable wrapped;

    public TracingCompletable(@NonNull Completable wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    protected void subscribeActual(CompletableObserver co) {
        wrapped.subscribeActual(new CompletableObserverWrapper(co));
    }

    private static final class CompletableObserverWrapper extends TracingObserver<CompletableObserver> implements CompletableObserver {

        CompletableObserverWrapper(@NonNull CompletableObserver wrapped) {
            super((wrapped));
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
            wrapped.onError(RxTracer.rewriteStackTrace(t, this.stackTrace));
        }
    }
}
