## [ContiPerf](http://databene.org/contiperf/)

[![Maven central](https://maven-badges.herokuapp.com/maven-central/org.databene/contiperf/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.databene/contiperf)


### Continuous Performance Testing (Junit)

In order to assure software performance, software needs to be tested accordingly as early as possible - only weaknesses diagnosed early can be assessed quickly and cheaply. ContiPerf enables performance testing already in early development phases and in an easy-to-learn manner: 

A developer writes a performance test in form of a JUnit 4 test case and adds performance test execution settings as well as performance requirements in form of Java annotations. When JUnit is invoked by an IDE, build script or build server, ContiPerf activates, performs the tests and creates an HTML report. The report provides a detailed overview of execution, requirements and measurements, even providing a latency distribution chart.

A large feature set for execution settings and performance requirements is available, e.g. Ramp up, warm up, individual pause timing, concurrent exection of test groups and more.

### Requirements

You need at least Java 5 and JUnit 4.7 to use ContiPerf

### Licence

ContiPerf is Open Source and you can choose among the following licenses:

* [Apache License 2.0](Apache_License-2.0.txt)
* [Lesser GNU Public License (LGPL) 3.0](lgpl-version3.txt)
* [Eclipse Public License 1.0](epl-v10.html)
* [BSD License](bsd-license.txt)

### Getting help / getting involved

If you are stuck, found a bug, have ideas for ContiPerf or want to help, visit the [forum](http://databene.org/forum).

### More

Check out [Wiki](https://github.com/lucaspouzac/contiperf/wiki).
