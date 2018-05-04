package com.halfhp.rxtracer;

import io.reactivex.TracingCompletable;
import io.reactivex.TracingFlowable;
import io.reactivex.TracingMaybe;
import io.reactivex.TracingObservable;
import io.reactivex.TracingSingle;
import io.reactivex.annotations.NonNull;
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
        RxJavaPlugins.setOnCompletableAssembly(TracingCompletable::new);
        RxJavaPlugins.setOnObservableAssembly(TracingObservable::new);
        RxJavaPlugins.setOnSingleAssembly(TracingSingle::new);
        RxJavaPlugins.setOnMaybeAssembly(TracingMaybe::new);
        RxJavaPlugins.setOnFlowableAssembly(TracingFlowable::new);

        // TODO:
        //RxJavaPlugins.setOnParallelAssembly(TracingParallelFlowable::new);
        //RxJavaPlugins.setOnConnectableFlowableAssembly(TracingConnectableFlowable::new);
        //RxJavaPlugins.setOnConnectableObservableAssembly(TracingConnectableObservable::new);
    }

    public static void disable() {
        // TODO
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
}
