package com.halfhp.rxtracer;

import com.halfhp.rxtracer.test.ExampleService;

import org.junit.BeforeClass;

public abstract class WrapperTest {

    protected ExampleService exampleService = new ExampleService();

    @BeforeClass
    public static void beforeClass() {
        RxTracer.enable();
    }
}
