# RxTracer [![Codix](https://codix.io/gh/badge/halfhp/rxtracer)](https://codix.io/gh/repo/halfhp/rxtracer) [![CircleCI](https://circleci.com/gh/halfhp/rxtracer.svg?style=shield)](https://circleci.com/gh/halfhp/rxtracer)
A [faster](docs/performance.md) way to rewrite RxJava2 stack traces to include the original subscribe call-site.

## Usage

Gradle Dependency:
```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/halfhp/rxtracer"
    }
}
...
implementation 'com.halfhp.rxtracer:rxtracer:0.1.4'
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
If the overhead of using RxJava in your project didn't scare you away then the overhead of 
RxTracer shouldn't scare you either.  For a longer answer completely with some benchmarks 
check out the [performance notes](docs/performance.md).

