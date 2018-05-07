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
implementation 'com.halfhp.rxtracer:rxtracer:0.1.1'
```

In your project code, enable RxTracer at the top of `Application.onCreate`:

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
While capturing a stack trace is a relatively slow operation, a trace is captured only once per subscription and 
subscription tends to be an infrequent operation: You create an observable, you subscribe to it and you operate 
on it's stream of emissions.  There are many emissions, but only one subscription.

Certainly use cases exist where hundreds or more subscriptions are firing every second.  If
your project falls into that category then you'll want measure the performance impact of using rxtracer.  
For most software projects, particularly mobile apps, desktop apps and and apps not running on a server, 
the overhead is typically negligible.



