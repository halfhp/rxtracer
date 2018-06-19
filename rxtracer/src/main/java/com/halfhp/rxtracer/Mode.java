package com.halfhp.rxtracer;

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
     * Append the the stack trace recorded when subscribe was invoked to the stack trace of the
     * exception generated when the subscribe callback ran
     */
    APPEND,

    /**
     * Experimental - prepend the the stack trace recorded when subscribe was invoked to the stack trace of the
     * exception generated when the subscribe callback ran.  This approach can sometimes produce
     * better stack trace aggregation in bug trackers like Crashalytics etc.
     */
    PREPEND
}
