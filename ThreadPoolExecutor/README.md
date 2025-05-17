# ThreadPoolExecutor

## 什么是线程池？

（首先要理解什么是线程）

线程池，thread pool，是一种线程使用模式，线程池维护着多个线程，等待着监督管理者分配可并发执行的任务。

通俗来说，就是可管理和维护以及分配线程的“池子”。

## 为什么使用线程池？

为了减少创建和销毁线程的次数，让每个线程都可以多次的使用，可以根据系统情况调整线程的数量，防止消耗过多内存。在实际使用中，服务器在创建和销毁线程上花费的时间和消耗的系统资源都相当大，使用线程池就可以优化。

线程池主要用来解决线程生命周期开销问题和资源不足问题。通过对多个任务重复使用线程，线程创建的开销就被分摊到了多个任务上了，而且由于在请求到达时线程已经存在，所以消除了线程创建所带来的延迟。这样，就可以立即为请求服务，使用应用程序响应更快。另外，通过适当的调整线程中的线程数目可以防止出现资源不足的情况。

通俗来说，就是为了优化线程的内存开销。

## 线程池的核心参数

```java
public ThreadPoolExecutor(int corePoolSize,//核心线程数
                          int maximumPoolSize,//最大线程数
                          long keepAliveTime,//线程空闲时间
                          TimeUnit unit,//时间单位
                          BlockingQueue<Runnable> workQueue,//任务队列
                          ThreadFactory threadFactory,//线程工厂
                          RejectedExecutionHandler handler//拒绝策略) 
{
    ...
}
```

### 参数：

(a) corePoolSize: 核心线程，来一个任务起一个线程，如果满了就放到workQueue

核心线程数，默认为1。

设置规则：

CPU密集型(CPU密集型也叫计算密集型，指的是运算较多，cpu占用高，读/写I/O(硬盘/内存)较少)：corePoolSize = CPU核数 + 1

IO密集型（与cpu密集型相反，系统运作，大部分的状况是CPU在等I/O (硬盘/内存) 的读/写操作，此时CPU Loading并不高。）：corePoolSize = CPU核数 * 2

(b) maximumPoolSize：当workQueue满了，会起额外的线程，但是核心线程+额外的线程不能超过maxmumPoolSize

最大线程数，默认为Integer.MAX_VALUE

一般设置为和核心线程数一样

(c) keepAliveTime：任务处理完了以后，额外的线程需要回收，过多久以后来回收

线程空闲时间，默认为60s，一般设置为默认60s

(d) unit：上面时间的单位

时间单位，默认为秒

(e) workQueue：阻塞队列，用来存储暂时处理不过来的任务

队列，当线程数目超过核心线程数时用于保存任务的队列。（BlockingQueue workQueue）此队列仅保存实现Runnable接口的任务。（因为线程池的底层BlockingQueue的泛型为Runnable）

（1）无界队列

队列大小无限制，常用的为无界的LinkedBlockingQueue，使用该队列作为阻塞队列时要尤其当心，当任务耗时较长时可能会导致大量新任务在队列中堆积最终导致OOM。阅读代码发现，Executors.newFixedThreadPool 采用就是 LinkedBlockingQueue，而博主踩到的就是这个坑，当QPS很高，发送数据很大，大量的任务被添加到这个无界LinkedBlockingQueue 中，导致cpu和内存飙升服务器挂掉。

当然这种队列，maximumPoolSize 的值也就无效了。当每个任务完全独立于其他任务，即任务执行互不影响时，适合于使用无界队列；例如，在 Web 页服务器中。这种排队可用于处理瞬态突发请求，当命令以超过队列所能处理的平均数连续到达时，此策略允许无界线程具有增长的可能性。

（2）有界队列

当使用有限的 maximumPoolSizes 时，有界队列有助于防止资源耗尽，但是可能较难调整和控制。常用的有两类，一类是遵循FIFO原则的队列如ArrayBlockingQueue，另一类是优先级队列如PriorityBlockingQueue。PriorityBlockingQueue中的优先级由任务的Comparator决定。

使用有界队列时队列大小需和线程池大小互相配合，线程池较小有界队列较大时可减少内存消耗，降低cpu使用率和上下文切换，但是可能会限制系统吞吐量。

（3）同步移交队列

如果不希望任务在队列中等待而是希望将任务直接移交给工作线程，可使用SynchronousQueue作为等待队列。SynchronousQueue不是一个真正的队列，而是一种线程之间移交的机制。要将一个元素放入SynchronousQueue中，必须有另一个线程正在等待接收这个元素。只有在使用无界线程池或者有饱和策略时才建议使用该队列。

(f) threadFactory：创建线程，代表怎么创建线程，线程属性

线程工厂，用来创建线程。

为了统一在创建线程时设置一些参数，如是否守护线程，线程一些特性等，如优先级。通过这个TreadFactory创建出来的线程能保证有相同的特性。

它是一个接口类，而且方法只有一个，就是创建一个线程。

如果没有另外说明，则在同一个ThreadGroup 中一律使用Executors.defaultThreadFactory() 创建线程，并且这些线程具有相同的NORM_PRIORITY 优先级和非守护进程状态。

通过提供不同的 ThreadFactory，可以改变线程的名称、线程组、优先级、守护进程状态，等等。

如果从newThread 返回 null 时ThreadFactory 未能创建线程，则执行程序将继续运行，但不能执行任何任务。

(g) handler：当队列和线程都满了拒绝策略

拒绝策略，默认是AbortPolicy，会抛出异常。

当线程数已经达到maxPoolSize，且队列已满，会拒绝新任务。

当线程池被调用shutdown()后，会等待线程池里的任务执行完毕再shutdown。如果在调用shutdown()和线程池真正shutdown之间提交任务，会拒绝新任务。

AbortPolicy 丢弃任务，抛运行时异常。

CallerRunsPolicy 由当前调用的任务线程执行任务。

DiscardPolicy 忽视，什么都不会发生。

DiscardOldestPolicy 从队列中踢出最先进入队列（最后一个执行）的任务。

### 参考：

https://www.jianshu.com/p/19c29960fc9d#comments

## 线程池的执行顺序

线程池按以下行为执行任务：

当线程数小于核心线程数时，创建线程。

当线程数大于等于核心线程数，且任务队列未满时，将任务放入任务队列。

当线程数大于等于核心线程数，且任务队列已满，若线程数小于最大线程数，创建线程。

若线程数等于最大线程数，则执行拒绝策略。

## jdk自带的四种线程池创建方式

```java
// 第一种线程池:固定个数的线程池,可以为每个CPU核绑定一定数量的线程数
ExecutorService fixedThreadPool = Executors.newFixedThreadPool(processors * 2);
// 缓存线程池，无上限
ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
// 单一线程池,永远会维护存在一条线程
ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
// 固定个数的线程池，可以执行延时任务，也可以执行带有返回值的任务。
ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
```

## 调用线程池的线程案例

```java
public List<AccountRecordVO> requestTest() throws ExecutionException, InterruptedException {
	List<String> ids = Lists.newArrayList();
	ids.add("1");
	ids.add("2");
	ids.add("3");
	ids.add("4");
	//有返回值的情况，定义接收返回值
	List<AccountRecordVO> futureList2 = Lists.newArrayList();
	//分布式计数器，若业务不需要则可以不定义
	CountDownLatch countDownLatch = new CountDownLatch(ids.size());
	for (String id : ids) {
		//调用线程池的线程执行任务
		threadPoolTaskExecutor.submit(new Runnable() {
			@Override
			public void run() {
				test(Lists.newArrayList(id),futureList2);
				//计数器-1
				countDownLatch.countDown();
			}
		});
	}
	//await阻塞，直到计数器为0
	countDownLatch.await();
	System.out.println("主线程"+"====");
	return futureList2;
}

public List<AccountRecordVO> test(List<String> ids, List<AccountRecordVO> list2){
	//随便写的业务逻辑代码，无实际意义，仅作演示
	System.out.println("线程体" + "====");
	List<AccountRecordVO> accountRecordVOS = Lists.newArrayList();
	int i = 0;
	AccountRecordVO accountRecordVO = new AccountRecordVO();
	accountRecordVO.setUserId("123");
	accountRecordVO.setAmount(12333);
	for (String id : ids){
		accountRecordVOS.add(accountRecordVO);
		list2.add(accountRecordVO);
	}
	try{
		Thread.sleep(Long.valueOf("1000"));
	}catch (Exception e){
		log.error(e.getMessage());
	}
	System.out.println("线程体结束" + "====");
	return accountRecordVOS;
}
```




