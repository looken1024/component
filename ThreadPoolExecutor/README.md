# ThreadPoolExecutor

### 参数：

corePoolSize: 核心线程，来一个任务起一个线程，如果满了就放到workQueue

maximumPoolSize：当workQueue满了，会起额外的线程，但是核心线程+额外的线程不能超过maxmumPoolSize

keepAliveTime：任务处理完了以后，额外的线程需要回收，过多久以后来回收

unit：上面时间的单位

workQueue：阻塞队列，用来存储暂时处理不过来的任务

threadFactory：创建线程，代表怎么创建线程，线程属性

handler：当队列和线程都满了拒绝策略

