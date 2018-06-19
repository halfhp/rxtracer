package com.halfhp.rxtracer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StackTraceRewriterTest {

    @Test
    public void rewriteStacktrace_append_addsNewTraceToEnd() {
        final StackTraceRewriter rewriter = new StackTraceRewriter(Mode.APPEND);
        final StackTraceElement[] newTrace = new Exception().getStackTrace();
        final Exception ex1 = new Exception();
        final StackTraceElement[] rawTrace = ex1.getStackTrace();

        assertEquals(rawTrace.length + newTrace.length, rewriter.rewrite(ex1, newTrace).getStackTrace().length);

        // first element in the original trace should remain first:
        assertEquals(rawTrace[0], ex1.getStackTrace()[0]);
    }

    @Test
    public void rewriteStacktrace_prepend_addsTraeToStart() {
        final StackTraceRewriter rewriter = new StackTraceRewriter(Mode.PREPEND);
        final StackTraceElement[] newTrace = new Exception().getStackTrace();
        final Exception ex1 = new Exception();
        final StackTraceElement[] rawTrace = ex1.getStackTrace();

        assertEquals(rawTrace.length + newTrace.length, rewriter.rewrite(ex1, newTrace).getStackTrace().length);

        // first element in the new trace should now be first:
        assertEquals(newTrace[0], ex1.getStackTrace()[0]);
    }
}
