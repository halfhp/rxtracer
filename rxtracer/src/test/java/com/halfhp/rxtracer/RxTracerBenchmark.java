package com.halfhp.rxtracer;

import com.google.caliper.Benchmark;
import com.google.caliper.api.VmOptions;
import com.google.caliper.runner.CaliperMain;
import org.junit.Ignore;

@Ignore
@VmOptions("-XX:-TieredCompilation")
public class RxTracerBenchmark {

    private final ExampleService exampleService = new ExampleService();

    {
        RxTracer.enable(); // comment this out to get DISABLED stats
    }

    @Benchmark
    void measureInstantiateOnly(int reps) {
        for(int i = 0; i < reps; i++) {
            exampleService.getFooObservable();
        }
    }

    @Benchmark
    void measureInstantiatePlusSubscribe(int reps) {
        for(int i = 0; i < reps; i++) {
            exampleService.getBarObservable().subscribe();
        }
    }


    public static void main(String[] args) {
        CaliperMain.main(RxTracerBenchmark.class, args);
    }
}
