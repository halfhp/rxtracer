package com.halfhp.rxtracer;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Function;

/**
 * Wraps an existing rx assembly so that it can be invoked after rxtracer's own assembly.
 * @param <T>
 */
class AssemblyWrapper<T> implements Function<T, T> {
    private final Function<? super T, ? extends T> wrappedAssembly;
    private final Function<? super T, ? extends T> tracer;

    private AssemblyWrapper(
            @NonNull Function<? super T, ? extends T> tracer,
            @Nullable Function<? super T, ? extends T> wrappedAssembly) {
        this.tracer = tracer;
        this.wrappedAssembly = wrappedAssembly;
    }

    @Nullable
    Function<? super T, ? extends T> getWrappedAssembly() {
        return wrappedAssembly;
    }

    @Override
    public T apply(T t) throws Exception {
        if (wrappedAssembly != null) {
            return wrappedAssembly.apply(tracer.apply(t));
        } else {
            return tracer.apply(t);
        }
    }

    static <T> Function<? super T, ? extends T> wrap(
            @NonNull Function<? super T, ? extends T> tracer,
            @Nullable Function<? super T, ? extends T> wrappedAssembly) {
        return new AssemblyWrapper<>(tracer, wrappedAssembly);
    }
}
