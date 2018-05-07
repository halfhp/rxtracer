package com.halfhp.rxtracer;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.TracingCompletable;
import io.reactivex.TracingFlowable;
import io.reactivex.TracingMaybe;
import io.reactivex.TracingObservable;
import io.reactivex.TracingSingle;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;

public class RxTracer {

    private static Mode mode = Mode.APPEND;

    /**
     *
     */
    public enum Mode {
        /**
         * The default mode.  Completely replaces the stack trace of the initial exception thrown
         * from within a subscribe callback with the stack trace that was recorded when subscribe was originally invoked.
         * This is typically the most appropriate and expected approach.
         */
        REWRITE,

        /**
         * Experimental - appends the the stack trace recorded when subscribe was invoked to the stack trace of the
         * exception generated when the subscribe callback ran.  This approach can sometimes produce
         * better stack trace aggregation in bug trackers like Crashalytics etc.
         */
        APPEND
    }

    public static void setMode(@NonNull Mode mode) {
        RxTracer.mode = mode;
    }

    public static void enable() {
        RxJavaPlugins.setOnCompletableAssembly(wrapAssembly(new Function<Completable, Completable>() {
            @Override
            public Completable apply(Completable wrapped) {
                return new TracingCompletable(wrapped);
            }
        }, RxJavaPlugins.getOnCompletableAssembly()));


        RxJavaPlugins.setOnObservableAssembly(wrapAssembly(new Function<Observable, Observable>() {
            @Override
            public Observable apply(Observable wrapped) {
                return new TracingObservable<Object>(wrapped);
            }
        }, RxJavaPlugins.getOnObservableAssembly()));


        RxJavaPlugins.setOnSingleAssembly(wrapAssembly(new Function<Single, Single>() {
            @Override
            public Single apply(Single wrapped) {
                return new TracingSingle<Object>(wrapped);
            }
        }, RxJavaPlugins.getOnSingleAssembly()));


        RxJavaPlugins.setOnMaybeAssembly(wrapAssembly(new Function<Maybe, Maybe>() {
            @Override
            public Maybe apply(Maybe wrapped) {
                return new TracingMaybe<Object>(wrapped);
            }
        }, RxJavaPlugins.getOnMaybeAssembly()));

        RxJavaPlugins.setOnFlowableAssembly(wrapAssembly(new Function<Flowable, Flowable>() {
            @Override
            public Flowable apply(Flowable wrapped) {
                return new TracingFlowable<Object>(wrapped);
            }
        }, RxJavaPlugins.getOnFlowableAssembly()));
    }

    public static void disable() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static <T extends Throwable> T rewriteStackTrace(@NonNull T throwable, StackTraceElement[] elements) {
        switch(mode) {
            case APPEND:
                throwable.setStackTrace(elements);
                break;
            case REWRITE:
                final StackTraceElement[] originalTrace = throwable.getStackTrace();
                final StackTraceElement[] concatenatedTrace = new StackTraceElement[originalTrace.length + elements.length];
                System.arraycopy(concatenatedTrace, 0, concatenatedTrace, 0, concatenatedTrace.length);
                System.arraycopy(elements, 0, concatenatedTrace, originalTrace.length, elements.length);
                throwable.setStackTrace(concatenatedTrace);
                break;
        }
        return throwable;
    }

    /**
     * Wraps a preexisting assembly if present, so that it it can remain in the assembly pipeline
     * along with rxtracer.
     * @param tracer
     * @param originalAssembly
     * @param <T>
     * @return
     */
    private static <T> Function<? super T, ? extends T> wrapAssembly(
            @NonNull final Function<? super T, ? extends T> tracer,
            @Nullable final Function<? super T, ? extends T> originalAssembly) {
        return new Function<T, T>() {
            @Override
            public T apply(T t) throws Exception {
                if (originalAssembly != null) {
                    return originalAssembly.apply(tracer.apply(t));
                } else {
                    return tracer.apply(t);
                }
            }
        };
    }
}
