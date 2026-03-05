package com.conceptcoding.myprjects;


// Very good 👏
//This is a strong answer already. You’re clearly thinking in terms of **Object Pool + Singleton + Thread Safety**.
//
//Now let’s refine it to a **6-year, system-design-ready answer** and connect it explicitly to design patterns (because interviewers care about naming patterns properly).
//
//---
//
//# ✅ What Pattern(s) Should Be Used?
//
//This problem actually combines **multiple patterns**:
//
//## 1️⃣ Object Pool Pattern (Primary)
//
//Because:
//
//* DB connection is expensive
//* Reuse instead of recreating
//* Maintain free + in-use objects
//* Limit max count
//
//Your implementation correctly follows Object Pool.
//
//---
//
//## 2️⃣ Singleton (Pool Manager)
//
//You used:
//
//```java
//private static DBConnectionPoolManager instance;
//```
//
//This ensures:
//
//* Single pool across application
//* Centralized resource management
//
//Correct usage 👍
//
//---
//
//## 3️⃣ Proxy (Very Important — You Missed This)
//
//Requirement says:
//
//> Control access to real connection
//> Add additional functionality (logging, pooling)
//
//This is where **Proxy Pattern** comes in.
//
//Right now:
//
//* You return the real `DBConnection`
//* User might call `close()`
//* That breaks pooling
//
//❌ That’s dangerous.
//
//---
//
//# 🔥 Proper Design Should Use Proxy
//
//Instead of returning real `DBConnection`,
//return a **PooledConnectionProxy**.
//
//Example:
//
//```java
//class PooledConnection implements DBConnection {
//
//    private DBConnection realConnection;
//    private DBConnectionPoolManager pool;
//
//    public PooledConnection(DBConnection realConnection,
//                            DBConnectionPoolManager pool) {
//        this.realConnection = realConnection;
//        this.pool = pool;
//    }
//
//    @Override
//    public void close() {
//        pool.releaseDBConnection(realConnection);
//    }
//
//    // delegate all other methods
//}
//```
//
//Now:
//
//* When user calls `close()`
//* It returns to pool instead of closing DB connection
//
//This is a **Protection Proxy**.
//
//Senior-level improvement.
//
//---
//
//# ⚠️ Improvements Needed in Your Code
//
//## 1️⃣ Returning null when pool full
//
//Instead of:
//
//```java
//return null;
//```
//
//Better:
//
//* Block until connection available
//* Or throw exception
//* Or use `wait()` / `notify()`
//
//Production pools (like HikariCP) block.
//
//---
//
//## 2️⃣ Use Queue Instead of List
//
//Better:
//
//```java
//Queue<DBConnection> freeConnections = new LinkedList<>();
//```
//
//Cleaner semantics.
//
//---
//
//## 3️⃣ Avoid synchronized Entire Method
//
//Better design:
//
//* Use `ReentrantLock`
//* Or `BlockingQueue`
//* Or `Semaphore` to control max connections
//
//At 6 years, mention this improvement.
//
//---
//
//# 🎯 Final Interview Answer (Polished)
//
//You say:
//
//> I would use the Object Pool pattern to manage reusable database connections.
//> The pool manager can be implemented as a Singleton to ensure centralized management.
//> To control access and prevent clients from directly closing real connections, I would use a Proxy that intercepts the close() call and returns the connection to the pool instead of actually closing it.
//>
//> The pool would lazily create connections up to a configurable maximum limit and reuse released connections.
//
//That is a perfect answer.
//
//---
//
//# 🧠 Pattern Breakdown
//
//| Requirement            | Pattern             |
//| ---------------------- | ------------------- |
//| Expensive object reuse | Object Pool         |
//| Single pool manager    | Singleton           |
//| Control access         | Proxy               |
//| Lazy creation          | Lazy Initialization |
//| Add logging            | Proxy               |
//
//---
//
//# 🔥 Even More Senior Insight
//
//Real-world libraries:
//
//* HikariCP
//* Apache DBCP
//* C3P0
//
//All internally use:
//
//* Pool
//* Proxy-wrapped connections
//* Blocking mechanisms
//* Timeout handling
//* Health checks
//
//If you mention that → very strong.
//
//---
//
//# 📌 One More Thing
//
//Your design is good conceptually.
//
//But at 6 years, interviewer expects you to say:
//
//> I would not build this manually in production. I would use a battle-tested pool like HikariCP.
//
//That shows maturity.
//
//---
//
//If you want next:
//
//* I can redesign your pool using modern concurrency (BlockingQueue)
//* Or simulate a follow-up where interviewer asks about deadlocks and connection leaks
//* Or compare Object Pool vs Flyweight
//
//Your thinking level is solid mid-senior.

// Excellent 👏 this is now proper senior-level discussion.
//
//You want:
//
//* ✅ Object Pool
//* ✅ Lazy creation
//* ✅ Max pool size control
//* ✅ ReentrantLock (not synchronized)
//* ✅ No artificial bucket split
//* ✅ Proxy wrapping real connection
//* ✅ Safe close() behavior
//
//We’ll design this properly like a production-grade simplified pool.
//
//---
//
//# ✅ Design Approach (Clean & Correct)
//
//We will use:
//
//* `ReentrantLock` for mutual exclusion
//* `Condition` to wait when pool is full
//* Single `Deque` for available connections
//* Counter for total created connections
//* Proxy connection that intercepts `close()`
//
//No dividing MAX_POOL into buckets.
//
//---
//
//# 🔷 Step 1: DBConnection Interface
//
//```java
//public interface DBConnection {
//    void execute(String query);
//    void close();
//}
//```
//
//---
//
//# 🔷 Step 2: Real DB Connection (Expensive Object)
//
//```java
//public class RealDBConnection implements DBConnection {
//
//    public RealDBConnection() {
//        System.out.println("Creating expensive DB connection...");
//    }
//
//    @Override
//    public void execute(String query) {
//        System.out.println("Executing: " + query);
//    }
//
//    @Override
//    public void close() {
//        System.out.println("Real DB connection closed");
//    }
//}
//```
//
//---
//
//# 🔷 Step 3: Proxy Connection (VERY IMPORTANT)
//
//This prevents real close.
//
//```java
//public class PooledConnectionProxy implements DBConnection {
//
//    private final RealDBConnection realConnection;
//    private final DBConnectionPool pool;
//    private boolean isClosed = false;
//
//    public PooledConnectionProxy(RealDBConnection realConnection,
//                                  DBConnectionPool pool) {
//        this.realConnection = realConnection;
//        this.pool = pool;
//    }
//
//    @Override
//    public void execute(String query) {
//        if (isClosed) {
//            throw new IllegalStateException("Connection already closed");
//        }
//        realConnection.execute(query);
//    }
//
//    @Override
//    public void close() {
//        if (!isClosed) {
//            isClosed = true;
//            pool.releaseConnection(realConnection);
//        }
//    }
//}
//```
//
//Notice:
//
//* User calls close()
//* It returns to pool
//* Real connection is NOT destroyed
//
//This is **Protection Proxy**.
//
//---
//
//# 🔷 Step 4: DBConnectionPool Using ReentrantLock
//
//```java
//import java.util.ArrayDeque;
//import java.util.Deque;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.ReentrantLock;
//
//public class DBConnectionPool {
//
//    private final int MAX_POOL_SIZE;
//    private int totalConnections = 0;
//
//    private final Deque<RealDBConnection> availableConnections = new ArrayDeque<>();
//
//    private final ReentrantLock lock = new ReentrantLock();
//    private final Condition connectionAvailable = lock.newCondition();
//
//    public DBConnectionPool(int maxPoolSize) {
//        this.MAX_POOL_SIZE = maxPoolSize;
//    }
//
//    public DBConnection getConnection() throws InterruptedException {
//        lock.lock();
//        try {
//
//            // If available connection exists → reuse
//            if (!availableConnections.isEmpty()) {
//                RealDBConnection conn = availableConnections.pop();
//                return new PooledConnectionProxy(conn, this);
//            }
//
//            // If we can create new → lazy creation
//            if (totalConnections < MAX_POOL_SIZE) {
//                RealDBConnection conn = new RealDBConnection();
//                totalConnections++;
//                return new PooledConnectionProxy(conn, this);
//            }
//
//            // Pool full → wait
//            while (availableConnections.isEmpty()) {
//                connectionAvailable.await();
//            }
//
//            RealDBConnection conn = availableConnections.pop();
//            return new PooledConnectionProxy(conn, this);
//
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    protected void releaseConnection(RealDBConnection connection) {
//        lock.lock();
//        try {
//            availableConnections.push(connection);
//            connectionAvailable.signal();  // Wake one waiting thread
//        } finally {
//            lock.unlock();
//        }
//    }
//}
//```
//
//---
//
//# 🔥 What This Achieves
//
//### ✅ Lazy creation
//
//Connection created only if needed.
//
//### ✅ Max pool size respected
//
//`totalConnections < MAX_POOL_SIZE`
//
//### ✅ Blocking when full
//
//Thread waits using `Condition`.
//
//### ✅ No bucket splitting
//
//Single queue + counter.
//
//### ✅ Proxy ensures safe return
//
//User cannot kill real connection.
//
//---
//
//# 🔷 Example Usage
//
//```java
//public class Main {
//    public static void main(String[] args) throws Exception {
//
//        DBConnectionPool pool = new DBConnectionPool(2);
//
//        DBConnection conn1 = pool.getConnection();
//        DBConnection conn2 = pool.getConnection();
//
//        conn1.execute("SELECT * FROM users");
//
//        // This will block if called in separate thread
//        // DBConnection conn3 = pool.getConnection();
//
//        conn1.close();  // returns to pool
//
//        DBConnection conn3 = pool.getConnection(); // reused
//
//        conn3.execute("SELECT * FROM orders");
//    }
//}
//```
//
//---
//
//# 🧠 Why ReentrantLock Instead of synchronized?
//
//Advantages:
//
//* Finer control
//* tryLock()
//* Multiple Conditions
//* Better scalability
//* Avoids intrinsic lock limitations
//
//At 6 years, mentioning this is good.
//
//---
//
//# 🎯 Pattern Summary in This Code
//
//| Part                            | Pattern                  |
//| ------------------------------- | ------------------------ |
//| Pool manager                    | Object Pool              |
//| Single pool instance (optional) | Singleton                |
//| Proxy wrapper                   | Proxy (Protection Proxy) |
//| Lazy creation                   | Lazy Initialization      |
//| Thread control                  | Monitor + Lock           |
//
//---
//
//# 💎 Even More Senior Insight
//
//Real-world pools also handle:
//
//* Connection timeout
//* Leak detection
//* Validation queries
//* Idle timeout cleanup
//* Eviction policy
//
//If interviewer pushes further — bring these up.
//
//---
//
//If you want next, I can:
//
//* Show a version using Semaphore instead of Condition (very clean design)
//* Add timeout support
//* Or turn this into production-grade with try-with-resources support
//
//Your system design thinking is solid.

// Excellent question 👏
//If you’re using `ReentrantLock`, you **must** understand `Condition`. This is core Java concurrency.
//
//Let’s break it down clearly.
//
//---
//
//# 🔷 What is `Condition`?
//
//`Condition` is basically:
//
//> The advanced replacement for `wait()` and `notify()` when using `ReentrantLock`.
//
//If:
//
//* `synchronized` → you use `wait()` / `notify()`
//* `ReentrantLock` → you use `Condition.await()` / `signal()`
//
//---
//
//# 🔥 Why Do We Need It?
//
//In your connection pool:
//
//When:
//
//* Pool is full
//* No free connection available
//
//Thread must:
//
//* WAIT until someone releases a connection
//
//That waiting mechanism is handled by `Condition`.
//
//---
//
//# 🔷 Simple Analogy
//
//Think of:
//
//* `ReentrantLock` → Door lock
//* `Condition` → Waiting room
//
//Thread:
//
//1. Acquires lock
//2. Checks condition
//3. If condition not satisfied → goes to waiting room
//4. When someone signals → wakes up and rechecks
//
//---
//
//# 🔷 How It Works Internally
//
//### Create Condition
//
//```java
//ReentrantLock lock = new ReentrantLock();
//Condition condition = lock.newCondition();
//```
//
//---
//
//### Thread waits
//
//```java
//condition.await();
//```
//
//What happens:
//
//1. Thread releases the lock
//2. Thread goes to waiting state
//3. It sleeps
//
//---
//
//### Another thread signals
//
//```java
//condition.signal();
//```
//
//What happens:
//
//1. One waiting thread is woken up
//2. It competes to acquire the lock again
//3. Once lock acquired → continues execution
//
//---
//
//# 🔥 Important Rule
//
//You MUST call:
//
//* `await()`
//* `signal()`
//
//Inside `lock.lock()` block.
//
//Otherwise → IllegalMonitorStateException.
//
//---
//
//# 🔷 Why Not Just Use if Instead of while?
//
//In pool code we wrote:
//
//```java
//while (availableConnections.isEmpty()) {
//    connectionAvailable.await();
//}
//```
//
//Why `while` and not `if`?
//
//Because of:
//
//### 🔥 Spurious Wakeups
//
//Sometimes threads wake up without signal.
//
//So best practice:
//
//Always recheck condition inside while loop.
//
//---
//
//# 🔷 Mapping to Old synchronized Style
//
//### Old Way:
//
//```java
//synchronized(lock) {
//    while(conditionNotMet) {
//        lock.wait();
//    }
//}
//```
//
//### New Way:
//
//```java
//lock.lock();
//try {
//    while(conditionNotMet) {
//        condition.await();
//    }
//} finally {
//    lock.unlock();
//}
//```
//
//---
//
//# 🔷 In Your DB Pool
//
//When pool is full:
//
//```java
//while (availableConnections.isEmpty()) {
//    connectionAvailable.await();
//}
//```
//
//Thread:
//
//* Releases lock
//* Sleeps
//* Wakes up when someone calls `signal()`
//
//When connection released:
//
//```java
//connectionAvailable.signal();
//```
//
//One waiting thread wakes up.
//
//---
//
//# 🔥 signal() vs signalAll()
//
//* `signal()` → wake ONE thread
//* `signalAll()` → wake ALL waiting threads
//
//In connection pool:
//
//Usually `signal()` is enough.
//
//---
//
//# 🔷 Why Condition is Better Than wait/notify?
//
//Because:
//
//1. You can create multiple conditions per lock
//2. Cleaner API
//3. More control
//4. Avoid intrinsic monitor limitations
//
//Example:
//
//```java
//Condition notFull;
//Condition notEmpty;
//```
//
//Used in BlockingQueue implementations.
//
//---
//
//# 🎯 Simple Real-Life Example
//
//Imagine:
//
//Pool size = 2
//3 threads request connection
//
//Thread 1 → gets
//Thread 2 → gets
//Thread 3 → waits (await())
//
//When Thread 1 calls close() → signal()
//
//Thread 3 wakes up → gets connection.
//
//---
//
//# 🧠 Interview-Level Explanation
//
//If interviewer asks:
//
//> What is Condition?
//
//You say:
//
//> Condition is a synchronization mechanism used with ReentrantLock that allows threads to wait and be notified, similar to wait() and notify() in synchronized blocks. It enables finer-grained thread coordination and supports multiple wait-sets per lock.
//
//That’s a solid answer.
//
//---
//
//# 💡 When Would You NOT Use Condition?
//
//If you can simply use:
//
//* `BlockingQueue`
//* `Semaphore`
//
//Those are often simpler and safer.
//
//In fact, production pools often use:
//
//👉 `Semaphore` to control max connections
//👉 `BlockingQueue` for available connections
//
//Cleaner than manual Condition handling.
//
//---
//
//If you want next, I can:
//
//* Compare Condition vs Semaphore (very common interview trap)
//* Rewrite your pool using Semaphore (cleaner design)
//* Or show internal flow diagram of thread states
//
//What do you want?
public class Question14 {
}

// Excellent 👌 this is fundamental Java concurrency — and interviewers love this question.
//
//Let’s break it clearly.
//
//---
//
//# 🔷 What Happens When `synchronized` is on a Method?
//
//Example:
//
//```java
//public synchronized void doWork() {
//    // critical section
//}
//```
//
//This is equivalent to:
//
//```java
//public void doWork() {
//    synchronized(this) {
//        // critical section
//    }
//}
//```
//
//So:
//
//👉 It locks on the **current object (`this`)**
//
//---
//
//# 🔥 Now Your Question:
//
//> If that method is already locked by another thread, what happens?
//
//Answer:
//
//### 👉 The second thread BLOCKS.
//
//It goes into **BLOCKED state** until:
//
//* The first thread exits the method
//* The lock is released
//
//Then JVM picks one waiting thread and gives it the lock.
//
//---
//
//# 🔷 Example
//
//```java
//class Test {
//
//    public synchronized void print() {
//        System.out.println(Thread.currentThread().getName() + " entered");
//        try { Thread.sleep(3000); } catch (Exception e) {}
//        System.out.println(Thread.currentThread().getName() + " exiting");
//    }
//}
//```
//
//If:
//
//```java
//Test t = new Test();
//
//Thread 1 → t.print()
//Thread 2 → t.print()
//```
//
//Execution:
//
//1. Thread 1 enters
//2. Thread 2 becomes BLOCKED
//3. Thread 1 exits
//4. Thread 2 enters
//
//---
//
//# 🔷 Important: Lock Is On Object
//
//If you do:
//
//```java
//Test t1 = new Test();
//Test t2 = new Test();
//```
//
//Then:
//
//```java
//Thread 1 → t1.print()
//Thread 2 → t2.print()
//```
//
//No blocking.
//
//Because:
//
//* Different objects
//* Different locks
//
//---
//
//# 🔷 Static synchronized Method
//
//```java
//public static synchronized void doWork()
//```
//
//Equivalent to:
//
//```java
//synchronized(Test.class)
//```
//
//This locks on the **Class object**, not instance.
//
//So:
//
//* All instances share the same lock.
//
//---
//
//# 🔥 What State Does Thread Go Into?
//
//When waiting for synchronized lock:
//
//👉 Thread state = **BLOCKED**
//
//Important distinction:
//
//* `BLOCKED` → waiting to acquire monitor lock
//* `WAITING` → waiting via wait()
//* `TIMED_WAITING` → sleep(), wait(timeout)
//
//Interviewers test this difference.
//
//---
//
//# 🔷 Is It FIFO?
//
//No.
//
//Java does NOT guarantee fairness.
//
//Thread scheduling is JVM dependent.
//
//---
//
//# 🔷 Is synchronized Reentrant?
//
//Yes.
//
//If a thread already holds the lock, it can enter again.
//
//Example:
//
//```java
//public synchronized void methodA() {
//    methodB();
//}
//
//public synchronized void methodB() {
//}
//```
//
//No deadlock.
//
//Because same thread re-enters lock.
//
//---
//
//# 🔥 What If Thread Dies?
//
//If thread exits method (normally or via exception):
//
//Lock is automatically released.
//
//Unlike ReentrantLock, you don’t manually unlock.
//
//---
//
//# 🔷 What If Deadlock Happens?
//
//Example:
//
//Thread 1 locks A
//Thread 2 locks B
//Thread 1 tries B
//Thread 2 tries A
//
//→ Deadlock.
//
//synchronized does NOT prevent deadlocks.
//
//---
//
//# 🎯 Interview-Level Summary
//
//If asked:
//
//> What happens if synchronized method is already locked?
//
//You say:
//
//> The second thread attempting to enter the synchronized method will move to BLOCKED state and wait until the monitor lock is released by the first thread. Once released, one of the blocked threads acquires the lock and proceeds.
//
//Clean. Precise. Senior-level.
//
//---
//
//# 💡 Extra Insight (6-Year Level)
//
//Difference from ReentrantLock:
//
//| synchronized           | ReentrantLock               |
//| ---------------------- | --------------------------- |
//| Automatic unlock       | Manual unlock required      |
//| No tryLock()           | Has tryLock()               |
//| No fairness option     | Can enable fairness         |
//| No multiple conditions | Supports multiple Condition |
//
//---
//
//If you want next, I can:
//
//* Show internal JVM monitor working (object header, monitor, etc.)
//* Explain BLOCKED vs WAITING deeply
//* Or explain how deadlock detection works
//
//You’re asking exactly the right concurrency questions 👍