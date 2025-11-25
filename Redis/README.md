# Redis

## 击穿、雪崩和穿透

1、缓存击穿：高并发时，当一个key非常热点（类似于爆款），在不停的扛着大并发，当这个key在失效的瞬间，持续的大并发就击穿缓存，直接请求数据库并设置到缓存中，导致性能下降

解决方案：a）永不过期；b）加锁排队(首次请求从数据库查得后写入redis，其他请求从redis即可获得数据，减少了数据库的压力)；

2、缓存雪崩：缓存集中过期，或者缓存服务器宕机，导致大量请求访问数据库，造成数据库瞬间压力过大、宕机

解决方案：a）加锁排队；b）随机失效时间；c）redis高可用（哨兵、灾备）；

3、缓存穿透：数据库不存在缓存中也不存在，导致每次请求都会去查数据库，这时的用户很可能是攻击者，如发起id=-1的数据或id为特别大（不存在的数据），导致数据库压力过大或宕机

解决方案：a）参数校验；b）缓存空对象；c）布隆过滤器；

## 如何快速判断几十亿个数中是否存在某个数？

使用位图！

在redis-cli中设置位图

```shell
62.234.20.62:8888> setbit test_bit 123 1
(integer) 0
```

pom.xml

```shell
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>untitled</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>22</maven.compiler.source>
        <maven.compiler.target>22</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson</artifactId>
            <version>3.16.4</version> <!-- 使用最新稳定版本 -->
        </dependency>
    </dependencies>
</project>
```

TestBit.java

```java
import org.redisson.Redisson;
import org.redisson.api.RBitSet;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class TestBit {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://62.234.20.62:8888")
                .setPassword("*************"); // 如果有密码

        RedissonClient redissonClient = Redisson.create(config);
        RBitSet bitSet = redissonClient.getBitSet("test_bit");
        if (bitSet.get(123)) {
            System.out.println("123 exists");
        }
        else {
            System.out.println("123 does not exist");
        }
    }
}
```


## RDB和AOF持久化

在服务器宕机或断电的情况下，内存中的数据会丢失。为了解决这个问题，Redis提供了持久化机制来确保数据的持久性和可靠性。

### 1、RDB持久化

在指定的时间间隔内将内存中的数据集快照写入磁盘，RDB是内存快照（内存数据的二进制序列化形式）的方式持久化，每次都是从redis生成一个快照进行数据的全量备份。

#### RDB持久化流程：

1）子进程：a）fork子进程备份；b）将共享内存数据写入临时RDB文件；c）完成临时文件写入，替换旧RDB文件，子进程退出；

2）主进程：a）继续处理客户端请求；b）处理新的写操作(COW机制)；

#### RDB触发规则：

1）手动触发：

save：阻塞当前redis进程，直到RDB持久化过程完成，如果内存实例比较大会造成长时间阻塞，尽量不要使用这种方式；

bgsave：redis主进程fork创建子进程，由子进程完成持久化，阻塞时间很短（微秒级）

2）自动触发：

配置触发：在redis安装目录下的redis.conf配置文件中搜索/snapshot即可快速定位。通过配置规则来触发RDB的持久化，需要开启或者根据自己的需求按照规则来配置。

```shell
save 300 100：-- 300秒内有100个key被修改，触发RDB
```

shutdown触发：在客户端执行shutdown即可。

flushall触发：清空redis数据的同时清空dump.rdb文件。


#### 优点：

性能高：RDB持久化是通过生成一个快照文件来保存数据，因此在恢复数据时速度非常快。

文件紧凑：RDB文件是二进制格式的数据库文件，相对于AOF文件来说，文件体积较小。

#### 缺点：

可能会丢失数据，由于RDB是定期生成的快照文件，如果redis意外宕机，最近一次的修改可能会丢失。

redis持久化默认开启为RDB持久化。


### 2、AOF持久化

AOF持久化需要手动修改conf配置开启。

#### AOF持久化流程：

1）客户端写请求 -> AOF缓冲区 -> AOF日志文件（AOF日志重写）

2）启动时读取AOF日志文件恢复数据


AOF持久化方案进行备份时，客户端所有请求的命令都会被追加到AOF缓冲区中，缓冲区中的数据会根据redis配置文件中配置的同步策略来同步到磁盘上的AOF文件中，同时当AOF的文件达到重写策略配置的阈值时，redis会对AOF日志文件进行重写，给AOF日志文件瘦身，redis服务重启的时候，通过加载AOF日志文件来恢复数据。

#### AOF配置：

AOF默认不开启，默认为appendonly no，开启则需要修改为appendonly yes

```shell
appendonly yes
```

关闭AOF+RDB混合模式，设为no：

```shell
aof-use-rdb-preamble no
```

#### AOF同步策略：

```shell
# appendfsync always
appendfsyc everysec
# aooendfsync no
```

appendfsync always: 每次redis写操作，都写入AOF日志，非常耗性能的。

appendfsync everysec：每秒刷新一次缓冲区的数据到AOF文件，这个redis配置文件中默认的策略，兼容了性能和数据完整性的折中方案，这种配置，理论上丢失的数据在一秒钟左右。

appendfsync no：redis进程不会主动的去刷新缓冲区中的数据到AOF文件中，而是直接交给操作系统去判断，这种操作也是不推荐的，丢失数据的可能性非常大。


#### AOF重写

重写其实是针对AOF存储的重复性冗余指令进行整理，比如有些key反复修改，又或者key反复修改后最终被删除，这些过程中的指令都是冗余且不需要存储的。

自动重写：当AOF日志文件达到阈值时会触发自动重写。

重写阈值配置：

auto-aof-rewrite-percentage 100：当AOF文件体积达到上次重写之后的体积的100%时，会触发AOF重写；

auto-aof-rewrite-min-size 64mb：当AOF文件体积超过这个阈值时，会触发AOF重写。

当AOF文件的体积达到或超过上次重写之后的比例，并且超过了最小体积阈值时，redis会自动触发AOF重写操作，生成一个新的AOF文件。

手动重写：bgrewriteaof

#### 优点：

1）数据更加可靠：AOF持久化记录了每个写命令的操作，因此在出现故障时，可以通过重新执行AOF文件来保证数据的完整性;

2）可以保留写命令历史：AOF文件是一个追加日志文件，可以用于回放过去的写操作。

#### 缺点：

1）文件较大：由于记录了每个写命令，AOF文件体积通常比RDB文件要大；

2）恢复速度较慢：当AOF文件较大时，redis重写时需要重新执行整个AOF文件，恢复速度相对较慢。


### 3、混合持久化

推荐两者均开启；

如果对数据不敏感，可以选单独用RDB；

不建议单独用AOF，因为可能会出现bug；

如果只是做纯内存缓存，可以都不用。


## 什么是redis哨兵机制？

主从架构：master（实时读写）、slave（实时同步，实时读）

当多数的哨兵节点都认为master故障，才会做主从切换（自动化）。

哨兵节点也会进行选举，少数服从多数。


## redis的过期策略和内存淘汰策略

### 过期策略

1）惰性删除

当客户端尝试访问某个键时，redis会先检查该键是否设置了过期时间，并判断是否过期；

如果键已过期，则redis会立即将其删除。

该策略可以最大化节省cpu资源，却对内存非常不友好，极端情况可能出现大量的过期key没有再次被访问，从而不会被清除，占用大量内存。

2）定期删除

redis会每隔一段时间（默认100ms）随机检查一部分设置了过期时间的键。

定期过期策略通过使用循环遍历方式，逐个检查键是否过期，并删除已过期的键值对。

通过调整定时扫描的时间间隔和每次扫描的限定耗时，可以在不同情况下使得cpu和内存资源达到最优的平衡效果。

redis中同时使用了惰性删除和定期过期两种过期策略。

如果定期删除漏掉了很多过期的key，然后也没有走惰性删除，就会有很多过期的key积在内存中，可能会导致内存溢出，或者是业务量太大，内存不够用然后溢出了，为了应对这个问题，redis引出了内存淘汰策略进行优化。

### 内存淘汰策略

内存淘汰策略允许redis在内存资源紧张时，根据一定的策略主动删除一些键值对，以释放内存空间并保持系统的稳定性。

1）noeviction（不淘汰策略）

当内存不足以容纳新写入数据时，redis将新写入的命令返回错误，这个策略确保数据的完整性，但会导致写入操作失败。

2）volatile-lru（最近最少使用）

从设置了过期时间的键中选择最少使用的键进行删除。该策略优先删除最久未被访问的键，保留最常用的键。

3）volatile-ttl（根据过期时间优先）

从设置了过期时间的键中选择剩余时间最短的键进行删除，该策略优先删除剩余时间较短的键，以尽量保留剩余时间更长的键。

4）volatile-random（随机删除）

从设置了过期时间的键中随机选择一个键进行删除，

5）allkeys-lru（全局最近最少使用）

从所有键中选择最少使用的键进行删除，无论键是否设置了过期时间，都将参与淘汰。

6）allkeys-random（全局随机删除）

从所有键中随机选择一个键进行删除。


## 为什么Redis这么快？

纯内存操作 + 线程模型

IO多路复用

队列

文件事件分派器 -> 连接应答处理器 + 命令请求处理器 + 命令回复处理器




