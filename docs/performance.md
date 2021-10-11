# Performance
RxTracer uses preemptive stack traces to provide call-site details when an exception occurs. 

While capturing a stack trace is a relatively slow operation, a trace is captured only once per subscription and 
subscription tends to be an infrequent operation: You create an observable, you subscribe to it and you operate 
on it's stream of emissions.  There are many emissions, but only one subscription*.

_\* It has been pointed out that operations such as `flatMap` result in additional subscriptions. This is true but
point about the ratio between subscriptions and emissions still holds. I've added a benchmark below to 
capture this scenario._ 

Use cases do exist where hundreds or more subscriptions are firing every second.  If
your project falls into that category then you'll want measure the performance impact of using rxtracer.  
For most software projects, particularly mobile apps, desktop apps and and apps not running on a server, 
the overhead is typically negligible.

These sorts of assertions are notoriously difficult to prove one way or another, but  I'll attempt to
use a Caliper microbenchmark to provide some baseline numbers:

For those interested, the source of the microbenchmark is [available here](rxtracer/src/test/java/com/halfhp/rxtracer/RxTracerBenchmark.java).

I ran the following benchmarks on a 2016 Macbook Pro with a 2.9ghz Intel i7 CPU.

The first benchmark, `measureInstantiateOnly`, measures only the time taken to instantiate a new Observable.  
Most of the heavy lifting is done at this stage so these results paint RxTracer in
the worst possible light.

* **DISABLED:** 7.8ns per instantiation
* **ENABLED:** 14.2ns per instantiation

You'll almost never instantiate an observable without subscribing to it though, since that would be pointless.  As a 
real-world example the second benchmark  `measureInstantiatePlusSubscibe` measures the combined time of instantiation and subscription:

* **DISABLED:** 53ns per instantiate-subscribe
* **ENABLED:** 62ns per instantiate-subscribe
 
Practically nothing is happening in the body of subscribe and we're still only
looking at a overhead of about 17%. In most real-world scenarios we're very likely looking at less than 10%.

The final benchmark `measureLongFlatMapSubscribeChain` instantiates an observable, runs it through five successive
invocations of `flatMap` and then subscribes to the result.

* **DISABLED** 295.38μs per instantiate-flatmap-5x-susbscribe
* **ENABLED** 313.75μs per instantiate-flatmap-5x-susbscribe

While both benchmarks shoot up into the microseconds range (possibly due to me misusing Caliper), the measured overhead 
of RxTracer is roughly 6%.  Five successive `flatMap` invocations might be a tad high to be representative of an average case, 
but keep in mind that each invocation is doing the bare minimum of work. 

## RxTracer vs Traceur
The [benchmarks](rxtracer/src/test/java/com/halfhp/rxtracer/RxTracerBenchmark.java) include a Traceur profile for each of the above tests.
While the benchmarks show RxTracer is about 15% faster than Traceur on average.