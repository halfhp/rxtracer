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

Make sure JDK 1.8 compatibility is enabled as well:

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
Imagine you have an app containing `FooService.java` which includes a method:

```
public Observable<Stuff> doStuff(...) {
    return Observable.fromCallable(() -> { ... }
      .subscribeOn(Schedulers.newThread());
}
```

You use this method and others like it throughout your application in hundreds of places.  Unfortunately, like every
other piece of software on earth your application has bugs.  In this particular made-up case one of the consumers
of `doStuff` is passing it bad data, causing an exeption.  You've got plenty of stack traces from the issue
but because these observables run asynchronously there's nothing in them showing which call to `doStuff(...).subscribe(...)` is  the culprit.

You're stuck either adding an error handler with unique logging messages to each subscription
or setting breakpoints and praying you can reproduce locally.

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



