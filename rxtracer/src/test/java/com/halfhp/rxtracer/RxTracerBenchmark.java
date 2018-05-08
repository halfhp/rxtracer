package com.halfhp.rxtracer;

import com.google.caliper.Benchmark;
import com.google.caliper.api.VmOptions;
import com.google.caliper.runner.CaliperMain;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import org.junit.Ignore;

@Ignore
@VmOptions("-XX:-TieredCompilation")
public class RxTracerBenchmark {

    private final ExampleService exampleService = new ExampleService();

    {
        //Traceur.enableLogging(); // use this to benchmark against Traceur
        RxTracer.enable(); // comment this out to get DISABLED stats
    }

    /**
     * Used to temporarily store op results to prevent overly aggressive optimizations
     */
    Object optimizerPrevention;

    @Benchmark
    void measureInstantiateOnly(int reps) {
        for(int i = 0; i < reps; i++) {
            optimizerPrevention = exampleService.getBarObservable();
        }
    }

    @Benchmark
    void measureInstantiatePlusSubscribe(int reps) {
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
    void measureLongFlatMapSubscribeChain(int reps) {
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

    public static void main(String[] args) {
        CaliperMain.main(RxTracerBenchmark.class, args);
    }
}
