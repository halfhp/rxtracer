package com.halfhp.rxtracer;

import io.reactivex.plugins.RxJavaPlugins;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RxTracerTest {

    @BeforeClass
    public static void beforeClass() {
        RxTracer.enable();
    }

    @Before
    public void before() {
        RxTracer.setMode(Mode.REWRITE);
    }

    @Test
    public void enable_enablesRxTracer() {
        assertTrue(RxJavaPlugins.getOnCompletableAssembly() instanceof AssemblyWrapper);
        assertTrue(RxJavaPlugins.getOnObservableAssembly() instanceof AssemblyWrapper);
        assertTrue(RxJavaPlugins.getOnSingleAssembly() instanceof AssemblyWrapper);
        assertTrue(RxJavaPlugins.getOnMaybeAssembly() instanceof AssemblyWrapper);
        assertTrue(RxJavaPlugins.getOnFlowableAssembly() instanceof AssemblyWrapper);
    }

    @Test
    public void enable_isIdempotent() {
        Object startingAssembly = RxJavaPlugins.getOnCompletableAssembly();
        RxTracer.enable();
        assertEquals(startingAssembly, RxJavaPlugins.getOnCompletableAssembly());
    }

    @Test
    public void disable_disablesRxTracer() {
        RxTracer.disable();
        assertNull(RxJavaPlugins.getOnCompletableAssembly());
        assertNull(RxJavaPlugins.getOnObservableAssembly());
        assertNull(RxJavaPlugins.getOnSingleAssembly());
        assertNull(RxJavaPlugins.getOnMaybeAssembly());
        assertNull(RxJavaPlugins.getOnFlowableAssembly());
    }

    @Ignore
    @Test
    public void disable_restoresWrappedAssembly() {
        // TODO
    }
}