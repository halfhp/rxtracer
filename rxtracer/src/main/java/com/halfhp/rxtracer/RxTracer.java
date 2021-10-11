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
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;

import static io.reactivex.plugins.RxJavaPlugins.getOnCompletableAssembly;

public class RxTracer {

    private StackTraceRewriter rewriter = new StackTraceRewriter(Mode.REWRITE);
    private static RxTracer instance = new RxTracer();

    public static void setMode(@NonNull Mode mode) {
        instance.getRewriter().setMode(mode);
    }

    public static RxTracer getInstance() {
        return RxTracer.instance;
    }

    /**
     * May be used to provide a customized tracer implementation.  Take care to set your custom instance
     * BEFORE invoking {@link #enable()}.
     * @param instance
     */
    public static void setInstance(@NonNull RxTracer instance) {
        RxTracer.instance = instance;
    }

    /**
     * Enables tracing, preventing redundant assembly wrappers if already enabled.
     */
    public static void enable() {
        if(!(RxJavaPlugins.getOnCompletableAssembly() instanceof AssemblyWrapper)) {
            RxJavaPlugins.setOnCompletableAssembly(instance.newOnCompletableAssembly());
        }

        if(!(RxJavaPlugins.getOnObservableAssembly() instanceof AssemblyWrapper)) {
            RxJavaPlugins.setOnObservableAssembly(instance.newOnObservableAssembly());
        }

        if(!(RxJavaPlugins.getOnSingleAssembly() instanceof AssemblyWrapper)) {
            RxJavaPlugins.setOnSingleAssembly(instance.newOnSingleAssembly());
        }

        if(!(RxJavaPlugins.getOnMaybeAssembly() instanceof AssemblyWrapper)) {
            RxJavaPlugins.setOnMaybeAssembly(instance.newOnMaybeAssembly());
        }

        if(!(RxJavaPlugins.getOnFlowableAssembly() instanceof AssemblyWrapper)) {
            RxJavaPlugins.setOnFlowableAssembly(instance.newOnFlowableAssembly());
        }
    }

    protected Function<? super Completable, ? extends Completable> newOnCompletableAssembly() {
        return AssemblyWrapper.wrap(new Function<Completable, Completable>() {
            @Override
            public Completable apply(Completable wrapped) {
                return new TracingCompletable(wrapped, instance.getRewriter());
            }
        }, getOnCompletableAssembly());
    }

    protected Function<? super Observable, ? extends Observable> newOnObservableAssembly() {
        return AssemblyWrapper.wrap(new Function<Observable, Observable>() {
            @Override
            public Observable apply(Observable wrapped) {
                return new TracingObservable<Object>(wrapped, instance.getRewriter());
            }
        }, RxJavaPlugins.getOnObservableAssembly());
    }

    protected Function<? super Single, ? extends Single> newOnSingleAssembly() {
        return AssemblyWrapper.wrap(new Function<Single, Single>() {
            @Override
            public Single apply(Single wrapped) {
                return new TracingSingle<Object>(wrapped, instance.getRewriter());
            }
        }, RxJavaPlugins.getOnSingleAssembly());
    }

    protected Function<? super Maybe, ? extends Maybe> newOnMaybeAssembly() {
        return AssemblyWrapper.wrap(new Function<Maybe, Maybe>() {
            @Override
            public Maybe apply(Maybe wrapped) {
                return new TracingMaybe<Object>(wrapped, instance.getRewriter());
            }
        }, RxJavaPlugins.getOnMaybeAssembly());
    }

    protected Function<? super Flowable, ? extends Flowable> newOnFlowableAssembly() {
        return AssemblyWrapper.wrap(new Function<Flowable, Flowable>() {
            @Override
            public Flowable apply(Flowable wrapped) {
                return new TracingFlowable<Object>(wrapped, instance.getRewriter());
            }
        }, RxJavaPlugins.getOnFlowableAssembly());
    }

    protected StackTraceRewriter getRewriter() {
        return this.rewriter;
    }

    /**
     * Disables RxTracer, restoring previously installed assemblies, if any.
     */
    public static void disable() {
        final Function<? super Completable, ? extends Completable> completableWrapper = RxJavaPlugins.getOnCompletableAssembly();
        RxJavaPlugins.setOnCompletableAssembly(
                completableWrapper instanceof AssemblyWrapper ?
                        ((AssemblyWrapper) completableWrapper).getWrappedAssembly() : null);

        final Function<? super Observable, ? extends Observable> observableWrapper = RxJavaPlugins.getOnObservableAssembly();
        RxJavaPlugins.setOnObservableAssembly(
                observableWrapper instanceof AssemblyWrapper ?
                        ((AssemblyWrapper) observableWrapper).getWrappedAssembly() : null);

        final Function<? super Single, ? extends Single> singleWrapper = RxJavaPlugins.getOnSingleAssembly();
        RxJavaPlugins.setOnSingleAssembly(
                singleWrapper instanceof AssemblyWrapper ?
                        ((AssemblyWrapper) singleWrapper).getWrappedAssembly() : null);

        final Function<? super Maybe, ? extends Maybe> maybeWrapper = RxJavaPlugins.getOnMaybeAssembly();
        RxJavaPlugins.setOnMaybeAssembly(
                maybeWrapper instanceof AssemblyWrapper ?
                        ((AssemblyWrapper) maybeWrapper).getWrappedAssembly() : null);

        final Function<? super Flowable, ? extends Flowable> flowableWrapper = RxJavaPlugins.getOnFlowableAssembly();
        RxJavaPlugins.setOnFlowableAssembly(
                flowableWrapper instanceof AssemblyWrapper ?
                        ((AssemblyWrapper) flowableWrapper).getWrappedAssembly() : null);
    }

}
