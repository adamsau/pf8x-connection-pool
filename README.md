# Pf8x-connection-pool
a hyper fast connection pool for high frequency transaction in Java!

This connection pool provides super performance on low latency, high frequency transaction and also maximize throughput!

It completely beats HikariCP where CAS fails to deliver under high contention, and also beats HikariCP under normal situation.

It use fine grained synchronised methods to minimize overhead and provides certain fairness

so that 99.99 percentile of connection served is under certain time constraints!

Here are the benchmark results comparing Pf8x-connection-pool, HikariCP
