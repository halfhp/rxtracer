package com.halfhp.rxtracer;

import com.google.caliper.Benchmark;
import com.google.caliper.api.VmOptions;
import com.google.caliper.runner.CaliperMain;
import com.halfhp.rxtracer.test.ExampleService;
import com.tspoon.traceur.Traceur;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import org.junit.Ignore;

@Ignore
@VmOptions("-XX:-TieredCompilation")
public class RxTracerBenchmark {

    private final ExampleService exampleService = new ExampleService();

    /**
     * Used to temporarily store op results to prevent overly aggressive optimizations
     */
    Object optimizerPrevention;

    private void preInit(boolean isRxTracerEnabled, boolean isTraceurEnabled) {
        if(isRxTracerEnabled) {
            Traceur.disableLogging();
            RxTracer.enable();
        } else if(isTraceurEnabled) {
            RxTracer.disable();
            Traceur.enableLogging();
        } else {
            RxTracer.disable();
            Traceur.disableLogging();
        }
    }

    // INSTANTIATE_ONLY BENCHMARKS

    private void instantiateOnlyLoop(int reps) {
        for(int i = 0; i < reps; i++) {
            optimizerPrevention = exampleService.getBarObservable();
        }
    }

    @Benchmark
    void instantiateOnly_baseline(int reps) {
        preInit(false, false);
        instantiateOnlyLoop(reps);
    }

    @Benchmark
    void instantiateOnly_rxtracer(int reps) {
        preInit(true, false);
        instantiateOnlyLoop(reps);
    }

    @Benchmark
    void instantiateOnly_traceur(int reps) {
        preInit(false, true);
        instantiateOnlyLoop(reps);
    }



    // SUBSCRIBE BENCHMARKS

    private void subscribeLoop(int reps) {
        for(int i = 0; i < reps; i++) {
            exampleService.getBarObservable().subscribe(new Consumer<ExampleService.Bar>() {
                @Override
                public void accept(ExampleService.Bar foo) {
                    optimizerPrevention = foo;
                }
            });
        }
    }

    @Benchmark
    void subscribe_baseline(int reps) {
        preInit(false, false);
        subscribeLoop(reps);
    }

    @Benchmark
    void subscribe_rxtracer(int reps) {
        preInit(true, false);
        subscribeLoop(reps);
    }

    @Benchmark
    void subscribe_traceur(int reps) {
        preInit(false, true);
        subscribeLoop(reps);
    }

    // MULTI-FLATMAP BENCHMARKS

    private void multiFlatMapLoop(int reps) {
        for(int i = 0; i < reps; i++) {
            exampleService.getBarObservable()
                    .flatMap(new Function<ExampleService.Bar, ObservableSource<ExampleService.Bar>>() {

                        @Override
                        public ObservableSource<ExampleService.Bar> apply(ExampleService.Bar foo) {
                            return exampleService.getBarObservable();
                        }
                    })
                    .flatMap(new Function<ExampleService.Bar, ObservableSource<ExampleService.Bar>>() {

                        @Override
                        public ObservableSource<ExampleService.Bar> apply(ExampleService.Bar foo) {
                            return exampleService.getBarObservable();
                        }
                    })
                    .flatMap(new Function<ExampleService.Bar, ObservableSource<ExampleService.Bar>>() {

                        @Override
                        public ObservableSource<ExampleService.Bar> apply(ExampleService.Bar foo) {
                            return exampleService.getBarObservable();
                        }
                    })
                    .flatMap(new Function<ExampleService.Bar, ObservableSource<ExampleService.Bar>>() {

                        @Override
                        public ObservableSource<ExampleService.Bar> apply(ExampleService.Bar foo) {
                            return exampleService.getBarObservable();
                        }
                    })
                    .flatMap(new Function<ExampleService.Bar, ObservableSource<ExampleService.Bar>>() {

                        @Override
                        public ObservableSource<ExampleService.Bar> apply(ExampleService.Bar foo) {
                            return exampleService.getBarObservable();
                        }
                    }).subscribe(new Consumer<ExampleService.Bar>() {
                @Override
                public void accept(ExampleService.Bar foo) {
                    optimizerPrevention = foo;
                }
            });
        }
    }

    @Benchmark
    void multiFlatMap_baseline(int reps) {
        preInit(false, false);
        multiFlatMapLoop(reps);
    }

    @Benchmark
    void multiFlatMap_rxtracer(int reps) {
        preInit(true, false);
        multiFlatMapLoop(reps);
    }

    @Benchmark
    void multiFlatMap_traceur(int reps) {
        preInit(false, true);
        multiFlatMapLoop(reps);
    }

    public static void main(String[] args) {
        CaliperMain.main(RxTracerBenchmark.class, args);
    }
}
