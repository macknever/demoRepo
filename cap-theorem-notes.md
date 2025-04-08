
# CAP Theorem – Notes

---

### 1. What is the CAP Theorem?

**Q:** What does the CAP theorem state?

**A:** The CAP theorem states that in any **distributed system**, it is _impossible_ to guarantee all three of the following:
- **Consistency (C):** Every read receives the most recent write or an error.
- **Availability (A):** Every request receives a (non-error) response, but it can not guarantee to be the latest state
- **Partition Tolerance (P):** The system continues to operate despite arbitrary message loss or failure of part of the network. Always is MUST

Distributed systems must choose **at most two** of these properties during a network partition.

---

### 2. What are examples of CP and AP systems?

| CAP Type | Description | Examples     |
|----------|-------------|--------------|
| **CP**   | Prioritizes consistency and partition tolerance; may sacrifice availability. | Finance      |
| **AP**   | Prioritizes availability and partition tolerance; may allow eventual consistency. | Social media |

---

### 3. For different database combinations, what are their features and which CAP combination fits best?

| Database     | Default CAP Focus | Key Features | Suitable For |
|--------------|----------------|--------------|--------------|
| **Redis**    | AP | In-memory, fast access | Caching, pub/sub, temporary data, session stores |
| **Aerospike** | AP | Low-latency reads/writes, eventual consistency, high availability | Ad tech, recommendation engines |
| **Cassandra** | AP | Eventual consistency with tunable quorum settings |  high-write throughput |

> **Use AP when:** system must stay available (even if data is slightly stale), e.g., cache, ads, analytics  
> **Use CP when:** correctness matters more than uptime during network issues, e.g., banking, user balances

---

### 4. Why can only two out of three be achieved?

**Q:** Why can't a distributed system guarantee Consistency, Availability, and Partition Tolerance at the same time?



---

## References

- [Redis Cluster and CAP](https://redis.io/docs/management/scaling/)
- [Aerospike: Consistency Models](https://docs.aerospike.com/server/architecture/consistency)
- [CAP Theorem in Real-World Scenarios](https://dev.to/codemasheen/cap-theorem-in-real-world-scenarios-559f)
