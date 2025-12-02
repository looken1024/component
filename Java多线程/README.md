## 如何确保三个线程按顺序执行？

解决方案一：Join方法实现

```java
Thread t1 = new Thread(() -> {
    System.out.println("T1执行");
});

Thread t2 = new Thread(() -> {
    try {
        t1.join();
        System.out.println("T2执行");
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
});

Thread t3 = new Thread(() -> {
    try {
        t2.join();
        System.out.println("T3执行");
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
});

t1.start();
t2.start();
t3.start();

```

解决方案二：使用CountDownLatch实现

使用计数器机制实现一次性的线程同步。每个线程完成后调用countDown()，后续线程通过await()等待计数归零。这种方法特别适合一次性的等待场景，代码结构清晰且易于理解。

```java
CountDownLatch latch1 = new CountDownLatch(1);
CountDownLatch latch2 = new CountDownLatch(1);

Thread t1 = new Thread(() -> {
    System.out.println("T1执行");
    latch1.countDown(); //T1完成后释放，通知t2可以执行
});

Thread t2 = new Thread(() -> {
    try {
        latch1.await();
        System.out.println("T2执行");
        latch2.countDown(); //T2执行完成后释放，通知T3可以执行
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
});

Thread t3 = new Thread(() -> {
    try {
        latch2.await();
        System.out.println("T3执行");
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
});

t1.start();
t2.start();
t3.start();

```

解决方案三：Semaphore实现

使用信号量控制线程执行顺序，通过初始许可数的设置，精确控制线程的执行顺序，这种方式适合需要精确控制并发访问资源数量的场景，实现了更灵活的线程协调。

```java
Semaphore s1 = new Semaphore(1);
Semaphore s2 = new Semaphore(0);
Semaphore s3 = new Semaphore(0);

Thread t1 = new Thread(() -> {
    try {
        s1.acquire();
        System.out.println("T1执行");
        s2.release();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
});

Thread t2 = new Thread(() -> {
    try {
        s2.acquire();
        System.out.println("T2执行");
        s3.release();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
});

Thread t3 = new Thread(() -> {
    try {
        s3.acquire();
        System.out.println("T3执行");
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
});

t1.start();
t2.start();
t3.start();

```





