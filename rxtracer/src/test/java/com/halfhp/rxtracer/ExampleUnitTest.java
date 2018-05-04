package com.halfhp.rxtracer;

import org.junit.Test;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    /**
     * Not a practical test to run automatically, however for testing purposes, if the stack trace
     * contains the exact line number of the subscribe call, it means things are working.
     */
    @Test
    public void foo() throws Exception {
        RxTracer.install();
        Completable.fromAction(() -> {
            throw new RuntimeException("Bla!");
        }).observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(() -> {
                });

        Thread.sleep(1000);
    }
}