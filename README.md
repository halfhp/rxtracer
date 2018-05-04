# RxTracer
A Utility to rewrite RxJava2 stack traces to include the original subscribe call-site.

## Usage

Gradle Dependency:
```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/halfhp/rxtracer"
    }
}
...
implementation 'com.halfhp.rxtracer:rxtracer:0.1.0'
```

Also make sure that you have JDK 1.8 compatibility enabled for your project:

```groovy
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

In your project code, enable RxTracer at the top of `Application.onCreate`:

```java
RxTracer.enable();
```

## Why You Need It
It's normal to subscribe to `Observable`, `Flowable`, etc. on a background thread.  When something 
goes wrong in these cases, the exception's stack trace only leads back to thethreadpool runner 
that invoked the subscription body.

Imagine you have dozens or maybe even hundreds of places in your code where you obtain 
an `Observable` and subscribe to it without providing an error handler because your code is 
designed to handle exceptions before this point and for unexpected exceptions you want to fail fast.

Now imagine that one of your subscriptions contains a bug.  You have multiple records of the 
offending stack trace, but no way to tell which subscription caused the exception.
You're stuck either adding an error handler with unique logging messages to each subscription
or setting breakpoints on each subscription and praying you can reproduce the problem locally.

Or you can install rxtracer.

## How It Works
The concept is simple: Whenever an RxJava2 reactive type is instantiated we wrap it with an enhanced
version of that same type that captures a stack trace at the point where you call `subscribe(...)`.
If that instance encounters an error while processing its subscribe block, that exception's
stack trace is replaced with the previously captured trace.  

Only the originating exception's trace is rewritten; you still get a trace that includes higher level
exceptions that rx adds such as an `UndeliverableException` if the exception occurs on a subscription that
has no error handler.

## Is It Slow
Yes and no.  Yes because capturing a stack trace is a relatively slow operation.  No because the stack trace
is captured only once per subscription and subscription tends to be an infrequent operation; You 
create your observable, you subscribe to it and you operate on it's stream of emissions.  
There are many emissions, but only one subscription.

Certainly use cases exist where thousands or more subscriptions are firing every second.  If
your project falls into that category then you'll want measure the performance impact of using rxtracer.  
For most projects though, particularly Android apps, Destop apps, etc. the overhead is typically negligible.



