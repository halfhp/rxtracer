package com.halfhp.rxtracer;

import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.TracingCompletable;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;

public class RxTracer {

    public static void install() {
        RxJavaPlugins.setOnCompletableAssembly(new Function<Completable, Completable>() {

            @Override
            public Completable apply(Completable completable) {
                return new TracingCompletable(completable);
            }
        });
    }

    public static <T extends Throwable> T rewriteStackTrace(@NonNull T throwable, StackTraceElement[] elements) {
        throwable.setStackTrace(elements);
        return throwable;
    }


    public static class TracingObserverBase<T> {
        protected final T wrapped;
        protected final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        public TracingObserverBase(T wrapped) {
            this.wrapped = wrapped;
        }
    }
}
