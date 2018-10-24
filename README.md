# Pf8x-connection-pool
a hyper fast connection pool for high frequency transaction in Java!

This connection pool provides super performance on low latency, high frequency transaction and also maximize throughput!

It completely beats HikariCP by over **80%+** where CAS fails to deliver under high contention, and also beats HikariCP under normal situation!

It use fine grained synchronised methods to minimize overhead and provides certain fairness

so that 99.99 percentile of connection served is under certain time constraints!

Here are the benchmark results comparing Pf8x-connection-pool, HikariCP

```java
# JMH version: 1.21
# VM version: JDK 1.8.0_181, OpenJDK 64-Bit Server VM, 25.181-b13
# VM invoker: /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java
# VM options: -javaagent:/snap/intellij-idea-community/91/lib/idea_rt.jar=35263:/snap/intellij-idea-community/91/bin -Dfile.encoding=UTF-8
# Warmup: 1 iterations, 10 s each
# Measurement: 1 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 32 threads, will synchronize iterations
# Benchmark mode: Sampling time
# Benchmark: com.pf8x.cpbench.CPBench.benchStatementCycle
# Parameters: (poolSize = 10, type = pf8x-connection-pool)
# Parameters: (poolSize = 10, type = tomcat-jdbc)
# Parameters: (poolSize = 10, type = HikariCP)

Benchmark                                                (poolSize)                (type)    Mode     Cnt       Score    Error  Units
CPBench.benchStatementCycle                                      10  pf8x-connection-pool  sample  232225    1385.812 ± 21.811  us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.00            10  pf8x-connection-pool  sample              89.472           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.50            10  pf8x-connection-pool  sample             279.552           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.90            10  pf8x-connection-pool  sample            3846.144           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.95            10  pf8x-connection-pool  sample            8345.190           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.99            10  pf8x-connection-pool  sample           15269.888           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.999           10  pf8x-connection-pool  sample           26978.615           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.9999          10  pf8x-connection-pool  sample           47287.815           us/op
CPBench.benchStatementCycle:benchStatementCycle·p1.00            10  pf8x-connection-pool  sample           88342.528           us/op
CPBench.benchStatementCycle                                      10           tomcat-jdbc  sample  162624    1966.091 ±  6.322  us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.00            10           tomcat-jdbc  sample             838.656           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.50            10           tomcat-jdbc  sample            1802.240           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.90            10           tomcat-jdbc  sample            2719.744           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.95            10           tomcat-jdbc  sample            3178.496           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.99            10           tomcat-jdbc  sample            4505.600           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.999           10           tomcat-jdbc  sample            9502.720           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.9999          10           tomcat-jdbc  sample           18398.413           us/op
CPBench.benchStatementCycle:benchStatementCycle·p1.00            10           tomcat-jdbc  sample           35389.440           us/op
CPBench.benchStatementCycle                                      10              HikariCP  sample  128411    2493.776 ± 99.190  us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.00            10              HikariCP  sample             190.464           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.50            10              HikariCP  sample             529.408           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.90            10              HikariCP  sample            1671.168           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.95            10              HikariCP  sample            4915.200           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.99            10              HikariCP  sample           64487.424           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.999           10              HikariCP  sample          120401.166           us/op
CPBench.benchStatementCycle:benchStatementCycle·p0.9999          10              HikariCP  sample          174355.015           us/op
CPBench.benchStatementCycle:benchStatementCycle·p1.00            10              HikariCP  sample          224657.408           us/op
```
