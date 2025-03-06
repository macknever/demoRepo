# Cassandra Learning Notes

These are my learning notes for Cassandra, covering key concepts and differences from SQL databases.

---

## **1. Primary Key, Partition Key, Clustering Key, and Composite Key**
### **Primary Key**
- Uniquely identifies a row in a Cassandra table.
- Consists of **at least one Partition Key** and **an optional Clustering Key**.

### **Partition Key**
- Determines **which node** stores a particular row.
- Ensures data distribution and scalability.
- Can be a **single column** or **multiple columns (composite key)**.

### **Clustering Key**
- Defines the **order of rows** inside a partition.
- Allows sorting **only within a partition**.

### **Composite Key**
- A **Primary Key with multiple columns**, where:
    - The **first part** is the **Partition Key**.
    - The **remaining part** is the **Clustering Key**.

#### **Example**:
```sql
CREATE TABLE orders (
    customer_id INT,
    order_time TIMESTAMP,
    product TEXT,
    PRIMARY KEY (customer_id, order_time)
);
```
- **Partition Key** = `customer_id` → Distributes data.
- **Clustering Key** = `order_time` → Sorts orders per customer.

---

## **2. Consistency Level, Quorum, and Cluster Relationships**
### **Consistency Level**
- Defines **how many replicas** must acknowledge a read/write operation.
- Trade-off between **strong consistency and availability**.

#### **Common Consistency Levels:**
| Level | Required Acknowledgments |
|-------|------------------------|
| **ANY** | At least 1 node (even if hinted handoff) |
| **ONE** | 1 replica |
| **QUORUM** | Majority (`⌊RF/2⌋+1`) |
| **ALL** | All replicas |

### **What is Quorum?**
- Ensures that **the majority of replicas** agree before confirming an operation.
- Formula:
  ```
  Quorum = ⌊Replication Factor (RF) / 2⌋ + 1
  ```
- Ensures **stronger consistency** compared to `ONE` but is more fault-tolerant than `ALL`.

### **Cluster, Nodes, and Data Centers**
- **Cluster**: A group of nodes storing data together. If we need duplication, 
- there has to be more than one node in the single cluster
- **Node**: A single machine(virtual or in premise) in the cluster.
- **Data Center (DC)**: A logical grouping of nodes, useful for multi-region deployments. 
Nodes in one cluster can cross different DC

#### **Example of a Multi-DC Setup**
```
Cassandra Cluster: "GlobalCluster"
-------------------------------------------------
|   Data Center 1 (USA)  |   Data Center 2 (Europe)  |
|------------------------|-------------------------|
|   Node 1  |   Node 2  |   Node 3  |   Node 4  |   Node 5  |   Node 6  |
-------------------------------------------------
```

---

## **3. Normalization vs. Denormalization in Cassandra**
### **What is Normalization?**
- Used in **SQL databases** to eliminate redundancy using multiple tables.
- Requires **JOIN operations**, which slow down performance.

### **What is Denormalization?**
- Used in **Cassandra** to improve performance.
- Stores **redundant data** in multiple places to avoid costly `JOINs`.

### **Concrete Example: Why Cassandra Outperforms SQL**
#### **SQL (Normalized) - Slower Read**
```sql
SELECT c.name, c.email, o.product, o.amount
FROM customers c
JOIN orders o ON c.customer_id = o.customer_id
WHERE c.customer_id = 101;
```
- Requires a **JOIN**, which is expensive in large datasets.
- Reads data from **multiple tables**, increasing query time.

#### **Cassandra (Denormalized) - Faster Read**
```sql
CREATE TABLE orders_by_customer (
    customer_id INT,
    order_id INT,
    name TEXT,
    email TEXT,
    product TEXT,
    amount DECIMAL,
    PRIMARY KEY (customer_id, order_id)
);
```
- Stores customer details **with each order**.
- Query execution is **O(1) + O(K)** instead of `O(log N) + O(log M) + O(K)` in SQL.

---

## **4. Transactions in SQL vs. CAS Transactions in Cassandra**
### **SQL Transactions (ACID)**
- **Atomic**: All operations succeed or fail.
- **Consistent**: Ensures database integrity.
- **Isolated**: No interference between transactions.
- **Durable**: Data is permanently saved.

#### **Example in SQL (ACID Transaction)**
```sql
BEGIN TRANSACTION;
UPDATE accounts SET balance = balance - 100 WHERE user_id = 101;
UPDATE accounts SET balance = balance + 100 WHERE user_id = 102;
COMMIT;
```
✅ **Ensures rollback if an error occurs.**

### **CAS (Compare-and-Set) in Cassandra**
- Ensures **atomic updates but only for a single partition**.
- Uses **PAXOS consensus** (slower than normal writes).
- No rollback support.

#### **Example in Cassandra (CAS Transaction)**
```sql
UPDATE accounts SET balance = balance - 100 WHERE user_id = 101 IF balance >= 100;
```
✅ **Only updates if condition is met.**

🔴 **Cassandra is not ideal for frequent transactions across multiple rows or tables.**

---

## **5. Filtering, Grouping, and Sorting in Cassandra**
### **Filtering in Cassandra**
- **Must use Partition Key** for efficient queries.
- `ALLOW FILTERING` forces a full-table scan (inefficient).

#### ✅ **Efficient Query**
```sql
SELECT * FROM users WHERE user_id = 101;
```

#### ❌ **Inefficient Query**
```sql
SELECT * FROM users WHERE city = 'New York' ALLOW FILTERING;
```

### **Grouping in Cassandra**
- **Cassandra does NOT support `GROUP BY`**.
- **Solution:** Pre-aggregate data using a counter table.

#### ✅ **Efficient Pre-Aggregation**
```sql
CREATE TABLE user_count_by_city (
    city TEXT PRIMARY KEY,
    user_count COUNTER
);
UPDATE user_count_by_city SET user_count = user_count + 1 WHERE city = 'New York';
```

### **Sorting in Cassandra**
- Sorting is **only allowed within a partition**.
- Sorting order is determined by the **Clustering Key**.

#### ✅ **Efficient Sorting (Within a Partition)**
```sql
SELECT * FROM orders_by_customer WHERE customer_id = 101 ORDER BY order_time DESC;
```
- Sorting works because `order_time` is the **Clustering Key**.

🔴 **Cassandra cannot sort across partitions.**

---

## **Final Thoughts**
- **Cassandra prioritizes fast reads & writes over complex queries.**
- **No JOINs or multi-table transactions** → Design tables as how you gonna query it.
- **Use QUORUM for balanced consistency & availability.**
- **Use pre-aggregated counters for grouping.**
- **Filter and sort only within partitions.**

🎯 **Cassandra is best for high-speed, large-scale distributed systems.** 🚀

## references

https://stackoverflow.com/questions/26757287/results-pagination-in-cassandra-cql

https://docs.datastax.com/en/developer/java-driver/2.1/manual/paging/index.html

https://github.com/jusexton/spring-cassandra-pagination-example
