# RxTracer [![Codix](https://codix.io/gh/badge/halfhp/rxtracer)](https://codix.io/gh/badge/halfhp/rxtracer) [![CircleCI](https://circleci.com/gh/halfhp/rxtracer.svg?style=shield)](https://circleci.com/gh/halfhp/rxtracer)
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
implementation 'com.halfhp.rxtracer:rxtracer:0.1.1'
```

In your project code, call the following as early as possible:

```java
RxTracer.enable();
```

NOTE: RxTracer uses assembly hooks to instrument RxJava2, and only one hook can be active at a time
per reactive type.  If your project uses other assembly hooks, ensure that RxTracer is enabled AFTER these other hooks
as RxTracer will wrap the existing hook so that it will continue to work.


## Why You Need It
Imagine you have an app containing `FooService.java` which includes a method:

```
public Observable<Stuff> doStuff(...) {
    return Observable.fromCallable(() -> { ... }
      .subscribeOn(Schedulers.newThread());
}
```

You use this method and others like it throughout your application.  Like every other app on earth yours has bugs.  
In this particular made-up case one of the consumers of `doStuff` is passing it bad data, causing an exeption.  
You've got plenty of stack traces from the issue but because these observables run asynchronously there's nothing in 
them showing which call to `doStuff(...).subscribe(...)` is  the culprit.

You're stuck either adding an error handler with unique log messages to each subscription
or praying you can reproduce locally and isolate the bug with a breakpoint.

Or you can install rxtracer.

## How It Works
Whenever a reactive type (`Observable`, `Single`, etc.) is instantiated it's wrapped with an enhanced
version of that same reactive type that captures a stack trace at the call-site of `subscribe(...)`.
If that instance encounters an error while processing its subscribe block, that exception's
stack trace is replaced with the previously captured trace.  

Only the originating exception's trace is rewritten; you still get a trace that includes higher level
exceptions that rx adds such as an `UndeliverableException` if the exception occurs on a subscription that
has no error handler.

## Is It Slow?

### The short Answer: 

If the performance overhead of using RxJava in your project didn't scare you away then the overhead of 
RxTracer shouldn't scare you either.

### The Long Answer: 

While capturing a stack trace is a relatively slow operation, a trace is captured only once per subscription and 
subscription tends to be an infrequent operation: You create an observable, you subscribe to it and you operate 
on it's stream of emissions.  There are many emissions, but only one subscription.

Use cases do exist where hundreds or more subscriptions are firing every second.  If
your project falls into that category then you'll want measure the performance impact of using rxtracer.  
For most software projects, particularly mobile apps, desktop apps and and apps not running on a server, 
the overhead is typically negligible.

These sorts of assertions are notoriously difficult to prove one way or another, but  I'll attempt to
use a Caliper microbenchmark to provide some baseline numbers:

For those interested, the source of the microbenchmark is [available here](rxtracer/src/test/java/com/halfhp/rxtracer/RxTracerBenchmark.java).

I ran the following benchmarks on a 2016 Macbook Pro with a 2.9ghz Intel i7 CPU.

The first benchmark, `measureInstanteOnly`, measures only the time taken to instantiate a new Observable.  
All the heavy lifting is done at this stage so these results paint RxTracer in
the worst possible light.

* **DISABLED:** 4.7ns per instantiation
* **ENABLED:** 13.851ns per instantiation

You'll almost never instantiate an observable without subscribing to it though, since that would be pointless.  As a 
real-world example the second benchmark  `measureInstantiatePlusSubscibe` measures the combined time of instantiation and subscription:

* **DISABLED:** 49ns per instantiate-subscribe
* **ENABLED:** 61ns per instantiate-subscribe
 
Practically nothing is happening in the body of subscribe and we're still only
looking at a overhead of about 20%. In most real-world scenarios we're very likely looking at less than 10%.  


